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

import com.typesafe.config._
import com.wegtam.books.pfhais.pure.config._
import eu.timepit.refined.auto._
import pureconfig.loadConfig
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

/**
  * A base class for our integration tests.
  */
abstract class BaseSpec extends WordSpec 
    with MustMatchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  protected val config = ConfigFactory.load()
  protected val dbConfig = loadConfig[DatabaseConfig](config, "database")

  override def beforeAll(): Unit = {
    val _ = withClue("Database configuration could not be loaded!") {
      dbConfig.isRight must be(true)
    }
  }
}
