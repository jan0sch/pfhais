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

package com.wegtam.books.pfhais.tapir

import eu.timepit.refined.api._
import eu.timepit.refined.string._
import shapeless.Witness

/** A type class to extract a regular expression value from a refined type.
  */
trait RefinedExtract[T] {

  /** Returns the regular expression of a refined type of the kind `MatchesRegex`.
    *
    * @return
    *   A string containing the regular expression.
    */
  def regex: String
}

object RefinedExtract {
  implicit def instance[T, S <: String](implicit
      ev: String Refined MatchesRegex[S] =:= T,
      ws: Witness.Aux[S]
  ): RefinedExtract[T] = new RefinedExtract[T] { val regex = ws.value }
}
