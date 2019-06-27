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
import cats.syntax.all._
import com.typesafe.config._
import com.wegtam.books.pfhais.pure.config._
import eu.timepit.refined.auto._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._

object Pure extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val productRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root / "product" / id =>
        ???
      case PUT -> Root / "product" / id =>
        ???
    }
    val productsRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root / "products" =>
        ???
      case POST -> Root / "products" =>
        ???
    }
    val routes  = productRoutes <+> productsRoutes
    val httpApp = Router("/" -> routes).orNotFound

    val program = for {
      cfg       <- IO(ConfigFactory.load)
      apiConfig <- loadConfig[ApiConfig](cfg, "api")
      dbConfig  <- loadConfig[DatabaseConfig](cfg, "database")
      server = BlazeServerBuilder[IO].bindHttp(apiConfig.port, apiConfig.host).withHttpApp(httpApp)
      fiber  = server.resource.use(_ => IO.never)
    } yield fiber
    program.as(ExitCode.Success)
  }
}
