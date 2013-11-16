package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._

object Stuff extends StuffGen {
}

// GENERATED case class start
case class Stuff(
  id: Pk[Int] = NA,
  content: Option[String] = None
) extends StuffCCGen with Entity[Stuff]
// GENERATED case class end

// GENERATED object start
trait StuffGen extends EntityCompanion[Stuff] {
  val simple = {
    get[Pk[Int]]("id") ~
    get[Option[String]]("content") map {
      case id~content =>
        Stuff(id, content)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from stuff where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Stuff] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Stuff] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Stuff] = findOne("id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Stuff] = DB.withConnection { implicit c =>
    SQL("select * from stuff limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Stuff): Option[Stuff] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into stuff (
            id,
            content
          ) VALUES (
            DEFAULT,
            {content}
          )
        """).on(
          'id -> o.id,
          'content -> o.content
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into stuff (
            id,
            content
          ) VALUES (
            {id},
            {content}
          )
        """).on(
          'id -> o.id,
          'content -> o.content
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Stuff): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update stuff set
        content={content}
      where id={id}
    """).on(
      'id -> o.id,
      'content -> o.content
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from stuff where id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait StuffCCGen {
  val companion = Stuff
}
// GENERATED object end

