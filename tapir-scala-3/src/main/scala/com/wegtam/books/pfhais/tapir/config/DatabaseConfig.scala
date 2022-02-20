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

package com.wegtam.books.pfhais.tapir.config

import com.wegtam.books.pfhais.tapir.{
  DatabaseLogin,
  DatabasePassword,
  DatabaseUrl,
  NonEmptyString
}
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import pureconfig._

/**
  * The configuration for our database connection.
  *
  * @param driver The class name of the driver to use.
  * @param url    The JDBC connection url (driver specific).
  * @param user   The username for the database connection.
  * @param pass   The password for the database connection.
  */
final case class DatabaseConfig(
    driver: NonEmptyString,
    url: DatabaseUrl,
    user: DatabaseLogin,
    pass: DatabasePassword
)

object DatabaseConfig {

  implicit val loginReader: ConfigReader[DatabaseLogin] =
    ConfigReader.fromStringOpt(s => DatabaseLogin.from(s).toOption)
  implicit val passReader: ConfigReader[DatabasePassword] =
    ConfigReader.fromStringOpt(s => DatabasePassword.from(s).toOption)
  implicit val urlReader: ConfigReader[DatabaseUrl] =
    ConfigReader.fromStringOpt(s => DatabaseUrl.from(s).toOption)

  implicit val configReader: ConfigReader[DatabaseConfig] =
    ConfigReader.forProduct4("driver", "url", "user", "pass")(DatabaseConfig(_, _, _, _))

}
