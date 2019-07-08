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

import com.wegtam.books.pfhais.pure.models._
import fs2.Stream

import scala.collection.immutable.Seq

/**
  * A base class for our database repository.
  *
  * @tparam F A higher kinded type which wraps the actual return value.
  */
trait Repository[F[_]] {

  /**
    * Load a product from the database repository.
    *
    * @param id The unique ID of the product.
    * @return A list of database rows for a single product which you'll need to combine.
    */
  def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]]

  /**
    * Load all products from the database repository.
    *
    * @return A stream of database rows which you'll need to combine.
    */
  def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)]

  /**
    * Save the given product in the database.
    *
    * @param p A product to be saved.
    * @return The number of affected database rows (product + translations).
    */
  def saveProduct(p: Product): F[Int]

  /**
    * Update the given product in the database.
    *
    * @param p The product to be updated.
    * @return The number of affected database rows.
    */
  def updateProduct(p: Product): F[Int]

}
