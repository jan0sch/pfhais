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
import com.wegtam.books.pfhais.pure.config.ApiConfigGenerators._
import eu.timepit.refined.auto._
import pureconfig.loadConfig

class ApiConfigTest extends BaseSpec {

  "ApiConfig" when {
    "loading invalid config format" must {
      "fail" in {
        val config = ConfigFactory.parseString("{}")
        loadConfig[ApiConfig](config, "api") match {
          case Left(_)  => succeed
          case Right(_) => fail("Loading an invalid config must fail!")
        }
      }
    }

    "loading valid config format" when {
      "settings are invalid" must {
        "fail" in {
          forAll("port") { i: Int =>
            whenever(i < 1 || i > 65535) {
              val config = ConfigFactory.parseString(s"""api{"host":"","port":$i}""")
              loadConfig[ApiConfig](config, "api") match {
                case Left(_)  => succeed
                case Right(_) => fail("Loading a config with invalid settings must fail!")
              }
            }
          }
        }
      }

      "settings are valid" must {
        "load correct settings" in {
          forAll("input") { expected: ApiConfig =>
            val config =
              ConfigFactory.parseString(
                s"""api{"host":"${expected.host}","port":${expected.port}}"""
              )
            loadConfig[ApiConfig](config, "api") match {
              case Left(e)  => fail(s"Parsing a valid configuration must succeed! ($e)")
              case Right(c) => c must be(expected)
            }
          }
        }
      }
    }
  }

}
