package service

import cats.effect.Concurrent
import cats.implicits.*
import entity.{Country, CovidData, MinMax}
import service.CovidExtendedApiService
import io.circe.{Decoder, Encoder}
import org.http4s
import org.http4s.*
import org.http4s.Method.*
import org.http4s.UriTemplate.{ParamElm, PathElm}
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits.*

import java.time.ZonedDateTime


trait CovidExtendedApiService[F[_]]:
  def getCountryList: F[List[Country]]

  def getMinMaxCases(country: String, from: String, to: String): F[MinMax]



object CovidExtendedApiService:
  def apply[F[_]](implicit ev: CovidExtendedApiService[F]): CovidExtendedApiService[F] = ev

  //custom error
  private final case class CovidApiError(e: Throwable) extends RuntimeException

  private val baseUrl = uri"https://api.covid19api.com"

  def impl[F[_] : Concurrent](client: Client[F]): CovidExtendedApiService[F] = new CovidExtendedApiService[F]:
    val dsl = new Http4sClientDsl[F] {}

    import dsl.*

    override def getCountryList: F[List[Country]] = {
      client.expect[List[Country]](baseUrl / "countries")
        .adaptError { case t =>
          t.printStackTrace()
          CovidApiError(t)
        }
    }

    //  max/min
    override def getMinMaxCases(country: String, from: String, to: String): F[MinMax] = {
      for {
        covidCasesList <- getCovidCasesList(country, from, to)

        //  exclude provinces in order them not to influence to minMax calculations
        covidCasesListWithoutProvinces = covidCasesList.filter(_.province.isEmpty)

        minMaxCasesResponse = calculateMinMaxCases(covidCasesListWithoutProvinces)/*covidCasesList*/
      } yield minMaxCasesResponse
    }

    //  all cases
    private def getCovidCasesList(country: String, from: String, to: String): F[List[CovidData]] = {
      val uri = baseUrl / "country" / country / "status" / "confirmed" +?
        ("from", from) +? ("to", to)

      (for {
        covidCasesList <- client.expect[List[CovidData]](uri)
        covidCasesListWithoutProvinces = covidCasesList.filter(_.province.isEmpty)
      } yield covidCasesListWithoutProvinces  //covidCasesList
        ).adaptError { case t =>
        t.printStackTrace()
        CovidApiError(t)
      }
    }

    private def calculateMinMaxCases(covidCasesList: List[CovidData]): MinMax = {
      var minNewCases = Int.MaxValue
      var minNewCasesDate: ZonedDateTime = null

      var maxNewCases = Int.MinValue
      var maxNewCasesDate: ZonedDateTime = null

      for (i <- 1 until covidCasesList.length) {
        val newCases = covidCasesList(i).cases - covidCasesList(i - 1).cases

        if (newCases > maxNewCases) {
          maxNewCases = newCases
          maxNewCasesDate = covidCasesList(i).date
        }

        if (newCases < minNewCases) {
          minNewCases = newCases
          minNewCasesDate = covidCasesList(i).date
        }

      }

      MinMax(covidCasesList.head.country, minNewCases, minNewCasesDate, maxNewCases, maxNewCasesDate)
    }