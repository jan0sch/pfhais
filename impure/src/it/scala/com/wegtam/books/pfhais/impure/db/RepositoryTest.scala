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

package com.wegtam.books.pfhais.db

import com.wegtam.books.pfhais.BaseSpec

class RepositoryTest extends BaseSpec {
  val databaseName = "repotest"

  override protected def beforeEach(): Unit = {
    java.sql.DriverManager.getConnection(s"jdbc:h2:mem:$databaseName")
    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    val connection = java.sql.DriverManager.getConnection(s"jdbc:h2:mem:$databaseName")
    val s          = connection.createStatement()
    s.execute("SHUTDOWN")
    connection.close()
    super.afterEach()
  }
}
