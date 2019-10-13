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

package com.wegtam.books.pfhais.impure.db

import java.util.UUID

import cats.data._
import com.wegtam.books.pfhais.impure.models._
import eu.timepit.refined.auto._
import slick.basic._
import slick.jdbc._

import scala.concurrent.Future

// Wartremover seems to report different on scala 2.13.
@SuppressWarnings(Array("org.wartremover.warts.Any"))
final class Repository(val dbConfig: DatabaseConfig[JdbcProfile]) {
  import dbConfig.profile.api._

  /**
    * The Slick table definition for the products table.
    *
    * @param tag A tag marks a specific row represented by an AbstractTable instance.
    */
  final class Products(tag: Tag) extends Table[(UUID)](tag, "products") {
    def id = column[UUID]("id", O.PrimaryKey)

    def * = (id)
  }
  val productsTable = TableQuery[Products]

  /**
    * The Slick table definition for the table with the translations of the product
    * names.
    *
    * @param tag A tag marks a specific row represented by an AbstractTable instance.
    */
  final class Names(tag: Tag) extends Table[(UUID, String, String)](tag, "names") {
    def productId = column[UUID]("product_id")
    def langCode  = column[String]("lang_code")
    def name      = column[String]("name")

    def pk = primaryKey("names_pk", (productId, langCode))
    def productFk =
      foreignKey("names_product_id_fk", productId, productsTable)(
        _.id,
        onDelete = ForeignKeyAction.Cascade,
        onUpdate = ForeignKeyAction.Cascade
      )

    def * = (productId, langCode, name)
  }
  val namesTable = TableQuery[Names]

  /**
    * Close the underlying database connection.
    */
  def close(): Unit = dbConfig.db.close

  /**
    * Load a product from the database repository.
    *
    * @param id The unique ID of the product.
    * @return A future holding a list of database rows for a single product
    *         which you'll need to combine.
    */
  def loadProduct(id: ProductId): Future[Seq[(UUID, String, String)]] = {
    val program = for {
      (p, ns) <- productsTable
        .filter(_.id === id)
        .join(namesTable)
        .on(_.id === _.productId)
    } yield (p.id, ns.langCode, ns.name)
    dbConfig.db.run(program.result)
  }

  /**
    * Load all products from the database repository.
    *
    * @return A future holding a list of database rows which you'll need to combine.
    */
  def loadProducts(): DatabasePublisher[(UUID, String, String)] = {
    val program = for {
      (p, ns) <- productsTable.join(namesTable).on(_.id === _.productId).sortBy(_._1.id)
    } yield (p.id, ns.langCode, ns.name)
    dbConfig.db.stream(program.result)
  }

  /**
    * Save the given product in the database.
    *
    * @param p A product to be saved.
    * @return A future holding a list of affected database rows (product + translations).
    */
  def saveProduct(p: Product): Future[List[Int]] = {
    val cp      = productsTable += (p.id)
    val program = DBIO.sequence(cp :: saveTranslations(p).toList).transactionally
    dbConfig.db.run(program)
  }

  /**
    * Update the given product in the database.
    *
    * @param p The product to be updated.
    * @return A future holding a list of affected database rows.
    */
  def updateProduct(p: Product): Future[List[Int]] = {
    val program = namesTable
      .filter(_.productId === p.id)
      .delete
      .andThen(DBIO.sequence(saveTranslations(p).toList))
      .transactionally
    dbConfig.db.run(program)
  }

  /**
    * Save the translations of the given product in the database, overwriting
    * possibly existing ones.
    *
    * @param p A product which must already exist in the database.
    * @return A list of composable sql queries for Slick.
    */
  protected def saveTranslations(p: Product): NonEmptyList[DBIO[Int]] = {
    val save = saveTranslation(p.id)(_)
    p.names.toNonEmptyList.map(t => save(t))
  }

  /**
    * Create a query to insert or update a given translation in the database.
    *
    * @param id The unique ID of the product.
    * @param t  The translation to be saved.
    * @return A composable sql query for Slick.
    */
  protected def saveTranslation(id: ProductId)(t: Translation): DBIO[Int] =
    namesTable.insertOrUpdate((id, t.lang, t.name))

}
