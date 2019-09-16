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

import org.scalatest.{ MustMatchers, WordSpec }
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/**
  * A base class for our tests.
  */
abstract class BaseSpec extends WordSpec with MustMatchers with ScalaCheckPropertyChecks {}
