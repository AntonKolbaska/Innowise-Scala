package entity

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.{Decoder, DecodingFailure, Encoder}
import org.http4s.*
import org.http4s.circe.*

case class Country(name: String, slug: String, iso2: String)

object Country:
  //  DE-ENCODERS:
  
  // JSON -> Country
  given Decoder[Country] = Decoder.instance { h =>
    for {
      name <- h.get[String]("Country")
      slug <- h.get[String]("Slug")
      iso2 <- h.get[String]("ISO2")
    } yield Country(name, slug, iso2)
  }
  // JSON -> Country (async)
  given[F[_] : Concurrent]: EntityDecoder[F, Country] = jsonOf

  // Country -> JSON
  given Encoder[Country] = Encoder.AsObject.derived[Country]

  // Country -> JSON (HTTPreq body)
  given[F[_]]: EntityEncoder[F, Country] = jsonEncoderOf



  // JSON list -> Country list
  given Decoder[List[Country]] = Decoder.decodeList[Country]

  // JSON list -> Country list (HTTPreq body, async)
  given[F[_] : Concurrent]: EntityDecoder[F, List[Country]] = jsonOf

  // Country list -> JSON list
  given Encoder[List[Country]] = Encoder.encodeList[Country]

  // Country list -> JSON list (HTTPreq body)
  given[F[_]]: EntityEncoder[F, List[Country]] = jsonEncoderOf
