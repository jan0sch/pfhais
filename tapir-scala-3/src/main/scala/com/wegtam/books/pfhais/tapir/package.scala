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

package com.wegtam.books.pfhais

import eu.timepit.refined.api._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._

package object tapir {

  // A string containing a database login which must be non empty.
  type DatabaseLogin = String Refined NonEmpty
  object DatabaseLogin extends RefinedTypeOps[DatabaseLogin, String] with CatsRefinedTypeOpsSyntax
  // A string containing a database password which must be non empty.
  type DatabasePassword = String Refined NonEmpty
  object DatabasePassword
      extends RefinedTypeOps[DatabasePassword, String]
      with CatsRefinedTypeOpsSyntax
  // A string containing a database url.
  type DatabaseUrl = String Refined Uri
  object DatabaseUrl extends RefinedTypeOps[DatabaseUrl, String] with CatsRefinedTypeOpsSyntax
  // A string that must not be empty.
  type NonEmptyString = String Refined NonEmpty
  object NonEmptyString extends RefinedTypeOps[NonEmptyString, String] with CatsRefinedTypeOpsSyntax
  // A TCP port number which is valid in the range of 1 to 65535.
  type PortNumber = Int Refined Interval.Closed[1, 65535]
  object PortNumber extends RefinedTypeOps[PortNumber, Int] with CatsRefinedTypeOpsSyntax

}
