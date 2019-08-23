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

package com.wegtam.books.pfhais.impure.usecases

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.util.ByteString
import com.wegtam.books.pfhais.impure.models._
import com.wegtam.books.pfhais.impure.models.TypeGenerators._
import io.circe.syntax._

import scala.collection.immutable._

class SaveProduct extends BaseUseCaseSpec {
  private final val http = Http()

  /**
    * Before each test we clean and migrate the database.
    */
  override protected def beforeEach(): Unit = {
    flyway.clean()
    val _ = flyway.migrate()
    super.beforeEach()
  }

  /**
    * After each test we clean the database again.
    */
  override protected def afterEach(): Unit = {
    flyway.clean()
    super.afterEach()
  }

  "Saving a Product" when {
    "the posted JSON is invalid" must {
      val expectedStatus = StatusCodes.BadRequest

      s"return $expectedStatus" in {
        for {
          resp <- http.singleRequest(
            HttpRequest(
              method = HttpMethods.POST,
              uri = s"$baseUrl/products",
              headers = Seq(),
              entity = HttpEntity(
                contentType = ContentTypes.`application/json`,
                data = ByteString(scala.util.Random.alphanumeric.take(256).mkString)
              )
            )
          )
        } yield {
          resp.status must be(expectedStatus)
        }
      }
    }

    "the posted JSON is valid" when {
      "the product does exist" must {
        val expectedStatus = StatusCodes.InternalServerError

        s"return $expectedStatus and not save the Product" in {
          (genProduct.sample, genProduct.sample) match {
            case (Some(a), Some(b)) =>
              val p = b.copy(id = a.id)
              for {
                _    <- repo.saveProduct(a)
                rows <- repo.loadProduct(a.id)
                resp <- http.singleRequest(
                  HttpRequest(
                    method = HttpMethods.POST,
                    uri = s"$baseUrl/products",
                    headers = Seq(),
                    entity = HttpEntity(
                      contentType = ContentTypes.`application/json`,
                      data = ByteString(p.asJson.noSpaces)
                    )
                  )
                )
                rows2 <- repo.loadProduct(a.id)
              } yield {
                withClue("Seeding product data failed!")(rows must not be(empty))
                resp.status must be(expectedStatus)
                Product.fromDatabase(rows2) match {
                  case None    => fail("Seeding product was not saved to database!")
                  case Some(s) => withClue("Existing product must not be changed!")(s mustEqual a)
                }
              }
            case _ => fail("Could not generate data sample!")
          }
        }
      }

      "the product does not exist" must {
        val expectedStatus = StatusCodes.OK

        s"return $expectedStatus and save the Product" in {
          genProduct.sample match {
            case None => fail("Could not generate data sample!")
            case Some(p) =>
              for {
                resp <- http.singleRequest(
                  HttpRequest(
                    method = HttpMethods.POST,
                    uri = s"$baseUrl/products",
                    headers = Seq(),
                    entity = HttpEntity(
                      contentType = ContentTypes.`application/json`,
                      data = ByteString(p.asJson.noSpaces)
                    )
                  )
                )
                rows <- repo.loadProduct(p.id)
              } yield {
                resp.status must be(expectedStatus)
                Product.fromDatabase(rows) match {
                  case None    => fail("Product was not saved to database!")
                  case Some(s) => s mustEqual p
                }
              }
          }
        }
      }
    }
  }
}

