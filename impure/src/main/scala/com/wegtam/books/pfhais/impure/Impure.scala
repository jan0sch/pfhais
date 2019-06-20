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
import com.wegtam.books.pfhais.impure.db._
import com.wegtam.books.pfhais.impure.models._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway
import slick.basic._
import slick.jdbc._

import scala.io.StdIn
import scala.concurrent.{ ExecutionContext, Future }
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
    implicit val system: ActorSystem    = ActorSystem()
    implicit val mat: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext   = system.dispatcher

    val url = "jdbc:postgresql://" +
    system.settings.config.getString("db.properties.serverName") +
    ":" + system.settings.config.getString("db.properties.portNumber") +
    "/" + system.settings.config.getString("db.properties.databaseName")
    val user           = system.settings.config.getString("db.properties.user")
    val pass           = system.settings.config.getString("db.properties.password")
    val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
    val _              = flyway.migrate()

    val dbConfig: DatabaseConfig[JdbcProfile] =
      DatabaseConfig.forConfig("db", system.settings.config)
    val repo = new Repository(dbConfig)

    val route = path("product" / ProductIdSegment) { id: ProductId =>
      get {
        complete {
          for {
            rows <- repo.loadProduct(id)
            prod <- Future { Product.fromDatabase(rows) }
          } yield prod
        }
      } ~ put {
        entity(as[Product]) { p =>
          complete {
            repo.updateProduct(p)
          }
        }
      }
    } ~ path("products") {
      get {
        complete {
          // FIXME This is pretty ugly and eats up memory.
          val products = for {
            rows <- repo.loadProducts()
            ps <- Future {
              rows.toList.groupBy(_._1).map {
                case (_, cols) => Product.fromDatabase(cols)
              }
            }
          } yield ps
          products.map(_.toList.flatten)
        }
      } ~
      post {
        entity(as[Product]) { p =>
          complete {
            repo.saveProduct(p)
          }
        }
      }
    }

    val host       = system.settings.config.getString("api.host")
    val port       = system.settings.config.getInt("api.port")
    val srv        = Http().bindAndHandle(route, host, port)
    val pressEnter = StdIn.readLine()
    srv.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

}
