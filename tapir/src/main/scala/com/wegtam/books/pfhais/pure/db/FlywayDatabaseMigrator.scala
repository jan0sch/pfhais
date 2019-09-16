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

import cats.effect.IO
import com.wegtam.books.pfhais.pure.{ DatabaseLogin, DatabasePassword, DatabaseUrl }
import eu.timepit.refined.auto._
import org.flywaydb.core.Flyway

/**
  * An implementation of the database migrator using Flyway and IO.
  */
final class FlywayDatabaseMigrator extends DatabaseMigrator[IO] {

  /**
    * Apply pending migrations to the database.
    *
    * @param url  A JDBC database connection url.
    * @param user The login name for the connection.
    * @param pass The password for the connection.
    * @return The number of applied migrations.
    */
  override def migrate(url: DatabaseUrl, user: DatabaseLogin, pass: DatabasePassword): IO[Int] =
    IO {
      val flyway: Flyway = Flyway.configure().dataSource(url, user, pass).load()
      flyway.migrate()
    }

}
