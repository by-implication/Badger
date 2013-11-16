package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._

object Location extends LocationGen {
}

// GENERATED case class start
case class Location(
  id: Pk[Int] = NA,
  name: Option[String] = None,
  parent: Option[Int] = None
) extends LocationCCGen with Entity[Location]
// GENERATED case class end

// GENERATED object start
trait LocationGen extends EntityCompanion[Location] {
  val simple = {
    get[Pk[Int]]("location_id") ~
    get[Option[String]]("location_name") ~
    get[Option[Int]]("location_parent") map {
      case id~name~parent =>
        Location(id, name, parent)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from locations where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Location] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Location] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Location] = findOne("location_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Location] = DB.withConnection { implicit c =>
    SQL("select * from locations limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Location): Option[Location] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into locations (
            location_id,
            location_name,
            location_parent
          ) VALUES (
            DEFAULT,
            {name},
            {parent}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'parent -> o.parent
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into locations (
            location_id,
            location_name,
            location_parent
          ) VALUES (
            {id},
            {name},
            {parent}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'parent -> o.parent
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Location): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update locations set
        location_name={name},
        location_parent={parent}
      where location_id={id}
    """).on(
      'id -> o.id,
      'name -> o.name,
      'parent -> o.parent
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from locations where location_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait LocationCCGen {
  val companion = Location
}
// GENERATED object end

