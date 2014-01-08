package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._
import scala.util.Random

object Location extends LocationGen {

  def json = Json.toJson(list(999).map(_.toJson))

}

// GENERATED case class start
case class Location(
  id: Pk[Int] = NA,
  name: String = "",
  areas: PGStringList = Nil,
  lat: BigDecimal = 0,
  lng: BigDecimal = 0
) extends LocationCCGen with Entity[Location]
// GENERATED case class end
{

  def children(offset: Int = 0, limit: Int = 30): (Seq[Location], Seq[Leaf]) = DB.withConnection { implicit c =>

    val locs = SQL("""
      SELECT * from locations
      WHERE location_parent_id = {id}
      ORDER BY location_lat DESC
    """).on('id -> id).list(Location.simple)

    val leaves = SQL("""
      SELECT * from leafs
      WHERE leaf_area_dsc = {areaDsc}
      AND (leaf_ps IS NOT NULL
        OR leaf_mooe IS NOT NULL
        OR leaf_co IS NOT NULL
        OR leaf_net IS NOT NULL
      )
      LIMIT {limit}
      OFFSET {offset}
    """).on(
      'areaDsc -> name,
      'limit -> limit,
      'offset -> offset
    ).list(Leaf.simple)

    (locs, leaves)

  }

  def toJson: JsObject = Json.obj(
    "id" -> id.get,
    "name" -> name,
    "areas" -> areas,
    "lat" -> lat,
    "lng" -> lng
  )

}

// GENERATED object start
trait LocationGen extends EntityCompanion[Location] {
  val simple = {
    get[Pk[Int]]("location_id") ~
    get[String]("location_name") ~
    get[PGStringList]("location_areas") ~
    get[java.math.BigDecimal]("location_lat") ~
    get[java.math.BigDecimal]("location_lng") map {
      case id~name~areas~lat~lng =>
        Location(id, name, areas, lat, lng)
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
            location_areas,
            location_lat,
            location_lng
          ) VALUES (
            DEFAULT,
            {name},
            {areas},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'areas -> o.areas,
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
            location_areas,
            location_lat,
            location_lng
          ) VALUES (
            {id},
            {name},
            {areas},
            {lat},
            {lng}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'areas -> o.areas,
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
        location_areas={areas},
        location_lat={lat},
        location_lng={lng}
      where location_id={id}
    """).on(
      'id -> o.id,
      'name -> o.name,
      'areas -> o.areas,
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

