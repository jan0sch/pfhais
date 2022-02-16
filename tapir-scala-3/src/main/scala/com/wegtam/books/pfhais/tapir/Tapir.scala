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
import cats.implicits._
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.api._
import com.wegtam.books.pfhais.tapir.config._
import com.wegtam.books.pfhais.tapir.db._
import com.wegtam.books.pfhais.tapir.models.LanguageCode
import doobie._
import eu.timepit.refined.auto._
import monocle._
import monocle.function.all._
import monocle.macros.GenLens
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.ember.server._
import pureconfig._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.collection.immutable._
import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Tapir extends IOApp.WithContext {
  val availableProcessors: Int      = Runtime.getRuntime().availableProcessors() / 2
  val blockingCores: Int            = if (availableProcessors < 2) 2 else availableProcessors
  val blockingPool: ExecutorService = Executors.newFixedThreadPool(blockingCores)
  val ec: ExecutionContext          = ExecutionContext.global

  override protected def executionContextResource: Resource[SyncIO, ExecutionContext] =
    Resource.eval(SyncIO(ec))

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def run(args: List[String]): IO[ExitCode] = {
    val blocker                        = Blocker.liftExecutorService(blockingPool)
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val program = for {
      (apiConfig, dbConfig) <- IO {
        val cfg = ConfigFactory.load(getClass().getClassLoader())
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
      updatedDocs = updateDocumentation(docs)
      docsRoutes  = new SwaggerHttp4s(updatedDocs.toYaml)
      routes      = productRoutes.routes <+> productsRoutes.routes
      httpApp     = Router("/" -> routes, "/docs" -> docsRoutes.routes).orNotFound
      resource = EmberServerBuilder
        .default[IO]
        .withBlocker(blocker)
        .withHost(apiConfig.host)
        .withPort(apiConfig.port)
        .withHttpApp(httpApp)
        .build
      fiber = resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
    } yield fiber
    program.attempt.unsafeRunSync() match {
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

  /**
    * Update the provided documentation structure by adding some information.
    *
    * @param docs The generated OpenAPI documentation from tapir.
    * @return An updated documentation structure.
    */
  private def updateDocumentation(docs: OpenAPI): OpenAPI = {
    // Generate some lenses.
    val components: Lens[OpenAPI, Option[Components]] = GenLens[OpenAPI](_.components)
    val componentsSchemas: Lens[Components, ListMap[String, OpenAPI.ReferenceOr[Schema]]] =
      GenLens[Components](_.schemas)
    val paths: Lens[OpenAPI, ListMap[String, PathItem]] = GenLens[OpenAPI](_.paths)
    val getOps: Lens[PathItem, Option[Operation]]       = GenLens[PathItem](_.get)
    val putOps: Lens[PathItem, Option[Operation]]       = GenLens[PathItem](_.put)
    val operationParams: Lens[Operation, List[OpenAPI.ReferenceOr[Parameter]]] =
      GenLens[Operation](_.parameters)
    val parameterSchema: Lens[Parameter, OpenAPI.ReferenceOr[Schema]] = GenLens[Parameter](_.schema)
    val schemaProperties: Lens[Schema, ListMap[String, OpenAPI.ReferenceOr[Schema]]] =
      GenLens[Schema](_.properties)
    val schemaPattern: Lens[Schema, Option[String]] = GenLens[Schema](_.pattern)
    // Now try to get things going...
    val typeRegex = implicitly[RefinedExtract[LanguageCode]].regex
    val langRegex = "/" + typeRegex + "/" // convert to Javascript regular expression
    val uuidRegex = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i"
    val updateGetProductId =
      (paths at ("/product/{id}") composeOptional possible andThen getOps composeOptional possible andThen operationParams composeTraversal each composeOptional possible andThen parameterSchema composeOptional possible andThen schemaPattern)
        .replace(uuidRegex.some)(docs)
    val updatePutProductId =
      (paths at ("/product/{id}") composeOptional possible andThen putOps composeOptional possible andThen operationParams composeTraversal each composeOptional possible andThen parameterSchema composeOptional possible andThen schemaPattern)
        .replace(uuidRegex.some)(updateGetProductId)
    val updateModelProduct =
      (components composeOptional possible andThen componentsSchemas at ("Product") composeOptional possible composeOptional possible andThen schemaProperties at ("id") composeOptional possible composeOptional possible andThen schemaPattern)
        .replace(uuidRegex.some)(updatePutProductId)
    val updateModelTranslation =
      (components composeOptional possible andThen componentsSchemas at ("Translation") composeOptional possible composeOptional possible andThen schemaProperties at ("lang") composeOptional possible composeOptional possible andThen schemaPattern)
        .replace(langRegex.some)(updateModelProduct)
    updateModelTranslation
  }
}
