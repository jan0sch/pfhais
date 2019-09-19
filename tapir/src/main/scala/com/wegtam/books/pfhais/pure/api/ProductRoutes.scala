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

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import com.wegtam.books.pfhais.pure.db._
import com.wegtam.books.pfhais.pure.models._
import eu.timepit.refined.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import tapir._
import tapir.json.circe._
import tapir.server.http4s._

final class ProductRoutes[F[_]: Sync](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product]                    = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf

  val getRoute: HttpRoutes[F] = ProductRoutes.getProduct.toRoutes { id =>
    for {
      rows <- repo.loadProduct(id)
      resp = Product.fromDatabase(rows).fold(().asLeft[Product])(_.asRight[Unit])
    } yield resp
  }

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "product" / UUIDVar(id) =>
      for {
        rows <- repo.loadProduct(id)
        resp <- Product.fromDatabase(rows).fold(NotFound())(p => Ok(p))
      } yield resp
    case req @ PUT -> Root / "product" / UUIDVar(id) =>
      req
        .as[Product]
        .flatMap { p =>
          for {
            cnt <- repo.updateProduct(p)
            res <- cnt match {
              case 0 => NotFound()
              case _ => NoContent()
            }
          } yield res
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }
  }

}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object ProductRoutes {

  val getProduct: Endpoint[ProductId, Unit, Product, Nothing] = endpoint.get
    .in("product" / path[ProductId])
    .out(jsonBody[Product])

  val updateProduct: Endpoint[(ProductId, Product), Unit, Unit, Nothing] = endpoint.put
    .in("product" / path[ProductId])
    .in(
      jsonBody[Product]
        .description("The updated product data which should be saved.")
    )

}
