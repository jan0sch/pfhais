/*
 * CC0 1.0 Universal (CC0 1.0) - Public Domain Dedication
 *
 *                                No Copyright
 *
 * The person who associated a work with this deed has dedicated the work to
 * the public domain by waiving all of his or her rights to the work worldwide
 * under copyright law, including all related and neighboring rights, to the
 * extent allowed by law.
 import models.tracking.TrackPOPMode
 */

package com.wegtam.books.pfhais.db

import java.util.UUID

import akka.stream.scaladsl._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.impure.db.Repository
import com.wegtam.books.pfhais.impure.models._
import com.wegtam.books.pfhais.impure.models.TypeGenerators._
import eu.timepit.refined.auto._
import slick.basic._
import slick.jdbc._

import scala.concurrent.Future

class RepositoryTest extends BaseSpec {

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

  "#loadProduct" when {
    "the ID does not exist" must {
      "return an empty list of rows" in {
        val dbConfig: DatabaseConfig[JdbcProfile] =
          DatabaseConfig.forConfig("database", system.settings.config)
        val repo = new Repository(dbConfig)
        val id   = UUID.randomUUID
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
            val dbConfig: DatabaseConfig[JdbcProfile] =
              DatabaseConfig.forConfig("database", system.settings.config)
            val repo = new Repository(dbConfig)
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
        val dbConfig: DatabaseConfig[JdbcProfile] =
          DatabaseConfig.forConfig("database", system.settings.config)
        val repo = new Repository(dbConfig)
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
            val expected = ps.flatMap(p => p.names.toList.map(n => (p.id, n.lang, n.name)))
            val dbConfig: DatabaseConfig[JdbcProfile] =
              DatabaseConfig.forConfig("database", system.settings.config)
            val repo = new Repository(dbConfig)
            for {
              _ <- Future.sequence(ps.map(p => repo.saveProduct(p)))
              src = Source.fromPublisher(repo.loadProducts())
              rows <- src.runWith(Sink.seq)
            } yield {
              expected.foreach(e => rows must contain(e))
              rows must not be(empty)
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
            val dbConfig: DatabaseConfig[JdbcProfile] =
              DatabaseConfig.forConfig("database", system.settings.config)
            val repo = new Repository(dbConfig)
            for {
              cnts <- repo.saveProduct(p)
              rows <- repo.loadProduct(p.id)
            } yield {
              cnts.fold(0)(_ + _) must be(p.names.size + 1)
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
            val dbConfig: DatabaseConfig[JdbcProfile] =
              DatabaseConfig.forConfig("database", system.settings.config)
            val repo = new Repository(dbConfig)
            val p = b.copy(id = a.id)
            for {
              cnts <- repo.saveProduct(a)
              _    <- repo.saveProduct(p)
              rows <- repo.loadProduct(a.id)
            } yield {
              withClue("Already existing product was not be created!")(cnts.fold(0)(_ + _) must be(p.names.size + 1))
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
        fail("Not yet implemented!")
      }
    }

    "the product does not exist" must {
      "return an error and not change the database" in {
        fail("Not yet implemented!")
      }
    }
  }
}
