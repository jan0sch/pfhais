/*
 * CC0 1.0 Universal (CC0 1.0) - Public Domain Dedication
 *
 *                                No Copyright
 *
 * The person who associated a work with this deed has dedicated the work to 
 * the public domain by waiving all of his or her rights to the work worldwide
 * under copyright law, including all related and neighboring rights, to the 
 * extent allowed by law.
 */

package com.wegtam.books.pfhais.impure.usecases

import java.net.ServerSocket

import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.testkit.TestKit
import com.wegtam.books.pfhais.impure.api._
import com.wegtam.books.pfhais.impure.db._
import com.wegtam.books.pfhais.impure.usecases.BaseUseCaseActor.BaseUseCaseActorCmds
import com.typesafe.config._
import org.flywaydb.core.Flyway
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import slick.basic._
import slick.jdbc._

import scala.concurrent.duration._

/**
  * A base class for our usecase integration tests.
  *
  * It uses the akka testkit to provide an actor system with loaded
  * configuration out of the box.
  */
abstract class BaseUseCaseSpec
    extends TestKit(
      ActorSystem(
        "it-test",
        ConfigFactory
          .parseString(s"api.port=${BaseUseCaseSpec.findAvailablePort()}")
          .withFallback(ConfigFactory.load())
      )
    )
    with AsyncWordSpecLike
    with MustMatchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  final val baseUrl: String = s"""http://${system.settings.config
    .getString("api.host")}:${system.settings.config
    .getInt("api.port")}"""

  private val url = "jdbc:postgresql://" +
    system.settings.config.getString("database.db.properties.serverName") +
    ":" + system.settings.config
    .getString("database.db.properties.portNumber") +
    "/" + system.settings.config
    .getString("database.db.properties.databaseName")
  private val user = system.settings.config.getString("database.db.properties.user")
  private val pass =
    system.settings.config.getString("database.db.properties.password")
  protected val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()

  protected val dbConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig("database", system.settings.config)
  protected val repo = new Repository(dbConfig)

  /**
    * Shutdown the actor system after the tests have run.
    * If the system does not terminate within the given time frame an error is thrown.
    */
  override protected def afterAll(): Unit =
    TestKit.shutdownActorSystem(system, FiniteDuration(5, SECONDS))

  /**
    * Initialise the database before any tests are run.
    */
  override protected def beforeAll(): Unit = {
    val _ = flyway.migrate()
    val a = system.actorOf(BaseUseCaseActor.props(repo, materializer))
    a ! BaseUseCaseActorCmds.Start
  }
}

object BaseUseCaseSpec {

  /**
    * Start a server socket and close it. The port number used by
    * the socket is considered free and returned.
    *
    * @return A port number.
    */
  def findAvailablePort(): Int = {
    val serverSocket = new ServerSocket(0)
    val freePort     = serverSocket.getLocalPort
    serverSocket.setReuseAddress(true) // Allow instant rebinding of the socket.
    serverSocket.close()
    freePort
  }
}

/**
  * An actor which fires up our routes so we can test them.
  *
  * @param repo The database repository.
  * @param mat  An actor materializer to be used.
  */
final class BaseUseCaseActor(repo: Repository, mat: ActorMaterializer) extends Actor with ActorLogging {
  import context.dispatcher

  implicit val system: ActorSystem             = context.system
  implicit val materializer: ActorMaterializer = mat

  override def receive: Receive = {
    case BaseUseCaseActorCmds.Start =>
      val productRoutes  = new ProductRoutes(repo)
      val productsRoutes = new ProductsRoutes(repo)
      val routes         = productRoutes.routes ~ productsRoutes.routes
      val host           = context.system.settings.config.getString("api.host")
      val port           = context.system.settings.config.getInt("api.port")
      val _              = Http().bindAndHandle(routes, host, port)
    case BaseUseCaseActorCmds.Stop =>
      context.stop(self)
  }
}

object BaseUseCaseActor {
  /**
    * Create the properties to create the actor.
    *
    * @param repo The database repository.
    * @param mat  An actor materializer to be used.
    * @return The props to create the actor.
    */
  def props(repo: Repository, mat: ActorMaterializer): Props = Props(new BaseUseCaseActor(repo, mat))

  /**
    * A sealed trait for our actor commands.
    */
  sealed trait BaseUseCaseActorCmds

  object BaseUseCaseActorCmds {
    /**
      * Start the API
      */
    case object Start extends BaseUseCaseActorCmds
    /**
      * Stop the API
      */
    case object Stop extends BaseUseCaseActorCmds
  }
}
