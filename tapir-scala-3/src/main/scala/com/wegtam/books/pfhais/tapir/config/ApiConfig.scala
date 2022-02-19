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

import cats.kernel.Eq
import cats.syntax.eq._
import com.comcast.ip4s.{ Host, Port }
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The configuration for our HTTP API.
  *
  * @param host The hostname or ip address on which the service shall listen.
  * @param port The port number on which the service shall listen.
  */
final case class ApiConfig(host: Host, port: Port)

object ApiConfig {
  implicit val eqApiConfig: Eq[ApiConfig] = Eq.instance { (a, b) =>
    a.host === b.host && a.port === b.port
  }

  implicit val hostReader: ConfigReader[Host] = ConfigReader.fromStringOpt[Host](Host.fromString)
  implicit val portReader: ConfigReader[Port] = ConfigReader.fromStringOpt[Port](Port.fromString)

  implicit val configReader: ConfigReader[ApiConfig] = deriveReader[ApiConfig]

}
