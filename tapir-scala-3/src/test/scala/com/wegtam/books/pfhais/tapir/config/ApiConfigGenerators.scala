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

import com.comcast.ip4s._
import org.scalacheck.{ Arbitrary, Gen }

object ApiConfigGenerators {
  val DefaultHost: Host = host"127.0.0.1"
  val DefaultPort: Port = port"80"

  val genHost: Gen[Host] = Gen
    .nonEmptyListOf(Gen.alphaNumChar)
    .map(_.mkString)
    .map(Host.fromString)
    .map(_.getOrElse(DefaultHost))

  implicit val arbitraryHost: Arbitrary[Host] = Arbitrary(genHost)

  val genPort: Gen[Port] = Gen.choose(1, 65535).map(Port.fromInt).map(_.getOrElse(DefaultPort))

  implicit val arbitraryPort: Arbitrary[Port] = Arbitrary(genPort)

  val genApiConfig: Gen[ApiConfig] = for {
    h <- genHost
    p <- genPort
  } yield ApiConfig(host = h, port = p)

  implicit val arbitraryApiConfig: Arbitrary[ApiConfig] = Arbitrary(genApiConfig)

}
