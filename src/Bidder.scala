import akka.actor.Actor
import io.circe.Json
import io.circe.syntax._

////////////////////////////////
// class Bidder
// simulates the work of a bidder entity:
// upon receiving a bid request, bidder parses it,
// and builds a bid response using original request id
// and information on prospect impressions
// Should we fail to parse bid request properly, the
// no-bid response will be sent as an empty JSON
class Bidder extends Actor {
  private val parser = new BidRequestParser()
  var bCount = 0    // so all the `bidid`s are unique

  override def receive: Receive = {
    case msg: String =>
      val result = parser.parseBidRequest(msg)
      result match {
        case Right(bidRequest) =>
          println(s"${self.path} received bid request with:\n    id=${bidRequest.id}\n    ua=${bidRequest.device.ua}")
          val bids = bidRequest.imp.map(im => {
            bCount += 1
            Json.fromFields(List(
                ("impid", Json.fromString(im.id)),
                ("id", Json.fromString(s"$bCount")),
                // setting bidding price a little bit about the floor value
                ("price", Json.fromFloatOrString(im.bidfloor + 0.1f))
              ))
            }
          ).asJson

          val bidResponseFieldList = List(("id", Json.fromString(bidRequest.id)),
                                          ("seatbid", Json.fromFields(List(("bids", bids)))))
          val jsonBidResponse =  Json.fromFields(bidResponseFieldList)
          sender() ! jsonBidResponse.spaces2

        case Left(failure) =>
          println(s"${self.path} received broken bid request: ${failure.getMessage}")
          sender() ! "{ }"

      }
    case _ =>
      println("Ill formed JSON message")
  }

}
