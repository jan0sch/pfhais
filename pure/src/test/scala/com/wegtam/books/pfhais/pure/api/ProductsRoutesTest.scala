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

package com.wegtam.books.pfhais.pure.api

import cats._
import cats.effect._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.pure.db._
import com.wegtam.books.pfhais.pure.models._
import com.wegtam.books.pfhais.pure.models.TypeGenerators._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.collection.immutable.Seq

final class ProductsRoutesTest extends BaseSpec {
  implicit def decodeProduct: EntityDecoder[IO, Product]                   = jsonOf
  implicit def decodeProducts: EntityDecoder[IO, List[Product]]            = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf
  private val emptyRepository: Repository[IO]                              = new TestRepository[IO](Seq.empty)

  "ProductsRoutes" when {
    "GET /products" when {
      "no products exist" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and an empty list" in {
          def service: HttpRoutes[IO] =
            Router("/" -> new ProductsRoutes(emptyRepository).routes)
          val response: IO[Response[IO]] = service.orNotFound.run(
            Request(method = Method.GET, uri = Uri.uri("/products"))
          )
          val result = response.unsafeRunSync
          result.status must be(expectedStatusCode)
          result.as[List[Product]].unsafeRunSync mustEqual List.empty[Product]
        }
      }

      "products exist" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and a list of products" in {
          forAll("products") { ps: List[Product] =>
            val repo: Repository[IO] = new TestRepository[IO](ps)
            def service: HttpRoutes[IO] =
              Router("/" -> new ProductsRoutes(repo).routes)
            val response: IO[Response[IO]] = service.orNotFound.run(
              Request(method = Method.GET, uri = Uri.uri("/products"))
            )
            val result = response.unsafeRunSync
            result.status must be(expectedStatusCode)
            result.as[List[Product]].unsafeRunSync mustEqual ps
          }
        }
      }
    }

    "POST /products" when {
      "request body is invalid" must {
        val expectedStatusCode = Status.BadRequest

        s"return $expectedStatusCode" in {
          def service: HttpRoutes[IO] =
            Router("/" -> new ProductsRoutes(emptyRepository).routes)
          val payload = scala.util.Random.alphanumeric.take(256).mkString
          val response: IO[Response[IO]] = service.orNotFound.run(
            Request(method = Method.POST, uri = Uri.uri("/products"))
              .withEntity(payload.asJson.noSpaces)
          )
          val result = response.unsafeRunSync
          result.status must be(expectedStatusCode)
          result.body.compile.toVector.unsafeRunSync must be(empty)
        }
      }

      "request body is valid" when {
        "product could be saved" must {
          val expectedStatusCode = Status.NoContent

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              val repo: Repository[IO] = new TestRepository[IO](Seq(p))
              def service: HttpRoutes[IO] =
                Router("/" -> new ProductsRoutes(repo).routes)
              val response: IO[Response[IO]] = service.orNotFound.run(
                Request(method = Method.POST, uri = Uri.uri("/products"))
                  .withEntity(p)
              )
              val result = response.unsafeRunSync
              result.status must be(expectedStatusCode)
              result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }

        "product could not be saved" must {
          val expectedStatusCode = Status.InternalServerError

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              def service: HttpRoutes[IO] =
                Router("/" -> new ProductsRoutes(emptyRepository).routes)
              val response: IO[Response[IO]] = service.orNotFound.run(
                Request(method = Method.POST, uri = Uri.uri("/products"))
                  .withEntity(p)
              )
              val result = response.unsafeRunSync
              result.status must be(expectedStatusCode)
              result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
      }
    }
  }
}
