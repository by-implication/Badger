package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._

object Location extends LocationGen {

  def query(id: Int)(implicit user: User): Option[JsObject] = DB.withConnection { implicit c =>
    Location.findById(id).map(_.toJson(expand = true))
  }

}

// GENERATED case class start
case class Location(
  id: Pk[Int] = NA,
  name: String = "",
  locationParentId: Option[Int] = None,
  lat: BigDecimal = 0,
  lng: BigDecimal = 0,
  ps: Int = 0,
  mooe: Int = 0,
  co: Int = 0,
  stars: Int = 0,
  ratings: Int = 0
) extends LocationCCGen with Entity[Location]
// GENERATED case class end
{

  lazy val parent: Option[Location] = locationParentId.map(Location.findById(_).get)

  def cascadeChangeRating(oldStars: Int, newStars: Int): Boolean = {
    copy(stars = stars - oldStars + newStars).save()
    parent.map(_.cascadeChangeRating(oldStars, newStars)).isDefined
  }

  def cascadeAddRating(newStars: Int): Boolean = {
    copy(stars = stars + newStars, ratings = ratings + 1).save()
    parent.map(_.cascadeAddRating(newStars)).isDefined
  }

  lazy val children: (Seq[Location], Seq[Leaf]) = DB.withConnection { implicit c =>

    val locs = SQL("""
      SELECT * from locations
      WHERE location_parent_id = {id}
    """).on('id -> id).list(Location.simple)

    val leaves = SQL("""
      SELECT * from leafs
      WHERE leaf_area_dsc = {areaDsc}
      AND (
        leaf_ps > 0
        OR leaf_mooe > 0
        OR leaf_co > 0
      )
    """).on('areaDsc -> name).list(Leaf.simple)

    (locs, leaves)

    

  }

  def toJson(expand: Boolean = false)(implicit user: User): JsObject = {
    var r = Json.obj(
      "id" -> id.get,
      "kind" -> "loc",
      "name" -> name,
      "ps" -> ps,
      "mooe" -> mooe,
      "co" -> co,
      "lat" -> lat,
      "lng" -> lng,
      "parent" -> parent.map(p => Json.obj(
        "id" -> p.id.get,
        "name" -> p.name
      ))
    )
    if(expand){
      val (locs, leaves) = children
      r ++= Json.obj("children" -> Map(
        "locs" -> locs.map(_.toJson()),
        "leaves" -> leaves.map(_.toJson(user))
      ))
    }
    r
  }
}

// GENERATED object start
trait LocationGen extends EntityCompanion[Location] {
  val simple = {
    get[Pk[Int]]("location_id") ~
    get[String]("location_name") ~
    get[Option[Int]]("location_parent_id") ~
    get[java.math.BigDecimal]("location_lat") ~
    get[java.math.BigDecimal]("location_lng") ~
    get[Int]("location_ps") ~
    get[Int]("location_mooe") ~
    get[Int]("location_co") ~
    get[Int]("location_stars") ~
    get[Int]("location_ratings") map {
      case id~name~locationParentId~lat~lng~ps~mooe~co~stars~ratings =>
        Location(id, name, locationParentId, lat, lng, ps, mooe, co, stars, ratings)
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
            location_parent_id,
            location_lat,
            location_lng,
            location_ps,
            location_mooe,
            location_co,
            location_stars,
            location_ratings
          ) VALUES (
            DEFAULT,
            {name},
            {locationParentId},
            {lat},
            {lng},
            {ps},
            {mooe},
            {co},
            {stars},
            {ratings}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'locationParentId -> o.locationParentId,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal,
          'ps -> o.ps,
          'mooe -> o.mooe,
          'co -> o.co,
          'stars -> o.stars,
          'ratings -> o.ratings
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into locations (
            location_id,
            location_name,
            location_parent_id,
            location_lat,
            location_lng,
            location_ps,
            location_mooe,
            location_co,
            location_stars,
            location_ratings
          ) VALUES (
            {id},
            {name},
            {locationParentId},
            {lat},
            {lng},
            {ps},
            {mooe},
            {co},
            {stars},
            {ratings}
          )
        """).on(
          'id -> o.id,
          'name -> o.name,
          'locationParentId -> o.locationParentId,
          'lat -> o.lat.bigDecimal,
          'lng -> o.lng.bigDecimal,
          'ps -> o.ps,
          'mooe -> o.mooe,
          'co -> o.co,
          'stars -> o.stars,
          'ratings -> o.ratings
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Location): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update locations set
        location_name={name},
        location_parent_id={locationParentId},
        location_lat={lat},
        location_lng={lng},
        location_ps={ps},
        location_mooe={mooe},
        location_co={co},
        location_stars={stars},
        location_ratings={ratings}
      where location_id={id}
    """).on(
      'id -> o.id,
      'name -> o.name,
      'locationParentId -> o.locationParentId,
      'lat -> o.lat.bigDecimal,
      'lng -> o.lng.bigDecimal,
      'ps -> o.ps,
      'mooe -> o.mooe,
      'co -> o.co,
      'stars -> o.stars,
      'ratings -> o.ratings
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

