package entity

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

import java.time.ZonedDateTime

case class MinMax(country: String, minCases: Int, minCasesDate: ZonedDateTime,
                  maxCases: Int, maxCasesDate: ZonedDateTime)

object MinMax:
  //  DE-ENCODERS:

  //  JSON -> MinMax
  given Decoder[MinMax] = Decoder.derived[MinMax]

  //  JSON -> MinMax (HTTPreq body, async)
  given[F[_] : Concurrent]: EntityDecoder[F, MinMax] = jsonOf

  //  MinMax -> JSON
  given Encoder[MinMax] = Encoder.AsObject.derived[MinMax]

  //  MinMax -> JSON (HTTPreq body)
  given[F[_]]: EntityEncoder[F, MinMax] = jsonEncoderOf
