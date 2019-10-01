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
import cats.effect._
import cats.implicits._
import com.wegtam.books.pfhais.pure.db._
import com.wegtam.books.pfhais.pure.models._
import eu.timepit.refined.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import tapir._
import tapir.json.circe._
import tapir.model.{ StatusCode, StatusCodes }
import tapir.server.http4s._

final class ProductRoutes[F[_]: Sync: ContextShift](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product]                    = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Product] = jsonEncoderOf

  private val getRoute: HttpRoutes[F] = ProductRoutes.getProduct.toRoutes { id =>
    for {
      rows <- repo.loadProduct(id)
      resp = Product
        .fromDatabase(rows)
        .fold(StatusCodes.NotFound.asLeft[Product])(_.asRight[StatusCode])
    } yield resp
  }

  private val updateRoute: HttpRoutes[F] = ProductRoutes.updateProduct.toRoutes {
    case (id, p) =>
      for {
        cnt <- repo.updateProduct(p)
        res = cnt match {
          case 0 => StatusCodes.NotFound.asLeft[Unit]
          case _ => ().asRight[StatusCode]
        }
      } yield res
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  val routes: HttpRoutes[F] = getRoute <+> updateRoute

}

@SuppressWarnings(Array("org.wartremover.warts.Any"))
object ProductRoutes {

  val getProduct: Endpoint[ProductId, StatusCode, Product, Nothing] = endpoint.get
    .in("product" / path[ProductId]("id"))
    .errorOut(statusCode)
    .out(jsonBody[Product])

  val updateProduct: Endpoint[(ProductId, Product), StatusCode, Unit, Nothing] = endpoint.put
    .in("product" / path[ProductId]("id"))
    .in(
      jsonBody[Product]
        .description("The updated product data which should be saved.")
    )
    .errorOut(statusCode)
    .out(statusCode(StatusCodes.NoContent))

}
