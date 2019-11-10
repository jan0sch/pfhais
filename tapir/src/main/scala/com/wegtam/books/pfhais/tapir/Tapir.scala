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

import cats._
import cats.effect._
import cats.implicits._
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.api._
import com.wegtam.books.pfhais.tapir.config._
import com.wegtam.books.pfhais.tapir.db._
import doobie._
import eu.timepit.refined.auto._
import monocle._
import monocle.function.{ At, Each, Index }
import monocle.function.all._
import monocle.macros.GenLens
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import pureconfig._
import shapeless.Witness
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
      updatedDocs = updateDocumentation(docs)
      docsRoutes  = new SwaggerHttp4s(updatedDocs.toYaml)
      routes      = productRoutes.routes <+> productsRoutes.routes
      httpApp     = Router("/" -> routes, "/docs" -> docsRoutes.routes).orNotFound
      server      = BlazeServerBuilder[IO].bindHttp(apiConfig.port, apiConfig.host).withHttpApp(httpApp)
      fiber       = server.resource.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)
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

  /**
    * Extract the regular expression value from an existing
    * implicit witness of for the type `String`.
    *
    * @return The value of the Witness.
    */
  def extractRegEx[S <: String](implicit ws: Witness.Aux[S]): String =
    ws.value

  /**
    * Update the provided documentation structure by adding some information.
    *
    * @param docs The generated OpenAPI documentation from tapir.
    * @return An updated documentation structure.
    */
  private def updateDocumentation(docs: OpenAPI): OpenAPI = {
    // Define needed type class instances for Monocle
    implicit def atListMap[K, V]: At[ListMap[K, V], K, Option[V]] = At(
      i => Lens((_: ListMap[K, V]).get(i))(optV => map => optV.fold(map - i)(v => map + (i -> v)))
    )
    implicit def listMapIndex[K, V]: Index[ListMap[K, V], K, V] = Index.fromAt
    implicit def listMapTraversal[K, V]: Traversal[ListMap[K, V], V] =
      new Traversal[ListMap[K, V], V] {
        def modifyF[F[_]: Applicative](f: V => F[V])(s: ListMap[K, V]): F[ListMap[K, V]] =
          s.foldLeft(Applicative[F].pure(ListMap.empty[K, V])) {
            case (acc, (k, v)) =>
              Applicative[F].map2(f(v), acc)((head, tail) => tail + (k -> head))
          }
      }
    implicit def listMapEach[K, V]: Each[ListMap[K, V], V] =
      Each(listMapTraversal)
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
    //val langRegex = extractRegEx[LanguageCode]
    val langRegex = "/^[a-z]{2}$/"
    val uuidRegex = "/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i"
    val updateGetProductId =
      (paths composeLens at("/product/{id}") composeOptional possible composeLens getOps composeOptional possible composeLens operationParams composeTraversal each composeOptional possible composeLens parameterSchema composeOptional possible composeLens schemaPattern)
        .set(uuidRegex.some)(docs)
    val updatePutProductId =
      (paths composeLens at("/product/{id}") composeOptional possible composeLens putOps composeOptional possible composeLens operationParams composeTraversal each composeOptional possible composeLens parameterSchema composeOptional possible composeLens schemaPattern)
        .set(uuidRegex.some)(updateGetProductId)
    val updateModelProduct =
      (components composeOptional possible composeLens componentsSchemas composeLens at("Product") composeOptional possible composeOptional possible composeLens schemaProperties composeLens at(
        "id"
      ) composeOptional possible composeOptional possible composeLens schemaPattern)
        .set(uuidRegex.some)(updatePutProductId)
    val updateModelTranslation =
      (components composeOptional possible composeLens componentsSchemas composeLens at(
        "Translation"
      ) composeOptional possible composeOptional possible composeLens schemaProperties composeLens at(
        "lang"
      ) composeOptional possible composeOptional possible composeLens schemaPattern)
        .set(langRegex.some)(updateModelProduct)
    updateModelTranslation
  }
}
