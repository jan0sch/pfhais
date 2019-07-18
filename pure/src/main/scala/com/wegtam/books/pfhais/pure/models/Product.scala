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

package com.wegtam.books.pfhais.pure.models

import java.util.UUID

import cats.Semigroup
import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import io.circe._
import io.circe.generic.semiauto._

/**
  * A product.
  *
  * @param id    The unique ID of the product.
  * @param names A list of translations of the product name.
  */
final case class Product(id: ProductId, names: NonEmptyList[Translation])

object Product {

  implicit val decode: Decoder[Product] = deriveDecoder[Product]

  implicit val encode: Encoder[Product] = deriveEncoder[Product]

  implicit val semigroupO: Semigroup[Option[Product]] = new Semigroup[Option[Product]] {
    override def combine(x: Option[Product], y: Option[Product]): Option[Product] = (x, y) match {
      case (Some(a), Some(b)) =>
        if (a.id === b.id)
          a.copy(names = a.names ::: b.names).some
        else
          None
      case (Some(a), None) => a.some
      case (None, Some(b)) => b.some
      case (None, None)    => None
    }
  }

  /**
    * Try to create a Product from the given list of database rows.
    *
    * @param rows The database rows describing a product and its translations.
    * @return An option to the successfully created Product.
    */
  def fromDatabase(rows: Seq[(UUID, LanguageCode, ProductName)]): Option[Product] = {
    val po = for {
      (id, c, n) <- rows.headOption
      t = Translation(lang = c, name = n)
      p <- Product(id = id, names = NonEmptyList.one(t)).some
    } yield p
    po.map(
      p =>
        rows.drop(1).foldLeft(p) { (a, cols) =>
          val (id, c, n) = cols
          a.copy(names = a.names :+ Translation(lang = c, name = n))
      }
    )
  }

}
