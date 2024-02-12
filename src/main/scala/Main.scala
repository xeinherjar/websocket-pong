import cats.data.State
import cats.effect.std.Queue
import cats.effect.{IO, IOApp}
import cats.implicits.*
import com.comcast.ip4s._
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.*
import org.http4s.circe._
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.staticcontent._
import org.http4s.server.websocket.*
import org.http4s.websocket.*
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

import concurrent.duration.*

object Foo:
  case class Sprite(x: Int, y: Int, width: Int, height: Int)
  case class Paddle(player: Int, sprite: Sprite)
  case class Team(players: List[Paddle])
  case class World(leftPlayers: Team, rightPlayers: Team, ball: Sprite, width: Int, height: Int)

enum GameEvent:
  case Connect
  case KeyPress(playerId: Int, keyId: Int)

object Main extends IOApp.Simple:
  given LoggerFactory[IO] = Slf4jFactory.create[IO]
  given Logger[IO]        = Slf4jLogger.getLogger[IO] // such impure, much log

  val logger: SelfAwareStructuredLogger[IO] = LoggerFactory[IO].getLogger

  val world = State[Foo.World, Unit]

  val service = HttpRoutes.of[IO] { case GET -> Root / "hello" / v =>
    Ok(s"Got: $v")
  }
  def wsService(ws: WebSocketBuilder2[IO], incomingEvents: Topic[IO, Int], outgoingEvents: Topic[IO, Foo.World]) = HttpRoutes.of[IO] {
    case GET -> Root / "ws" =>
      val send: Stream[IO, WebSocketFrame] = outgoingEvents.subscribeUnbounded.map(world => WebSocketFrame.Text(world.asJson.toString))
      def receive: Pipe[IO, WebSocketFrame, Unit] = _.evalMap {
        case WebSocketFrame.Text(d, l) => logger.info(s"Text--: $d, $l")
        case WebSocketFrame.Text(t)    => logger.info(s"Text: ${t._1}") *> t._1.toIntOption.traverse_(i => incomingEvents.publish1(i))
        case WebSocketFrame.Close(c)   => logger.info(s"Close: $c")
        case e: WebSocketFrame         => logger.info(s"Event: $e")
      }
      ws.build(send, receive)
  }

  def run: IO[Unit] = for {
    keyPressEvents    <- Topic[IO, Int]
    worldChangeEvents <- Topic[IO, Foo.World]
    server = EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8000")
      .withHttpWebSocketApp(wsBuilder =>
        Router(
          "/"      -> (wsService(wsBuilder, keyPressEvents, worldChangeEvents) <+> service),
          "assets" -> fileService(FileService.Config("./assets"))
        ).orNotFound
      )
      .build
    // _ <- keyPressEvents.subscribeUnbounded.through(updateWorld...
    _ <- server.use(_ => IO.never)

  } yield ()
