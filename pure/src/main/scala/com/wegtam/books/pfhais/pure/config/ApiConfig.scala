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
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import pureconfig._
import pureconfig.generic.semiauto._

/**
  * The configuration for our HTTP API.
  *
  * @param host The hostname or ip address on which the service shall listen.
  * @param port The port number on which the service shall listen.
  */
final case class ApiConfig(host: NonEmptyString, port: PortNumber)

object ApiConfig {

  implicit val configReader: ConfigReader[ApiConfig] = deriveReader[ApiConfig]

}
