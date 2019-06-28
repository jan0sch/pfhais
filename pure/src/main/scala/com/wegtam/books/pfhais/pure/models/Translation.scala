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

import eu.timepit.refined.api._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._

/**
  * The translation data for a product name.
  *
  * @param lang A language code specifying the target translation.
  * @param name The product name in the language.
  */
final case class Translation(lang: LanguageCode, name: ProductName)

object Translation {

  /**
    * Try to create an instance of a Translation from unsafe input data.
    *
    * @param lang A string that must contain a valid LanguageCode.
    * @param name A string that must contain a valid ProductName.
    * @return An option to the successfully created Translation.
    */
  def fromUnsafe(lang: String)(name: String): Option[Translation] =
    for {
      l <- RefType.applyRef[LanguageCode](lang).toOption
      n <- RefType.applyRef[ProductName](name).toOption
    } yield Translation(lang = l, name = n)

  implicit val decode: Decoder[Translation] = deriveDecoder[Translation]

  implicit val encode: Encoder[Translation] = deriveEncoder[Translation]

}
