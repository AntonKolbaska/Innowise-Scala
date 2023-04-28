import cats.effect.Async
import cats.syntax.all.*
import com.comcast.ip4s.*
import routing.ReqRoutes
import service.CovidExtendedApiService
import org.http4s.Uri
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Origin
import org.http4s.implicits.*
import org.http4s.server.middleware.{CORS, Logger}

object CovidApiServer:

  def run[F[_] : Async]: F[Nothing] = {
    for {
      client <- EmberClientBuilder.default[F].build
      covidCasesAlg = CovidExtendedApiService.impl[F](client)

      httpApp = ReqRoutes.covidCasesRoutes[F](covidCasesAlg).orNotFound

      loggerHttpApp = Logger.httpApp(true, true)(httpApp)

      corsHttpApp = CORS.policy
        .withAllowOriginHost(Set(
          Origin.Host(Uri.Scheme.http, Uri.RegName("localhost"), Some(4200))
        ))
        .withAllowCredentials(false)
        .httpApp(loggerHttpApp)

      _ <-
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(corsHttpApp)
          .build
    } yield ()
  }.useForever
