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

final class ProductsRoutes[F[_]: Sync](repo: Repository[F]) extends Http4sDsl[F] {
  implicit def decodeProduct: EntityDecoder[F, Product]                            = jsonOf
  implicit def encodeProduct[A[_]: Applicative]: EntityEncoder[A, Option[Product]] = jsonEncoderOf

  val productsRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "products" =>
      ???
    case req @ POST -> Root / "products" =>
      for {
        p <- req.as[Product]
        _ <- repo.saveProduct(p)
        r <- NoContent()
      } yield r
  }

}
