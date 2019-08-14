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

package com.wegtam.books.pfhais.impure.api

import java.util.UUID

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import cats.implicits._
import com.wegtam.books.pfhais.impure.db._
import com.wegtam.books.pfhais.impure.models._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import eu.timepit.refined.auto._

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

final class ProductRoutes(repo: Repository)(implicit ec: ExecutionContext) {
  // Custom path matcher to extract product ids from the akka http path.
  val ProductIdSegment: PathMatcher1[ProductId] =
    PathMatcher("^$".r).flatMap(s => Try(UUID.fromString(s)).toOption)

  val routes = path("product" / ProductIdSegment) { id: ProductId =>
    get {
      complete {
        for {
          rows <- repo.loadProduct(id)
          prod <- Future { Product.fromDatabase(rows) }
        } yield prod
      }
    } ~ put {
      entity(as[Product]) { p =>
        complete {
          repo.updateProduct(p)
        }
      }
    }
  }
}
