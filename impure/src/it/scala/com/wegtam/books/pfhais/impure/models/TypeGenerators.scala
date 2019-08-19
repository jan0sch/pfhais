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

package com.wegtam.books.pfhais.impure.models

import java.util.UUID

import cats.data.NonEmptyList
import eu.timepit.refined.auto._
import eu.timepit.refined.api._
import org.scalacheck.{ Arbitrary, Gen }

object TypeGenerators {

  val DefaultProductName: ProductName = "I am a product name!"

  val genLanguageCode: Gen[LanguageCode] = Gen.oneOf(LanguageCodes.all)

  def genUuid: Gen[UUID] = Gen.delay(UUID.randomUUID)

  val genProductId: Gen[ProductId] = genUuid

  val genProductName: Gen[ProductName] = for {
    cs <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    name = RefType.applyRef[ProductName](cs.mkString).getOrElse(DefaultProductName)
  } yield name

  val genTranslation: Gen[Translation] = for {
    c <- genLanguageCode
    n <- genProductName
  } yield
    Translation(
      lang = c,
      name = n
    )

  implicit val arbitraryTranslation: Arbitrary[Translation] = Arbitrary(genTranslation)

  val genTranslationList: Gen[List[Translation]] = for {
    ts <- Gen.nonEmptyListOf(genTranslation)
  } yield ts

  val genNonEmptyTranslationList: Gen[NonEmptyList[Translation]] = for {
    t  <- genTranslation
    ts <- genTranslationList
    ns = NonEmptyList.fromList(ts)
  } yield ns.getOrElse(NonEmptyList.of(t))

  val genProduct: Gen[Product] = for {
    id <- genProductId
    ts <- genNonEmptyTranslationList
  } yield
    Product(
      id = id,
      names = ts
    )

  implicit val arbitraryProduct: Arbitrary[Product] = Arbitrary(genProduct)

  val genProducts: Gen[List[Product]] = Gen.nonEmptyListOf(genProduct)

}
