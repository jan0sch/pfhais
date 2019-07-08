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

import cats.effect.Sync
import com.wegtam.books.pfhais.pure.models._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.refined.implicits._
import eu.timepit.refined.auto._

import scala.collection.immutable._

/**
  * The doobie implementation of our repository.
  *
  * @param tx A transactor for actually executing our queries.
  */
final class DoobieRepository[F[_]: Sync](tx: Transactor[F]) extends Repository[F] {

  /**
    * Load a product from the database repository.
    *
    * @param id The unique ID of the product.
    * @return A list of database rows for a single product which you'll need to combine.
    */
  def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]] =
    sql"SELECT products.id, names.lang_code, names.name FROM products JOIN names ON products.id = names.product_id WHERE products.id = $id"
      .query[(ProductId, LanguageCode, ProductName)]
      .to[Seq]
      .transact(tx)

  /**
    * Load all products from the database repository.
    *
    * @return A list of database rows which you'll need to combine.
    */
  def loadProducts(): F[Seq[(ProductId, LanguageCode, ProductName)]] = ???

  /**
    * Save the given product in the database.
    *
    * @param p A product to be saved.
    * @return A list of affected database rows (product + translations).
    */
  def saveProduct(p: Product): F[Seq[Int]] = ???

  /**
    * Update the given product in the database.
    *
    * @param p The product to be updated.
    * @return A list of affected database rows.
    */
  def updateProduct(p: Product): F[Seq[Int]] = ???

}
