import akka.actor.{ActorSystem, PoisonPill, Props}
import java.io.{File, FileNotFoundException, IOException}
import language.postfixOps
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("RTBSimulator")
    val bidder = system.actorOf(Props[Bidder](), "Bidder")
    val exchange = system.actorOf(Props(classOf[Exchange], bidder), "Exchange")

    val dir = new File("./data/").listFiles.filter(_.isFile).map(_.getPath)
    dir.foreach( filename => {
        var bufferedSource: Source = null
        try {
          bufferedSource = Source.fromFile(filename)
          val fileContents = bufferedSource.getLines.mkString
          exchange ! fileContents
        } catch {
          case _: FileNotFoundException => println(s"Couldn't find $filename")
          case _: IOException => println(s"Had an IOException reading $filename")
        } finally {
          if (bufferedSource != null)
            bufferedSource.close
        }
        Thread.sleep(1000)
      }
    )

    bidder ! PoisonPill
    exchange ! PoisonPill

    system.terminate()
  }
}