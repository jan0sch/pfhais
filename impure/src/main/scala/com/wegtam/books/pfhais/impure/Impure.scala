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

import java.util.UUID

import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import com.wegtam.books.pfhais.impure.models._
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext
import scala.io.StdIn
import scala.util.Try

object Impure {

  // Custom path matcher to extract product ids from the akka http path.
  val ProductIdSegment: PathMatcher1[ProductId] =
    PathMatcher("^$".r).flatMap(s => Try(UUID.fromString(s)).toOption)

  /**
    * Main entry point of the application.
    *
    * @param args A list of arguments given on the command line.
    */
  def main(args: Array[String]): Unit = {
    implicit val as: ActorSystem       = ActorSystem()
    implicit val am: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext  = as.dispatcher

    val url = "jdbc:postgresql://" +
    as.settings.config.getString("db.properties.serverName") +
    ":" + as.settings.config.getString("db.properties.portNumber") +
    "/" + as.settings.config.getString("db.properties.databaseName")
    val user           = as.settings.config.getString("db.properties.user")
    val pass           = as.settings.config.getString("db.properties.password")
    val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
    val _              = flyway.migrate()

    val route = path("product" / ProductIdSegment) { id: ProductId =>
      get {
        ???
      } ~ put {
        ???
      }
    } ~ path("products") {
      get {
        ???
      } ~
      post {
        ???
      }
    }

    val host       = as.settings.config.getString("api.host")
    val port       = as.settings.config.getInt("api.port")
    val srv        = Http().bindAndHandle(route, host, port)
    val pressEnter = StdIn.readLine()
    srv.flatMap(_.unbind()).onComplete(_ => as.terminate())
  }

}
