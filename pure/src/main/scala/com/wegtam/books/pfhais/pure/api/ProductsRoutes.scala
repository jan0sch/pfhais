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

import cats.effect.Sync
import cats.implicits._
import com.wegtam.books.pfhais.pure.db._
import com.wegtam.books.pfhais.pure.models._
import eu.timepit.refined.auto._
import fs2.Stream
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

final class ProductsRoutes[F[_]: Sync](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product] = jsonOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "products" =>
      val prefix = Stream.eval("[".pure[F])
      val suffix = Stream.eval("]".pure[F])
      val ps = repo.loadProducts
        .groupAdjacentBy(_._1)
        .map {
          case (id, rows) => Product.fromDatabase(rows.toList)
        }
        .collect {
          case Some(p) => p
        }
        .map(_.asJson.noSpaces)
        .intersperse(",")
      @SuppressWarnings(Array("org.wartremover.warts.Any"))
      val result: Stream[F, String] = prefix ++ ps ++ suffix
      Ok(result)
    case req @ POST -> Root / "products" =>
      req
        .as[Product]
        .flatMap { p =>
          for {
            cnt <- repo.saveProduct(p)
            res <- cnt match {
              case 0 => InternalServerError()
              case _ => NoContent()
            }
          } yield res
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }
  }

}
