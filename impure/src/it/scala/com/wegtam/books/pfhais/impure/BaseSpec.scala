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

package com.wegtam.books.pfhais

import java.net.ServerSocket

import akka.actor._
import akka.stream._
import akka.testkit.TestKit
import com.typesafe.config._
import org.flywaydb.core.Flyway
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import scala.concurrent.duration._

/**
  * A base class for our integration tests.
  *
  * It uses the akka testkit to provide an actor system with loaded
  * configuration out of the box.
  */
abstract class BaseSpec
    extends TestKit(
      ActorSystem(
        "it-test",
        ConfigFactory
          .parseString(s"api.port=${BaseSpec.findAvailablePort()}")
          .withFallback(ConfigFactory.load())
      )
    )
    with AsyncWordSpecLike
    with MustMatchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

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
  }
}

object BaseSpec {

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
