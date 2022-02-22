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

import com.typesafe.config.ConfigValue
import com.wegtam.books.pfhais.tapir.{
  DatabaseLogin,
  DatabasePassword,
  DatabaseUrl,
  NonEmptyString
}
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import pureconfig._
import pureconfig.error._

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

  implicit def refTypeConfigConvert[F[_, _], T, P](
      implicit configConvert: ConfigConvert[T],
      refType: RefType[F],
      validate: Validate[T, P]
  ): ConfigConvert[F[T, P]] =
    new ConfigConvert[F[T, P]] {
      override def from(cur: ConfigCursor): ConfigReader.Result[F[T, P]] =
        configConvert.from(cur) match {
          case Left(es) => Left(es)
          case Right(t) =>
            refType.refine[P](t) match {
              case Left(because) =>
                Left(
                  ConfigReaderFailures(
                    ConvertFailure(
                      reason = CannotConvert(
                        value = cur.valueOpt.map(_.render()).getOrElse("none"),
                        toType = "a refined type",
                        because = because
                      ),
                      cur = cur
                    )
                  )
                )
              case Right(refined) => Right(refined)
            }
        }
      override def to(t: F[T, P]): ConfigValue =
        configConvert.to(refType.unwrap(t))
    }

  implicit val configReader: ConfigReader[DatabaseConfig] =
    ConfigReader.forProduct4("driver", "url", "user", "pass")(DatabaseConfig(_, _, _, _))

}
