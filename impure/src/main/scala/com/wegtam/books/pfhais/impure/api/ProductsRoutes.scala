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

import akka.NotUsed
import akka.http.scaladsl.common._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl._
import cats.implicits._
import com.wegtam.books.pfhais.impure.db._
import com.wegtam.books.pfhais.impure.models._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import eu.timepit.refined.auto._

import scala.concurrent.ExecutionContext

final class ProductsRoutes(repo: Repository)(implicit ec: ExecutionContext) {
  val routes = path("products") {
    get {
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()

      val src = Source.fromPublisher(repo.loadProducts())
      val products: Source[Product, NotUsed] = src
        .collect(
          cs =>
            Product.fromDatabase(Seq(cs)) match {
              case Some(p) => p
            }
        )
        .groupBy(Int.MaxValue, _.id)
        .fold(Option.empty[Product])(
          (op, x) => op.fold(x.some)(p => p.copy(names = p.names ++ x.names).some)
        )
        .mergeSubstreams
        .collect(
          op =>
            op match {
              case Some(p) => p
            }
        )
      complete(products)
    } ~
    post {
      entity(as[Product]) { p =>
        complete {
          repo.saveProduct(p)
        }
      }
    }
  }
}
