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

package com.wegtam.books.pfhais.db

import java.util.UUID

import akka.stream.scaladsl._
import cats.implicits._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.impure.db.Repository
import com.wegtam.books.pfhais.impure.models._
import com.wegtam.books.pfhais.impure.models.TypeGenerators._
import eu.timepit.refined.auto._
import slick.basic._
import slick.jdbc._

import scala.concurrent.Future

class RepositoryTest extends BaseSpec {
  private val dbConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig("database", system.settings.config)
  private val repo = new Repository(dbConfig)

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

  /**
    * Close the database connection after the tests.
    */
  override protected def afterAll(): Unit = {
    repo.close()
    super.afterAll()
  }

  "#loadProduct" when {
    "the ID does not exist" must {
      "return an empty list of rows" in {
        val id = UUID.randomUUID
        for {
          rows <- repo.loadProduct(id)
        } yield {
          rows must be(empty)
        }
      }
    }

    "the ID exists" must {
      "return a list with all product rows" in {
        genProduct.sample match {
          case None => fail("Could not generate data sample!")
          case Some(p) =>
            for {
              _    <- repo.saveProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              Product.fromDatabase(rows) match {
                case None => fail("No product created from database rows!")
                case Some(c) =>
                  c.id must be(p.id)
                  c mustEqual p
              }
            }
        }
      }
    }
  }

  "#loadProducts" when {
    "no products exist" must {
      "return an empty stream" in {
        val src = Source.fromPublisher(repo.loadProducts())
        for {
          ps <- src.runWith(Sink.seq)
        } yield {
          ps must be(empty)
        }
      }
    }

    "some products exist" must {
      "return a stream with all product rows" in {
        genProducts.sample match {
          case None => fail("Could not generate data sample!")
          case Some(ps) =>
            val expected = ps.flatMap(p => p.names.toNonEmptyList.toList.map(n => (p.id, n.lang, n.name)))
            for {
              _ <- Future.sequence(ps.map(p => repo.saveProduct(p)))
              src = Source
                .fromPublisher(repo.loadProducts())
                .collect(
                  cs =>
                    Product.fromDatabase(Seq(cs)) match {
                      case Some(p) => p
                    }
                )
                .groupBy(Int.MaxValue, _.id)
                .fold(Option.empty[Product])(
                  (op, x) => op.fold(x.some)(p => p.copy(names = p.names ++ x.names).some)
                )
                .mergeSubstreams
                .collect(
                  op =>
                    op match {
                      case Some(p) => p
                    }
                )
              rows <- src.runWith(Sink.seq)
            } yield {
              rows must not be (empty)
              rows.size mustEqual ps.size
              rows.toList.sorted mustEqual ps.sorted
            }
        }
      }
    }
  }

  "#saveProduct" when {
    "the product does not already exist" must {
      "save the product to the database" in {
        genProduct.sample match {
          case None => fail("Could not generate data sample!")
          case Some(p) =>
            for {
              cnts <- repo.saveProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              withClue("Data missing from database!")(cnts.fold(0)(_ + _) must be(p.names.toNonEmptyList.size + 1))
              Product.fromDatabase(rows) match {
                case None => fail("No product created from database rows!")
                case Some(c) =>
                  c.id must be(p.id)
                  c mustEqual p
              }
            }
        }
      }
    }

    "the product does already exist" must {
      "return an error and not change the database" in {
        (genProduct.sample, genProduct.sample) match {
          case (Some(a), Some(b)) =>
            val p = b.copy(id = a.id)
            for {
              cnts <- repo.saveProduct(a)
              nosv <- repo.saveProduct(p).recover {
                case _ => 0
              }
              rows <- repo.loadProduct(a.id)
            } yield {
              withClue("Saving a duplicate product must fail!")(nosv must be(0))
              Product.fromDatabase(rows) match {
                case None => fail("No product created from database rows!")
                case Some(c) =>
                  c.id must be(a.id)
                  c mustEqual a
              }
            }
          case _ => fail("Could not create data sample!")
        }
      }
    }
  }

  "#updateProduct" when {
    "the product does exist" must {
      "update the database" in {
        (genProduct.sample, genProduct.sample) match {
          case (Some(a), Some(b)) =>
            val p = b.copy(id = a.id)
            for {
              cnts <- repo.saveProduct(a)
              upds <- repo.updateProduct(p)
              rows <- repo.loadProduct(a.id)
            } yield {
              withClue("Already existing product was not created!")(
                cnts.fold(0)(_ + _) must be(a.names.toNonEmptyList.size + 1)
              )
              Product.fromDatabase(rows) match {
                case None => fail("No product created from database rows!")
                case Some(c) =>
                  c.id must be(a.id)
                  c mustEqual p
              }
            }
          case _ => fail("Could not create data sample!")
        }
      }
    }

    "the product does not exist" must {
      "return an error and not change the database" in {
        genProduct.sample match {
          case None => fail("Could not generate data sample!")
          case Some(p) =>
            for {
              nosv <- repo.updateProduct(p).recover {
                case _ => 0
              }
              rows <- repo.loadProduct(p.id)
            } yield {
              withClue("Updating a not existing product must fail!")(nosv must be(0))
              withClue("Product must not exist in database!")(rows must be(empty))
            }
        }
      }
    }
  }
}
