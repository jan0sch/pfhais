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

import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.pure.models.TypeGenerators._
import eu.timepit.refined.api._
import io.circe.parser._
import io.circe.refined._
import io.circe.syntax._

class TranslationTest extends BaseSpec {
  "Translation" when {
    "decoding from JSON" when {
      "JSON format is invalid" must {
        "return an error" in {
          forAll("input") { s: String =>
            decode[Translation](s).isLeft must be(true)
          }
        }
      }

      "JSON format is valid" when {
        "data is invalid" must {
          "return an error" in {
            forAll("lang", "name") { (l: String, n: String) =>
              whenever(
                RefType
                  .applyRef[LanguageCode](l)
                  .toOption
                  .isEmpty || RefType.applyRef[ProductName](n).toOption.isEmpty
              ) {
                val json = """{"lang":""" + l.asJson.noSpaces + ""","name":""" + n.asJson.noSpaces + """}"""
                decode[Translation](json).isLeft must be(true)
              }
            }
          }
        }

        "data is valid" must {
          "return the correct types" in {
            forAll("input") { i: Translation =>
              val json = s"""{
                |"lang": ${i.lang.asJson.noSpaces},
                |"name": ${i.name.asJson.noSpaces}
                |}""".stripMargin
              withClue(s"Unable to decode JSON: $json") {
                decode[Translation](json) match {
                  case Left(e)  => fail(e.getMessage)
                  case Right(v) => v must be(i)
                }
              }
            }
          }
        }
      }
    }

    "encoding to JSON" must {
      "return correct JSON" in {
        forAll("input") { i: Translation =>
          val json = i.asJson.noSpaces
          json must include(s""""lang":${i.lang.asJson.noSpaces}""")
          json must include(s""""name":${i.name.asJson.noSpaces}""")
        }
      }

      "return decodeable JSON" in {
        forAll("input") { i: Translation =>
          decode[Translation](i.asJson.noSpaces) match {
            case Left(_)  => fail("Must be able to decode encoded JSON!")
            case Right(d) => withClue("Must decode the same product!")(d must be(i))
          }
        }
      }
    }
  }
}
