package routing

import cats.effect.{Concurrent, Sync}
import cats.implicits.*
import entity.CovidData
import service.CovidExtendedApiService
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher

object ReqRoutes:

  private object FromDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("from")

  private object ToDateQueryParamMatcher extends QueryParamDecoderMatcher[String]("to")

  def covidCasesRoutes[F[_] : Sync](covidApiService: CovidExtendedApiService[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "covidextendedapi" / "countries" =>
        for {
          countryList <- covidApiService.getCountryList
          resp <- Ok(countryList)
        } yield resp
      case GET -> Root / "covidextendedapi" / "country" / country :?
        FromDateQueryParamMatcher(from) +& ToDateQueryParamMatcher(to) =>
        for {
          covidCases <- covidApiService.getMinMaxCases(country, from, to)
          resp <- Ok(covidCases)
        } yield resp
    }
