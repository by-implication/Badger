package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._

object Node extends NodeGen {

  def query(id: Int, comments: Boolean): Option[JsObject] = DB.withConnection { implicit c => 
    SQL("""
      SELECT * FROM nodes
      WHERE node_id = {nodeId}
    """).on('nodeId -> id).singleOpt(Node.simple)
    .map { n =>
      Json.obj("node" -> n.toJson(withComments = comments))
    }
  }

}

// GENERATED case class start
case class Node(
  id: Pk[Int] = NA,
  parent: Option[Int] = None,
  content: Option[String] = None
) extends NodeCCGen with Entity[Node]
// GENERATED case class end
{

  lazy val children: Seq[Node] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM nodes
      WHERE node_parent = {nodeId}
    """).on('nodeId -> id).list(Node.simple)
  }

  lazy val comments: Seq[Comment] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM comments NATURAL JOIN users
      WHERE node_id = {nodeId}
    """).on('nodeId -> id).list(Comment.simple)
  }

  def toJson(withComments: Boolean): JsObject = {

    var result = Json.obj(
      "id" -> id.get,
      "parent" -> parent,
      "content" -> content,
      "children" -> children.map(_.toJson(withComments = false))
    )

    if(withComments) result ++= Json.obj("comments" -> comments.map(_.toJson))

    result

  }

}

// GENERATED object start
trait NodeGen extends EntityCompanion[Node] {
  val simple = {
    get[Pk[Int]]("node_id") ~
    get[Option[Int]]("node_parent") ~
    get[Option[String]]("node_content") map {
      case id~parent~content =>
        Node(id, parent, content)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from nodes where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Node] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Node] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Node] = findOne("node_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Node] = DB.withConnection { implicit c =>
    SQL("select * from nodes limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Node): Option[Node] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into nodes (
            node_id,
            node_parent,
            node_content
          ) VALUES (
            DEFAULT,
            {parent},
            {content}
          )
        """).on(
          'id -> o.id,
          'parent -> o.parent,
          'content -> o.content
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into nodes (
            node_id,
            node_parent,
            node_content
          ) VALUES (
            {id},
            {parent},
            {content}
          )
        """).on(
          'id -> o.id,
          'parent -> o.parent,
          'content -> o.content
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Node): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update nodes set
        node_parent={parent},
        node_content={content}
      where node_id={id}
    """).on(
      'id -> o.id,
      'parent -> o.parent,
      'content -> o.content
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from nodes where node_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait NodeCCGen {
  val companion = Node
}
// GENERATED object end

