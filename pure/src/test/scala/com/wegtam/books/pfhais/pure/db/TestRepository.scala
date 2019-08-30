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

package com.wegtam.books.pfhais.pure.db

import cats.effect._
import cats.implicits._
import com.wegtam.books.pfhais.pure.models._
import fs2.Stream

import scala.collection.immutable._

class TestRepository[F[_]: Effect](data: Seq[Product]) extends Repository[F] {
  override def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]] =
    data.find(_.id === id) match {
      case None => Seq.empty.pure[F]
      case Some(p) =>
        val ns = p.names.toNonEmptyList.toList.to[Seq]
        ns.map(n => (p.id, n.lang, n.name)).pure[F]
    }

  override def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)] = {
    val rows = data.flatMap { p =>
      val ns = p.names.toNonEmptyList.toList.to[Seq]
      ns.map(n => (p.id, n.lang, n.name))
    }
    Stream.emits(rows)
  }

  override def saveProduct(p: Product): F[Int] =
    data.find(_.id === p.id).fold(0.pure[F])(_ => 1.pure[F])

  override def updateProduct(p: Product): F[Int] =
    data.find(_.id === p.id).fold(0.pure[F])(_ => 1.pure[F])

}
