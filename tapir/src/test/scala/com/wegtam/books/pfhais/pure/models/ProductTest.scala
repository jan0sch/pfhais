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

import cats.implicits._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.pure.models.TypeGenerators._
import io.circe.parser._
import io.circe.syntax._

class ProductTest extends BaseSpec {
  "Product" when {
    "decoding from JSON" when {
      "JSON format is invalid" must {
        "return an error" in {
          forAll("input") { s: String =>
            decode[Product](s).isLeft must be(true)
          }
        }
      }

      "JSON format is valid" when {
        "data is invalid" must {
          "return an error" in {
            forAll("id", "names") { (id: String, ns: List[String]) =>
              val json = """{"id":""" + id.asJson.noSpaces + ""","names":""" + ns.asJson.noSpaces + """}"""
              decode[Product](json).isLeft must be(true)
            }
          }
        }

        "data is valid" must {
          "return the correct types" in {
            forAll("input") { i: Product =>
              val json = s"""{
                |"id": ${i.id.asJson.noSpaces},
                |"names": ${i.names.asJson.noSpaces}
                |}""".stripMargin
              withClue(s"Unable to decode JSON: $json") {
                decode[Product](json) match {
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
        forAll("input") { i: Product =>
          val json = i.asJson.noSpaces
          json must include(s""""id":${i.id.asJson.noSpaces}""")
          json must include(s""""names":${i.names.asJson.noSpaces}""")
        }
      }

      "return decodeable JSON" in {
        forAll("input") { p: Product =>
          decode[Product](p.asJson.noSpaces) match {
            case Left(_)  => fail("Must be able to decode encoded JSON!")
            case Right(d) => withClue("Must decode the same product!")(d must be(p))
          }
        }
      }
    }

    "#fromDatabase" must {
      "create correct results" in {
        forAll("input") { p: Product =>
          val rows = p.names.toNonEmptyList.map(t => (p.id, t.lang, t.name)).toList
          Product.fromDatabase(rows) must contain(p)
        }
      }
    }

    "ordering" must {
      "sort by ID" in {
        forAll("products") { ps: List[Product] =>
          val expected = ps.map(_.id).sorted
          val sorted   = ps.sorted.map(_.id)
          sorted mustEqual expected
        }
      }
    }
  }
}
