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

package com.wegtam.books.pfhais.pure

import cats.effect._
import cats.implicits._
//import cats.syntax.all._
import com.typesafe.config._
import com.wegtam.books.pfhais.pure.api._
import com.wegtam.books.pfhais.pure.config._
import com.wegtam.books.pfhais.pure.db._
import doobie._
import eu.timepit.refined.auto._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._

import scala.io.StdIn

object Pure extends IOApp {

  @SuppressWarnings(
    Array(
      "org.wartremover.warts.Any",
      "scalafix:DisableSyntax.null"
    )
  )
  def run(args: List[String]): IO[ExitCode] = {
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val program = for {
      (apiConfig, dbConfig) <- IO {
        val cfg = ConfigFactory.load(getClass().getClassLoader())
        // TODO Think about alternatives to `Throw`.
        (
          loadConfigOrThrow[ApiConfig](cfg, "api"),
          loadConfigOrThrow[DatabaseConfig](cfg, "database")
        )
      }
      ms <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      tx = Transactor
        .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass)
      repo           = new DoobieRepository(tx)
      productRoutes  = new ProductRoutes(repo)
      productsRoutes = new ProductsRoutes(repo)
      routes         = productRoutes.routes <+> productsRoutes.routes
      httpApp        = Router("/" -> routes).orNotFound
      server         = BlazeServerBuilder[IO].bindHttp(apiConfig.port, apiConfig.host).withHttpApp(httpApp)
      fiber          = server.resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
    } yield fiber
    program.attempt.unsafeRunSync match {
      case Left(e) =>
        IO {
          println("*** An error occured! ***")
          if (e ne null) {
            println(e.getMessage)
          }
          ExitCode.Error
        }
      case Right(r) => r
    }
  }
}
