package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import play.api.libs.json._
import budget.support._

object Category extends CategoryGen {

  def json = Json.toJson(list(999).map(_.toJson))

}

// GENERATED case class start
case class Category(
  id: Pk[Int] = NA,
  name: String = "",
  subcats: PGStringList = Nil
) extends CategoryCCGen with Entity[Category]
// GENERATED case class end
{
  def toJson = Json.obj(
    "id" -> id.get,
    "name" -> name
  )
}

// GENERATED object start
trait CategoryGen extends EntityCompanion[Category] {
  val simple = {
    get[Pk[Int]]("category_id") ~
    get[String]("category_name") ~
    get[PGStringList]("category_subcats") map {
      case id~name~subcats =>
        Category(id, name, subcats)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from categorys where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Category] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Category] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Category] = findOne("category_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Category] = DB.withConnection { implicit c =>
    SQL("select * from categorys limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Category): Option[Category] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into categorys (
            category_id,
            category_name,
            category_subcats
          ) VALUES (
            DEFAULT,
            {name},
            {subcats}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'subcats -> o.subcats
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into categorys (
            category_id,
            category_name,
            category_subcats
          ) VALUES (
            {id},
            {name},
            {subcats}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'subcats -> o.subcats
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Category): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update categorys set
        category_name={name},
        category_subcats={subcats}
      where category_id={id}
    """).on(
      'id -> o.id,
      'name -> o.name,
      'subcats -> o.subcats
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from categorys where category_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait CategoryCCGen {
  val companion = Category
}
// GENERATED object end

