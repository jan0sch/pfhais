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

package com.wegtam.books.pfhais.pure.db

import cats.effect._
import com.wegtam.books.pfhais.BaseSpec
import com.wegtam.books.pfhais.pure.config._
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException

final class FlywayDatabaseMigratorTest extends BaseSpec {

  override def beforeEach(): Unit = {
    dbConfig.foreach { cfg =>
      val flyway: Flyway = Flyway.configure().dataSource(cfg.url, cfg.user, cfg.pass).load()
      val _ = flyway.migrate()
      flyway.clean()
    }
  }

  override def afterEach(): Unit = {
    dbConfig.foreach { cfg =>
      val flyway: Flyway = Flyway.configure().dataSource(cfg.url, cfg.user, cfg.pass).load()
      flyway.clean()
    }
  }

  "FlywayDatabaseMigrator#migrate" when {
    "the database is configured and available" when {
      "the database is not up to date" must {
        "return the number of applied migrations" in {
          dbConfig.map { cfg =>
            val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
            val program = migrator.migrate(cfg.url, cfg.user, cfg.pass)
            program.unsafeRunSync must be > 0
          }
        }
      }

      "the database is up to date" must {
        "return zero" in {
          dbConfig.map { cfg =>
            val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
            val program = migrator.migrate(cfg.url, cfg.user, cfg.pass)
            val _ = program.unsafeRunSync
            program.unsafeRunSync must be(0)
          }
        }
      }
    }

    "the database is not available" must {
      "throw an exception" in {
        val cfg = DatabaseConfig(
          driver = "This is no driver name!",
          url = "jdbc://some.host/whatever",
          user = "no-user",
          pass = "no-password"
        )
        val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
        val program = migrator.migrate(cfg.url, cfg.user, cfg.pass)
        an[FlywayException] must be thrownBy program.unsafeRunSync
      }
    }
  }

}
