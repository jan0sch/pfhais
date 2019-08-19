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

import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.impure.db.Repository
import com.wegtam.books.pfhais.impure.models._
import com.wegtam.books.pfhais.impure.models.TypeGenerators._
import org.flywaydb.core.Flyway
import slick.basic._
import slick.jdbc._

class RepositoryTest extends BaseSpec {

  /**
    * Before each test we clean and migrate the database.
    */
  override protected def beforeEach(): Unit = {
    val url = "jdbc:postgresql://" +
      system.settings.config.getString("database.db.properties.serverName") +
      ":" + system.settings.config
        .getString("database.db.properties.portNumber") +
      "/" + system.settings.config
        .getString("database.db.properties.databaseName")
    val user = system.settings.config.getString("database.db.properties.user")
    val pass =
      system.settings.config.getString("database.db.properties.password")
    val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
    flyway.clean()
    val _ = flyway.migrate()
    super.beforeEach()
  }

  /**
    * After each test we clean the database again.
    */
  override protected def afterEach(): Unit = {
    val url = "jdbc:postgresql://" +
      system.settings.config.getString("database.db.properties.serverName") +
      ":" + system.settings.config
        .getString("database.db.properties.portNumber") +
      "/" + system.settings.config
        .getString("database.db.properties.databaseName")
    val user = system.settings.config.getString("database.db.properties.user")
    val pass =
      system.settings.config.getString("database.db.properties.password")
    val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
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
                case None    => fail("No product created from database rows!")
                case Some(c) => c must be(p)
              }
            }
        }
      }
    }
  }

  "#loadProducts" when {
    "no products exist" must {
      "return an empty stream" in {
        fail("Not yet implemented!")
      }
    }

    "some products exist" must {
      "return a stream with all product rows" in {
        fail("Not yet implemented!")
      }
    }
  }

  "#saveProduct" when {
    "the product does not already exist" must {
      "save the product to the database" in {
        fail("Not yet implemented!")
      }
    }

    "the product does already exist" must {
      "return an error and not change the database" in {
        fail("Not yet implemented!")
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
