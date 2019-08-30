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

final class ProductRoutesTest extends BaseSpec {
  implicit def decodeProduct: EntityDecoder[IO, Product]                   = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf
  private val emptyRepository: Repository[IO]                              = new TestRepository[IO](Seq.empty)

  "ProductRoutes" when {
    "GET /product/ID" when {
      "product does not exist" must {
        val expectedStatusCode = Status.NotFound

        s"return $expectedStatusCode" in {
          forAll("id") { id: ProductId =>
            Uri.fromString("/product/" + id.toString) match {
              case Left(_) => fail("Could not generate valid URI!")
              case Right(u) =>
                def service: HttpRoutes[IO] =
                  Router("/" -> new ProductRoutes(emptyRepository).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result = response.unsafeRunSync
                result.status must be(expectedStatusCode)
                result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
      }

      "product exists" must {
        val expectedStatusCode = Status.Ok

        s"return $expectedStatusCode and the product" in {
          forAll("product") { p: Product =>
            Uri.fromString("/product/" + p.id.toString) match {
              case Left(_) => fail("Could not generate valid URI!")
              case Right(u) =>
                val repo: Repository[IO] = new TestRepository[IO](Seq(p))
                def service: HttpRoutes[IO] =
                  Router("/" -> new ProductRoutes(repo).routes)
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.GET, uri = u)
                )
                val result = response.unsafeRunSync
                result.status must be(expectedStatusCode)
                result.as[Product].unsafeRunSync must be(p)
            }
          }
        }
      }
    }

    "PUT /product/ID" when {
      "request body is invalid" must {
        val expectedStatusCode = Status.BadRequest

        s"return $expectedStatusCode" in {
          forAll("id") { id: ProductId =>
            Uri.fromString("/product/" + id.toString) match {
              case Left(_) => fail("Could not generate valid URI!")
              case Right(u) =>
                def service: HttpRoutes[IO] =
                  Router("/" -> new ProductRoutes(emptyRepository).routes)
                val payload = scala.util.Random.alphanumeric.take(256).mkString
                val response: IO[Response[IO]] = service.orNotFound.run(
                  Request(method = Method.PUT, uri = u)
                    .withEntity(payload.asJson.noSpaces)
                )
                val result = response.unsafeRunSync
                result.status must be(expectedStatusCode)
                result.body.compile.toVector.unsafeRunSync must be(empty)
            }
          }
        }
      }

      "request body is valid" when {
        "product does not exist" must {
          val expectedStatusCode = Status.NotFound

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              Uri.fromString("/product/" + p.id.toString) match {
                case Left(_) => fail("Could not generate valid URI!")
                case Right(u) =>
                  def service: HttpRoutes[IO] =
                    Router("/" -> new ProductRoutes(emptyRepository).routes)
                  val response: IO[Response[IO]] = service.orNotFound.run(
                    Request(method = Method.PUT, uri = u)
                      .withEntity(p)
                  )
                  val result = response.unsafeRunSync
                  result.status must be(expectedStatusCode)
                  result.body.compile.toVector.unsafeRunSync must be(empty)
              }
            }
          }
        }

        "product exists" must {
          val expectedStatusCode = Status.NoContent

          s"return $expectedStatusCode" in {
            forAll("product") { p: Product =>
              Uri.fromString("/product/" + p.id.toString) match {
                case Left(_) => fail("Could not generate valid URI!")
                case Right(u) =>
                  val repo: Repository[IO] = new TestRepository[IO](Seq(p))
                  def service: HttpRoutes[IO] =
                    Router("/" -> new ProductRoutes(repo).routes)
                  val response: IO[Response[IO]] = service.orNotFound.run(
                    Request(method = Method.PUT, uri = u)
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
}
