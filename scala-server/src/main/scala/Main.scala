import java.util.concurrent.Executors
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.{Materializer, OverflowStrategy}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.io.StdIn
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import org.reactivestreams.Publisher
import akka.actor.actorRef2Scala


object WSServer {
  implicit val system: ActorSystem = ActorSystem("app")
  implicit val materializer: Materializer = Materializer.matFromSystem
  implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(16))

  // CHANGE THE CONST HERE TO MODIFY THE DOMAIN AND/OR THE PORT
  val address = "localhost"
  val port = 8080
  
  def main(args: Array[String]): Unit = {

    val route: Route =
      path("ws") { // /ws are upgraded to WebSocket connection
        handleWebSocketMessages(connection())
      }

    // Bind Address and port to create the server
    val bindingFut = Http().bindAndHandle(route, address, port)
    
    println("Server is running: "+address+":"+port.toString)
    println("Type anything into the prompt to end the program")
    
    // Handle the end of server
    StdIn.readLine()
    bindingFut
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }



  def connection(): Flow[Message, Message, Any] = {
    //counter for each client
    var counter: Int = 0
    
    println("Client Successfully Connected")

    val (actorRef: ActorRef, publisher: Publisher[TextMessage.Strict]) =
      Source.actorRef[String](16, OverflowStrategy.fail)
        .map(msg => TextMessage.Strict(msg))
        .toMat(Sink.asPublisher(false))(Keep.both).run()


    val sink: Sink[Message, Any] = Flow[Message]
      .map {
        case TextMessage.Strict(msg) =>
          // Message from client
          println(msg)
          try{
            // Try to parse it as a Integer
            counter = counter + msg.toInt
            actorRef ! counter.toString
          }catch{
            // If fail, send an Error
            case _ =>{
              actorRef ! "NotANumber"
              println("Error: NotANumber")
            }
          }
      }
      .to(Sink.onComplete(x => x))

    // Pair sink and source
    Flow.fromSinkAndSource(sink, Source.fromPublisher(publisher))
  }


}