import cats.effect.{IO, Resource}
import entity.MinMax
import routing.ReqRoutes
import service.CovidExtendedApiService
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.client.JavaNetClientBuilder
import org.http4s.implicits.*

import java.time.ZonedDateTime

class CovidDataSpec extends CatsEffectSuite:
  val client = JavaNetClientBuilder[IO].create
  val service = CovidExtendedApiService.impl[IO](client)
  val routes = ReqRoutes.covidCasesRoutes(service)

  private def returnMinMaxCases(country: String, from: String, to: String): IO[Response[IO]] = {
    val uri = uri"/covidextendedapi" / "country" / country +? ("from", from) +? ("to", to)
    val getMinMaxCasesRequest = Request[IO](Method.GET, uri)

    routes.orNotFound(getMinMaxCasesRequest)
  }

  test("Min / max cases without provinces") {
    val expected = MinMax("Netherlands",
      904, ZonedDateTime.parse("2020-04-03T00:00:00Z"),
      1224, ZonedDateTime.parse("2020-04-04T00:00:00Z"))

    val response = returnMinMaxCases("netherlands", "2020-04-01", "2020-04-05")

    assertIO(response.flatMap(_.as[MinMax]), expected)
  }

  test("Min / max cases") {
    val expected = MinMax("Germany",
      679, ZonedDateTime.parse("2020-05-04T00:00:00Z"),
      945, ZonedDateTime.parse("2020-05-02T00:00:00Z"))

    val response = returnMinMaxCases("germany", "2020-05-01", "2020-05-05")

    assertIO(response.flatMap(_.as[MinMax]), expected)
  }
