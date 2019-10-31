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

package com.wegtam.books.pfhais.tapir

import cats.effect._
import cats.implicits._
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.api._
import com.wegtam.books.pfhais.tapir.config._
import com.wegtam.books.pfhais.tapir.db._
import doobie._
import eu.timepit.refined.auto._
import monocle._
import monocle.function.At
import monocle.function.all._
import monocle.macros.GenLens
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._
import tapir.docs.openapi._
import tapir.openapi._
import tapir.openapi.circe.yaml._
import tapir.swagger.http4s.SwaggerHttp4s

import scala.collection.immutable._
import scala.io.StdIn

object Tapir extends IOApp {

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
        val cfg = ConfigFactory.load
        // TODO Think about alternatives to `Throw`.
        (
          ConfigSource.fromConfig(cfg).at("api").loadOrThrow[ApiConfig],
          ConfigSource.fromConfig(cfg).at("database").loadOrThrow[DatabaseConfig]
        )
      }
      ms <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      tx = Transactor
        .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass)
      repo           = new DoobieRepository(tx)
      productRoutes  = new ProductRoutes(repo)
      productsRoutes = new ProductsRoutes(repo)
      docs = List(
        ProductRoutes.getProduct,
        ProductRoutes.updateProduct,
        ProductsRoutes.getProducts,
        ProductsRoutes.createProduct
      ).toOpenAPI("Pure Tapir API", "1.0.0")
      _          = fixme(docs)
      docsRoutes = new SwaggerHttp4s(docs.toYaml)
      routes     = productRoutes.routes <+> productsRoutes.routes
      httpApp    = Router("/" -> routes, "/docs" -> docsRoutes.routes).orNotFound
      server     = BlazeServerBuilder[IO].bindHttp(apiConfig.port, apiConfig.host).withHttpApp(httpApp)
      fiber      = server.resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
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

  private def fixme(d: OpenAPI): Unit = {
    implicit def atListMap[K, V]: At[ListMap[K, V], K, Option[V]] = At(
      i => Lens((_: ListMap[K, V]).get(i))(optV => map => optV.fold(map - i)(v => map + (i -> v)))
    )
    // Generate some lenses.
    val paths: Lens[OpenAPI, ListMap[String, PathItem]] = GenLens[OpenAPI](_.paths)
    val parameters: Lens[PathItem, List[OpenAPI.ReferenceOr[Parameter]]] =
      GenLens[PathItem](_.parameters)
    val parameterSchema: Lens[Parameter, OpenAPI.ReferenceOr[Schema]] = GenLens[Parameter](_.schema)
    // Now try to get thins going...
    val x  = (paths composeLens at("/product/{id}")).get(d)
    val ps = d.paths.get("/product/{id}")
    println(s"PS: $ps")
    val _ = for {
      pi <- ps
      go <- pi.get
      id <- go.parameters.find(_.toOption.map(_.name === "id").getOrElse(false)).flatMap(_.toOption)
      _ = println(id)
    } yield ()
  }
}
