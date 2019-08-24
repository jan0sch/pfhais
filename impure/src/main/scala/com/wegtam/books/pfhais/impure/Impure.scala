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

package com.wegtam.books.pfhais.impure

import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import com.wegtam.books.pfhais.impure.api._
import com.wegtam.books.pfhais.impure.db._
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway
import slick.basic._
import slick.jdbc._

import scala.io.StdIn
import scala.concurrent.ExecutionContext

object Impure {

  /**
    * Main entry point of the application.
    *
    * @param args A list of arguments given on the command line.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem    = ActorSystem()
    implicit val mat: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext   = system.dispatcher

    val url = "jdbc:postgresql://" +
      system.settings.config.getString("database.db.properties.serverName") +
      ":" + system.settings.config.getString("database.db.properties.portNumber") +
      "/" + system.settings.config.getString("database.db.properties.databaseName")
    val user           = system.settings.config.getString("database.db.properties.user")
    val pass           = system.settings.config.getString("database.db.properties.password")
    val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
    val _              = flyway.migrate()

    val dbConfig: DatabaseConfig[JdbcProfile] =
      DatabaseConfig.forConfig("database", system.settings.config)
    val repo = new Repository(dbConfig)

    val productRoutes  = new ProductRoutes(repo)
    val productsRoutes = new ProductsRoutes(repo)
    val routes         = productRoutes.routes ~ productsRoutes.routes

    val host       = system.settings.config.getString("api.host")
    val port       = system.settings.config.getInt("api.port")
    val srv        = Http().bindAndHandle(routes, host, port)
    val pressEnter = StdIn.readLine()
    srv.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

}
