package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._

object Rating extends RatingGen {

  def findByUserAndLeaf(user: User, leaf: Leaf): Option[Rating] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM ratings
      WHERE user_id = {userId}
      AND leaf_id = {leafId}
    """).on(
      'userId -> user.id,
      'leafId -> leaf.id
    ).singleOpt(simple)
  }

}

// GENERATED case class start
case class Rating(
  id: Pk[Int] = NA,
  userId: Int = 0,
  leafId: Int = 0,
  stars: Int = 0
) extends RatingCCGen with Entity[Rating]
// GENERATED case class end
{
  lazy val leaf = Leaf.findById(leafId).get
}

// GENERATED object start
trait RatingGen extends EntityCompanion[Rating] {
  val simple = {
    get[Pk[Int]]("rating_id") ~
    get[Int]("user_id") ~
    get[Int]("leaf_id") ~
    get[Int]("rating_stars") map {
      case id~userId~leafId~stars =>
        Rating(id, userId, leafId, stars)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from ratings where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Rating] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Rating] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Rating] = findOne("rating_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Rating] = DB.withConnection { implicit c =>
    SQL("select * from ratings limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Rating): Option[Rating] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into ratings (
            rating_id,
            user_id,
            leaf_id,
            rating_stars
          ) VALUES (
            DEFAULT,
            {userId},
            {leafId},
            {stars}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'stars -> o.stars
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into ratings (
            rating_id,
            user_id,
            leaf_id,
            rating_stars
          ) VALUES (
            {id},
            {userId},
            {leafId},
            {stars}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'stars -> o.stars
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Rating): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update ratings set
        user_id={userId},
        leaf_id={leafId},
        rating_stars={stars}
      where rating_id={id}
    """).on(
      'id -> o.id,
      'userId -> o.userId,
      'leafId -> o.leafId,
      'stars -> o.stars
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from ratings where rating_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait RatingCCGen {
  val companion = Rating
}
// GENERATED object end

