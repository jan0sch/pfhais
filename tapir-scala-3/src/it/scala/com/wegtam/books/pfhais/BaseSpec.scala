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

import cats.effect.unsafe.IORuntime
import com.typesafe.config._
import com.wegtam.books.pfhais.tapir.config._
import eu.timepit.refined.auto._
import org.scalatest._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pureconfig._

/** A base class for our integration tests.
  */
abstract class BaseSpec
    extends AnyWordSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  protected val config   = ConfigFactory.load()
  protected val dbConfig = ConfigSource.fromConfig(config).at("database").load[DatabaseConfig]

  implicit val runtime: IORuntime = IORuntime.global

  override def beforeAll(): Unit = {
    val _ = withClue("Database configuration could not be loaded!") {
      dbConfig.isRight must be(true)
    }
  }
}
