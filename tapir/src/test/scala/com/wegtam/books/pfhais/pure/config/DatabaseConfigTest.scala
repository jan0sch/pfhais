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

package com.wegtam.books.pfhais.pure.config

import com.typesafe.config._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.pure.config.DatabaseConfigGenerators._
import eu.timepit.refined.auto._
import pureconfig.loadConfig

class DatabaseConfigTest extends BaseSpec {

  "DatabaseConfig" when {
    "loading invalid config format" must {
      "fail" in {
        val config = ConfigFactory.parseString("{}")
        loadConfig[DatabaseConfig](config, "database") match {
          case Left(_)  => succeed
          case Right(_) => fail("Loading an invalid config must fail!")
        }
      }
    }

    "loading valid config format" when {
      "settings are invalid" must {
        "fail" in {
          forAll("input") { i: Int =>
            val config = ConfigFactory.parseString(
              """database {
                  |  "driver":"",
                  |  "url":"",
                  |  "user": "",
                  |  "pass": ""
                  |}""".stripMargin
            )
            loadConfig[DatabaseConfig](config, "database") match {
              case Left(_)  => succeed
              case Right(_) => fail("Loading a config with invalid settings must fail!")
            }
          }
        }
      }

      "settings are valid" must {
        "load correct settings" in {
          forAll("input") { expected: DatabaseConfig =>
            val config = ConfigFactory.parseString(
              s"""database {
                   |  "driver": "${expected.driver}",
                   |  "url": "${expected.url}",
                   |  "user": "${expected.user}",
                   |  "pass": "${expected.pass}"
                   |}""".stripMargin
            )
            loadConfig[DatabaseConfig](config, "database") match {
              case Left(e)  => fail(s"Parsing a valid configuration must succeed! ($e)")
              case Right(c) => c must be(expected)
            }
          }
        }
      }
    }
  }

}
