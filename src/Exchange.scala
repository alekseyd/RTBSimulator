import akka.actor.{Actor, ActorRef}
import akka.util.Timeout
import akka.pattern.ask
import io.circe.parser

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Success}

/////////////////////////////////////////////////
// class Exchange -- emulates behavior of exchange entity
// To conform with Actor paradigm, we pretend that we got
// an ad request from an SSP by sending
// the prepared request from the main loop
// Exchange "knows" its bidders, so we initialized
// Exchange object with bidder instance
// To simulate the timeout feature, we are sending
// the bid request with `ask`, not with `tell`

class Exchange(bidder: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: String =>
      // a little trick - we extract max waiting time
      // for a bidder response from a `tmax` field of the request
      val jsonRequest = parser.parse(msg)
      val cursor = jsonRequest.toOption.get.hcursor
      val tmax = cursor.downField("tmax").as[Int].toOption.getOrElse(1000)

      val futureResponse = bidder.ask(msg) (tmax second)

      // Handle the future response using "onComplete"
      futureResponse.onComplete {
        case Success(response: String) =>
          println(s"${self.path} received response:\n $response")
        case Success(_) =>
          println(s"${self.path} received malformed response")
        case Failure(exception) =>
          println(s"${self.path} failed to receive response: ${exception.getMessage}")
      }

    case _ =>
      println("Unknown message")
  }

}
