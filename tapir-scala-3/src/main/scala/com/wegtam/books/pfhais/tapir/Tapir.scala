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
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.api._
import com.wegtam.books.pfhais.tapir.config._
import com.wegtam.books.pfhais.tapir.db._
import doobie._
import eu.timepit.refined.auto._
import org.http4s.ember.server._
import org.http4s.server.Router
import pureconfig._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

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

  /**
    * Update the provided documentation structure by adding some information.
    *
    * @param docs The generated OpenAPI documentation from tapir.
    * @return An updated documentation structure.
    */
  private def updateDocumentation(docs: OpenAPI): OpenAPI =
    // Generate some lenses.
    //val components: Lens[OpenAPI, Option[Components]] = GenLens[OpenAPI](_.components)
    //val componentsSchemas: Lens[Components, ListMap[String, ReferenceOr[Schema]]] =
    //  GenLens[Components](_.schemas)
    //val paths: Lens[OpenAPI, ListMap[String, PathItem]] = GenLens[OpenAPI](_.paths)
    //val getOps: Lens[PathItem, Option[Operation]]       = GenLens[PathItem](_.get)
    //val putOps: Lens[PathItem, Option[Operation]]       = GenLens[PathItem](_.put)
    //val operationParams: Lens[Operation, List[ReferenceOr[Parameter]]] =
    //  GenLens[Operation](_.parameters)
    //val parameterSchema: Lens[Parameter, ReferenceOr[Schema]] = GenLens[Parameter](_.schema)
    //val schemaProperties: Lens[Schema, ListMap[String, ReferenceOr[Schema]]] =
    //  GenLens[Schema](_.properties)
    //val schemaPattern: Lens[Schema, Option[String]] = GenLens[Schema](_.pattern)
    //// Now try to get things going...
    //val typeRegex = implicitly[RefinedExtract[LanguageCode]].regex
    //val langRegex = "/" + typeRegex + "/" // convert to Javascript regular expression
    //val uuidRegex = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i"
    //val updateGetProductId =
    //  (paths at ("/product/{id}") composeOptional possible andThen getOps composeOptional possible andThen operationParams composeTraversal each composeOptional possible andThen parameterSchema composeOptional possible andThen schemaPattern)
    //    .replace(uuidRegex.some)(docs)
    //val updatePutProductId =
    //  (paths at ("/product/{id}") composeOptional possible andThen putOps composeOptional possible andThen operationParams composeTraversal each composeOptional possible andThen parameterSchema composeOptional possible andThen schemaPattern)
    //    .replace(uuidRegex.some)(updateGetProductId)
    //val updateModelProduct =
    //  (components composeOptional possible andThen componentsSchemas at ("Product") composeOptional possible composeOptional possible andThen schemaProperties at ("id") composeOptional possible composeOptional possible andThen schemaPattern)
    //    .replace(uuidRegex.some)(updatePutProductId)
    //val updateModelTranslation =
    //  (components composeOptional possible andThen componentsSchemas at ("Translation") composeOptional possible composeOptional possible andThen schemaProperties at ("lang") composeOptional possible composeOptional possible andThen schemaPattern)
    //    .replace(langRegex.some)(updateModelProduct)
    //updateModelTranslation
    docs
}
