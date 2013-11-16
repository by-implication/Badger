package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._

object Click extends ClickGen {

  def findByUserAndLeaf(user: User, leaf: Leaf): Option[Click] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM clicks
      WHERE user_id = {userId}
      AND leaf_id = {leafId}
    """).on(
      'userId -> user.id,
      'leafId -> leaf.id
    ).singleOpt(simple)
  }

}

// GENERATED case class start
case class Click(
  id: Pk[Int] = NA,
  userId: Int = 0,
  leafId: Int = 0,
  lat: BigDecimal = 0,
  lng: BigDecimal = 0
) extends ClickCCGen with Entity[Click]
// GENERATED case class end

// GENERATED object start
trait ClickGen extends EntityCompanion[Click] {
  val simple = {
    get[Pk[Int]]("click_id") ~
    get[Int]("user_id") ~
    get[Int]("leaf_id") ~
    get[java.math.BigDecimal]("click_lat") ~
    get[java.math.BigDecimal]("click_lng") map {
      case id~userId~leafId~lat~lng =>
        Click(id, userId, leafId, lat, lng)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from clicks where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Click] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Click] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Click] = findOne("click_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Click] = DB.withConnection { implicit c =>
    SQL("select * from clicks limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Click): Option[Click] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into clicks (
            click_id,
            user_id,
            leaf_id,
            click_lat,
            click_lng
          ) VALUES (
            DEFAULT,
            {userId},
            {leafId},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into clicks (
            click_id,
            user_id,
            leaf_id,
            click_lat,
            click_lng
          ) VALUES (
            {id},
            {userId},
            {leafId},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Click): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update clicks set
        user_id={userId},
        leaf_id={leafId},
        click_lat={lat},
        click_lng={lng}
      where click_id={id}
    """).on(
      'id -> o.id,
      'userId -> o.userId,
      'leafId -> o.leafId,
      'lat -> o.lat.bigDecimal,
      'lng -> o.lng.bigDecimal
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from clicks where click_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait ClickCCGen {
  val companion = Click
}
// GENERATED object end

