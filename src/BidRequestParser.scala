import io.circe._
import io.circe.parser._

case class BidRequest(id: String, tmax: Int, imp: List[Impression], device: Device)
case class Impression(id: String, bidfloor: Float)
case class Device(ua: String, os: String, ip: String)


class BidRequestParser {
  def parseBidRequest(jsonString: String): Either[ParsingFailure, BidRequest] = {
    val json = parser.parse(jsonString)

    val cursor = json.toOption.get.hcursor
    val id = cursor.downField("id").as[String].toOption.getOrElse("")
    val tmax = cursor.downField("tmax").as[Int].toOption.getOrElse(0)
    val imp = cursor.downField("imp").as[List[Json]].toOption.getOrElse(Nil).flatMap(parseImpression)
    val device = parseDevice(cursor.downField("device"))

    if (id.nonEmpty && imp.nonEmpty) {
      Right(BidRequest(id, tmax, imp, device))
    } else {
      Left(ParsingFailure("Data validation failed", new Exception("Invalid bid request data")))
    }
  }


  private def parseDevice(cursor: ACursor): Device = {
    val ua = cursor.downField("ua").as[String].toOption.getOrElse("")
    val os = cursor.downField("os").as[String].toOption.getOrElse("")
    val ip = cursor.downField("ip").as[String].toOption.getOrElse("")

    Device(ua, os, ip)
  }

  private def parseImpression(json: Json): Option[Impression] = {
    val cursor = json.hcursor
    val id = cursor.downField("id").as[String].toOption
    val bidfloor =  cursor.downField("bidfloor").as[Float].toOption

    if (id.isDefined) {
      Some(Impression(id.get, bidfloor.getOrElse(0)))
    }else{
      None
    }
  }
}
