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

package com.wegtam.books.pfhais.pure.config

import com.wegtam.books.pfhais.pure._
import eu.timepit.refined.api.RefType
import eu.timepit.refined.auto._
import org.scalacheck.{ Arbitrary, Gen }

object DatabaseConfigGenerators {
  val DefaultPassword: DatabasePassword = "secret"

  val genDatabaseConfig: Gen[DatabaseConfig] = for {
    gp <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    p = RefType.applyRef[DatabasePassword](gp.mkString).getOrElse(DefaultPassword)
  } yield DatabaseConfig(
    driver = "org.postgresql.Driver",
    url = "jdbc:postgresql://localhost:5422/test-database",
    user = "pure",
    pass = p
  )

  implicit val arbitraryDatabaseConfig: Arbitrary[DatabaseConfig] = Arbitrary(genDatabaseConfig)

}
