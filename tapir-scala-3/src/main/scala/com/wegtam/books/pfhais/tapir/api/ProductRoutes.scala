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

package com.wegtam.books.pfhais.tapir.api

import cats._
import cats.data.NonEmptySet
import cats.effect._
import cats.implicits._
import com.wegtam.books.pfhais.tapir.db._
import com.wegtam.books.pfhais.tapir.models._
import eu.timepit.refined.auto._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.{ EntityDecoder, EntityEncoder, HttpRoutes }
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

final class ProductRoutes[F[_]: Async](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product]                    = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf

  private val getRoute: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(ProductRoutes.getProduct.serverLogic { id =>
      for {
        rows <- repo.loadProduct(id)
        resp = Product
          .fromDatabase(rows)
          .fold(StatusCode.NotFound.asLeft[Product])(_.asRight[StatusCode])
      } yield resp
    })

  private val updateRoute: HttpRoutes[F] =
    Http4sServerInterpreter[F]().toRoutes(ProductRoutes.updateProduct.serverLogic {
      case (_, p) =>
        for {
          cnt <- repo.updateProduct(p)
          res = cnt match {
            case 0 => StatusCode.NotFound.asLeft[Unit]
            case _ => ().asRight[StatusCode]
          }
        } yield res
    })

  val routes: HttpRoutes[F] = getRoute <+> updateRoute

}

object ProductRoutes {
  val example = Product(
    id = java.util.UUID.randomUUID,
    names = NonEmptySet.one(
        Translation(
          lang = "de",
          name = "Das ist ein Name."
        )
      ) ++
      NonEmptySet.one(
        Translation(
          lang = "en",
          name = "That's a name."
        )
      ) ++
      NonEmptySet.one(
        Translation(
          lang = "es",
          name = "Ese es un nombre."
        )
      )
  )

  val getProduct: Endpoint[Unit, ProductId, StatusCode, Product, Any] = endpoint.get
    .in(
      "product" / path[ProductId]("id")
        .description("The ID of a product which is a UUID.")
        .example(example.id)
    )
    .errorOut(statusCode)
    .out(
      jsonBody[Product].description("The product associated with the given ID.").example(example)
    )
    .description(
      "Returns the product specified by the ID given in the URL path. If the product does not exist then a HTTP 404 error is returned."
    )

  val updateProduct: Endpoint[Unit, (ProductId, Product), StatusCode, Unit, Any] =
    endpoint.put
      .in(
        "product" / path[ProductId]("id")
          .description("The ID of a product which is a UUID.")
          .example(example.id)
      )
      .in(
        jsonBody[Product]
          .description("The updated product data which should be saved.")
          .example(example)
      )
      .errorOut(statusCode)
      .out(
        statusCode(StatusCode.NoContent)
          .description("Upon successful product update no content is returned.")
      )
      .description(
        "Updates the product specified by the ID given in the URL path. The product data has to be passed encoded as JSON in the request body. If the product does not exist then a HTTP 404 error is returned."
      )

}
