package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._

object Location extends LocationGen {

  def query(id: Int): Option[JsObject] = DB.withConnection { implicit c =>
    Location.findById(id).map(_.toJson(expand = true))
  }

}

// GENERATED case class start
case class Location(
  id: Pk[Int] = NA,
  name: Option[String] = None,
  parent: Option[Int] = None,
  lat: BigDecimal = 0,
  lng: BigDecimal = 0
) extends LocationCCGen with Entity[Location]
// GENERATED case class end
{

  lazy val children: (Seq[Location], Seq[Leaf]) = DB.withConnection { implicit c =>

    val locs = SQL("""
      SELECT * from locations
      WHERE location_parent = {id}
    """).on('id -> id).list(Location.simple)

    val leaves = SQL("""
      SELECT * from leafs
      WHERE leaf_area_dsc = {areaDsc}
      AND leaf_ps > 0
      AND leaf_mooe > 0
      AND leaf_co > 0
    """).on('areaDsc -> name).list(Leaf.simple)

    (locs, leaves)

  }

  def toJson(expand: Boolean = false): JsObject = {
    var r = Json.obj(
      "id" -> id.get,
      "kind" -> "loc",
      "name" -> name,
      "parent" -> parent.map(p => Json.obj(
        "id" -> p,
        "name" -> Location.findById(p).get.name
      ))
    )
    if(expand){
      val (locs, leaves) = children
      r ++= Json.obj("children" -> Map(
        "locs" -> locs.map(_.toJson()),
        "leaves" -> leaves.map(_.toJson)
      ))
    }
    r
  }
}

// GENERATED object start
trait LocationGen extends EntityCompanion[Location] {
  val simple = {
    get[Pk[Int]]("location_id") ~
    get[Option[String]]("location_name") ~
    get[Option[Int]]("location_parent") ~
    get[java.math.BigDecimal]("location_lat") ~
    get[java.math.BigDecimal]("location_lng") map {
      case id~name~parent~lat~lng =>
        Location(id, name, parent, lat, lng)
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
            location_parent,
            location_lat,
            location_lng
          ) VALUES (
            DEFAULT,
            {name},
            {parent},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'parent -> o.parent,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into locations (
            location_id,
            location_name,
            location_parent,
            location_lat,
            location_lng
          ) VALUES (
            {id},
            {name},
            {parent},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'parent -> o.parent,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Location): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update locations set
        location_name={name},
        location_parent={parent},
        location_lat={lat},
        location_lng={lng}
      where location_id={id}
    """).on(
      'id -> o.id,
      'name -> o.name,
      'parent -> o.parent,
      'lat -> o.lat.bigDecimal,
      'lng -> o.lng.bigDecimal
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

