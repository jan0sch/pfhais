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

import java.util.concurrent.{ ExecutorService, Executors }

import cats.effect._
import cats.syntax.all._
import com.softwaremill.quicklens._
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.api._
import com.wegtam.books.pfhais.tapir.config._
import com.wegtam.books.pfhais.tapir.db._
import com.wegtam.books.pfhais.tapir.models.LanguageCode
import doobie._
import eu.timepit.refined.auto._
import org.http4s.ember.server._
import org.http4s.server.Router
import pureconfig._
import sttp.tapir.apispec._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

import scala.collection.immutable._
import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Tapir extends IOApp {
  val availableProcessors: Int      = Runtime.getRuntime().availableProcessors() / 2
  val blockingCores: Int            = if (availableProcessors < 2) 2 else availableProcessors
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(blockingCores)
  val ec: ExecutionContext          = ExecutionContext.global

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def run(args: List[String]): IO[ExitCode] = {
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    for {
      cfg       <- IO(ConfigFactory.load(getClass().getClassLoader()))
      apiConfig <- IO(ConfigSource.fromConfig(cfg).at("api").loadOrThrow[ApiConfig])
      dbConfig  <- IO(ConfigSource.fromConfig(cfg).at("database").loadOrThrow[DatabaseConfig])
      ms        <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
      tx = Transactor
        .fromDriverManager[IO](dbConfig.driver, dbConfig.url, dbConfig.user, dbConfig.pass)
      repo           = new DoobieRepository(tx)
      productRoutes  = new ProductRoutes(repo)
      productsRoutes = new ProductsRoutes(repo)
      docs = OpenAPIDocsInterpreter().toOpenAPI(
        List(
          ProductRoutes.getProduct,
          ProductRoutes.updateProduct,
          ProductsRoutes.getProducts,
          ProductsRoutes.createProduct
        ),
        "Pure Tapir API",
        "1.0.0"
      )
      updatedDocs = updateDocumentation(docs)
      docsRoutes  = Http4sServerInterpreter[IO]().toRoutes(SwaggerUI[IO](updatedDocs.toYaml))
      routes      = productRoutes.routes <+> productsRoutes.routes
      httpApp     = Router("/" -> routes, "/docs" -> docsRoutes).orNotFound
      resource = EmberServerBuilder
        .default[IO]
        .withHost(apiConfig.host)
        .withPort(apiConfig.port)
        .withHttpApp(httpApp)
        .build
      fiber <- resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
    } yield fiber
  }

  /** Update the provided documentation structure by adding some information.
    *
    * @param docs
    *   The generated OpenAPI documentation from tapir.
    * @return
    *   An updated documentation structure.
    */
  private def updateDocumentation(docs: OpenAPI): OpenAPI = {
    // Our regular expressions.
    val langRegex = "/^[a-z]{2}$/"
    val uuidRegex = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i"
    // Update the documentation structure.
    val updateProductId = docs
      .modify(_.paths.pathItems.at("/product/{id}").parameters.each.eachRight.schema.at.eachRight.pattern)
      .using(_ => uuidRegex.some)
    val updateModelProduct = updateProductId
      .modify(_.components.at.schemas.at("Product").eachRight.properties.at("id").eachRight.pattern)
      .using(_ => uuidRegex.some)
    val updateModelTranslation = updateModelProduct
      .modify(_.components.at.schemas.at("Translation").eachRight.properties.at("lang").eachRight.pattern)
      .using(_ => langRegex.some)
    updateModelTranslation
  }
}
