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

package com.wegtam.books.pfhais.pure

import java.util.UUID

import cats._
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection._
import eu.timepit.refined.string._

package object models {

  // A language code format according to ISO 639-1. Please note that this only verifies the format!
  type LanguageCode = String Refined MatchesRegex[W.`"^[a-z]{2}$"`.T]
  object LanguageCode extends RefinedTypeOps[LanguageCode, String] with CatsRefinedTypeOpsSyntax
  // A product id which must be a valid UUID in version 4.
  type ProductId = UUID
  // A product name must be a non-empty string.
  type ProductName = String Refined NonEmpty
  object ProductName extends RefinedTypeOps[ProductName, String] with CatsRefinedTypeOpsSyntax

  implicit val orderLanguageCode: Order[LanguageCode] = new Order[LanguageCode] {
    def compare(x: LanguageCode, y: LanguageCode): Int = x.value.compare(y.value)
  }

  implicit val orderProductId: Order[ProductId] = new Order[ProductId] {
    def compare(x: ProductId, y: ProductId): Int = x.toString.compare(y.toString)
  }

  implicit val orderProductName: Order[ProductName] = new Order[ProductName] {
    def compare(x: ProductName, y: ProductName): Int = x.value.compare(y.value)
  }

}
