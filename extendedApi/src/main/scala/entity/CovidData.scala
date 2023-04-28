package entity

import cats.effect.Concurrent
import cats.implicits.*
import entity.CovidData
import io.circe.{Decoder, Encoder}
import org.http4s.*
import org.http4s.circe.*

import java.time.ZonedDateTime

case class CovidData(country: String,
                     province: String,
                     cases: Int,
                     date: ZonedDateTime)

object CovidData:
  //  DE-ENCODERS:

  //  JSON -> CovData
  given Decoder[CovidData] = Decoder.instance { h =>
    for {
      country <- h.get[String]("Country")
      province <- h.get[String]("Province")
      cases <- h.get[Int]("Cases")
      date <- h.get[ZonedDateTime]("Date")
    } yield CovidData(country,
      province,
      cases, date)
  }

  //  JSON -> CovDat (async)
  given[F[_] : Concurrent]: EntityDecoder[F, CovidData] = jsonOf

  //  CovData -> JSON
  given Encoder[CovidData] = Encoder.AsObject.derived[CovidData]

  //  CovData -> JSON (HTTPreq body)
  given[F[_]]: EntityEncoder[F, CovidData] = jsonEncoderOf


  //  JSON list -> CovData list
  given Decoder[List[CovidData]] = Decoder.decodeList[CovidData]

  //  JSON list -> CovData list (HTTPreq body, async)
  given[F[_] : Concurrent]: EntityDecoder[F, List[CovidData]] = jsonOf

  //  CovData list -> JSON list
  given Encoder[List[CovidData]] = Encoder.encodeList[CovidData]

  //  CovData list -> JSON list (HTTPreq body)
  given[F[_]]: EntityEncoder[F, List[CovidData]] = jsonEncoderOf