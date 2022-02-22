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

import com.wegtam.books.pfhais.tapir._
import org.scalacheck.{ Arbitrary, Gen }

object DatabaseConfigGenerators {
  val DefaultPassword: DatabasePassword = DatabasePassword.unsafeFrom("secret")

  val genDatabaseConfig: Gen[DatabaseConfig] = for {
    gp <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    p = DatabasePassword.from(gp.mkString).toOption.getOrElse(DefaultPassword)
  } yield DatabaseConfig(
    driver = NonEmptyString.unsafeFrom("org.postgresql.Driver"),
    url = DatabaseUrl.unsafeFrom("jdbc:postgresql://localhost:5422/test-database"),
    user = DatabaseLogin.unsafeFrom("tapir"),
    pass = p
  )

  implicit val arbitraryDatabaseConfig: Arbitrary[DatabaseConfig] = Arbitrary(genDatabaseConfig)

}
