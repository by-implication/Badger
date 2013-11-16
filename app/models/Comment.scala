package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.Play.current
import budget.support._
import play.api.libs.json._

object Comment extends CommentGen {

  def getForLeaf(id: Int): Seq[Comment] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM comments
      WHERE leaf_id = {id}
    """).on('id -> id).list(simple)
  }

}

// GENERATED case class start
case class Comment(
  id: Pk[Int] = NA,
  userId: Int = 0,
  leafId: Int = 0,
  content: Option[String] = None,
  timestamp: Timestamp = Time.now
) extends CommentCCGen with Entity[Comment]
// GENERATED case class end
{

  lazy val userName = User.findById(userId).get.handle

  lazy val toJson: JsObject = Json.obj(
    "user" -> userName,
    "content" -> content,
    "timestamp" -> timestamp
  )

}

// GENERATED object start
trait CommentGen extends EntityCompanion[Comment] {
  val simple = {
    get[Pk[Int]]("comment_id") ~
    get[Int]("user_id") ~
    get[Int]("leaf_id") ~
    get[Option[String]]("comment_content") ~
    get[Timestamp]("comment_timestamp") map {
      case id~userId~leafId~content~timestamp =>
        Comment(id, userId, leafId, content, timestamp)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from comments where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Comment] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Comment] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Comment] = findOne("comment_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Comment] = DB.withConnection { implicit c =>
    SQL("select * from comments limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Comment): Option[Comment] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into comments (
            comment_id,
            user_id,
            leaf_id,
            comment_content,
            comment_timestamp
          ) VALUES (
            DEFAULT,
            {userId},
            {leafId},
            {content},
            {timestamp}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'content -> o.content,
          'timestamp -> o.timestamp
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into comments (
            comment_id,
            user_id,
            leaf_id,
            comment_content,
            comment_timestamp
          ) VALUES (
            {id},
            {userId},
            {leafId},
            {content},
            {timestamp}
          )
        """).on(
          'id -> o.id,
          'userId -> o.userId,
          'leafId -> o.leafId,
          'content -> o.content,
          'timestamp -> o.timestamp
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Comment): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update comments set
        user_id={userId},
        leaf_id={leafId},
        comment_content={content},
        comment_timestamp={timestamp}
      where comment_id={id}
    """).on(
      'id -> o.id,
      'userId -> o.userId,
      'leafId -> o.leafId,
      'content -> o.content,
      'timestamp -> o.timestamp
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from comments where comment_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait CommentCCGen {
  val companion = Comment
}
// GENERATED object end

