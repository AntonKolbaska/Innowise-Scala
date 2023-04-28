import cats.effect.{Concurrent, IO, Resource}
import entity.{Country, MinMax}
import routing.ReqRoutes
import service.CovidExtendedApiService
import io.circe.{Decoder, Encoder}
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits.*

class CountriesSpec extends CatsEffectSuite {
  val client = JavaNetClientBuilder[IO].create
  val service = CovidExtendedApiService.impl[IO](client)
  val route = ReqRoutes.covidCasesRoutes(service)


  private def returnCountryList(): IO[Response[IO]] = {
    val getMinMaxCasesRequest = Request[IO](Method.GET, uri"/covidextendedapi/countries")
    route.orNotFound(getMinMaxCasesRequest)
  }

  given Decoder[Country] = Decoder.derived[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, Country] = jsonOf

  given Decoder[List[Country]] = Decoder.decodeList[Country]

  given[F[_] : Concurrent]: EntityDecoder[F, List[Country]] = jsonOf

  test("Accessible") {
    val response = returnCountryList()
    assertIO(response.map(_.status), Status.Ok)
  }

  test("Accessible countries number"){
    val response = returnCountryList()
    assertIO(response.flatMap(_.as[List[Country]]).map(_.length), 248)
  }


}