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

import com.wegtam.books.pfhais.pure.{ NonEmptyString, PortNumber }
import eu.timepit.refined.api.RefType
import eu.timepit.refined.auto._
import org.scalacheck.{ Arbitrary, Gen }

object ApiConfigGenerators {
  val DefaultHost: NonEmptyString = "api.example.com"
  val DefaultPort: PortNumber     = 1234

  val genApiConfig: Gen[ApiConfig] = for {
    gh <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    gp <- Gen.choose(1, 65535)
    h = RefType.applyRef[NonEmptyString](gh.mkString).getOrElse(DefaultHost)
    p = RefType.applyRef[PortNumber](gp).getOrElse(DefaultPort)
  } yield ApiConfig(host = h, port = p)

  implicit val arbitraryApiConfig: Arbitrary[ApiConfig] = Arbitrary(genApiConfig)

}
