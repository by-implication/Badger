package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._
import scala.util.Random

object Leaf extends LeafGen {

  def yearsJson = DB.withConnection { implicit c =>
    Json.toJson(SQL("SELECT DISTINCT leaf_year FROM leafs ORDER BY leaf_year").list(scalar[Int]))
  }

  def exploreQuery(dptDscs: Seq[String], areaDscs: Seq[String], year: Option[Int], offset: Int, sort: String, order: String): (Seq[Leaf], Long) = DB.withConnection { implicit c =>

    val constraints = Seq(dptDscs, areaDscs).filter(!_.isEmpty)

    val sortField = sort match {
      case "Year" => "leaf_year"
      case "Rating" => "leaf_rating"
      case "Latest Activity" => "leaf_last_activity"
      case _ => "leaf_total" // Amount
    }

    val sortOrder = order match {
      case "Descending" => "DESC"
      case _ => "ASC"
    }

    val conds = {"""
      FROM leafs leaf
      WHERE (
        leaf_ps IS NOT NULL
        OR leaf_mooe IS NOT NULL
        OR leaf_co IS NOT NULL
        OR leaf_net IS NOT NULL
      )
      """ +
      (if(!dptDscs.isEmpty){ "AND leaf_dpt_dsc = ANY({dptDscs}) " } else {""}) +
      (if(!areaDscs.isEmpty){ "AND leaf_area_dsc = ANY({areaDscs}) " } else {""}) +
      year.map(y => "AND leaf_year = {year}").getOrElse("")
    }

    val list = SQL("""SELECT *,
      COALESCE(leaf_ps, 0) + COALESCE(leaf_mooe, 0) + COALESCE(leaf_co, 0) + COALESCE(leaf_net, 0) AS leaf_total,
      CASE WHEN leaf_ratings != 0 THEN leaf_stars / leaf_ratings ELSE 0 END AS leaf_rating
    """ + conds +
      "ORDER BY " + sortField + " " + sortOrder +
      " LIMIT 30 OFFSET {offset}"
    ).on(
      'dptDscs -> PGStringList(dptDscs),
      'areaDscs -> PGStringList(areaDscs),
      'year -> year,
      'offset -> offset
    ).list(Leaf.simple)

    val count = SQL("SELECT COUNT(*)" + conds).on(
      'dptDscs -> PGStringList(dptDscs),
      'areaDscs -> PGStringList(areaDscs),
      'year -> year,
      'offset -> offset
    ).as(scalar[Long].single)

    (list, count)

  }

  def query(kind: String, year: Int, dpt_cd: String, owner_cd: String, fpap_cd: String)(implicit user: User): Option[JsObject] = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM leafs WHERE leaf_kind = {kind}
      AND leaf_year = {year}
      AND leaf_dpt_cd = {dpt_cd}
      AND leaf_owner_cd = {owner_cd}
      AND leaf_fpap_cd = {fpap_cd}
    """).on(
      'kind -> kind,
      'year -> year,
      'dpt_cd -> dpt_cd,
      'owner_cd -> owner_cd,
      'fpap_cd -> PGLTree(fpap_cd.split("\\."))
    ).singleOpt(Leaf.simple).map(_.toJson(user))
  }

}

// GENERATED case class start
case class Leaf(
  dptCd: Option[String] = None,
  dptDsc: Option[String] = None,
  agyType: Option[String] = None,
  ownerCd: Option[String] = None,
  ownerDsc: Option[String] = None,
  fpapCd: Option[PGLTree] = None,
  fpapDsc: Option[String] = None,
  areaCd: Option[String] = None,
  areaDsc: Option[String] = None,
  ps: Option[Int] = None,
  mooe: Option[Int] = None,
  co: Option[Int] = None,
  net: Option[Int] = None,
  year: Option[Int] = None,
  kind: Option[String] = None,
  id: Pk[Int] = NA,
  stars: Int = 0,
  ratings: Int = 0,
  lastActivity: Option[Timestamp] = None
) extends LeafCCGen with Entity[Leaf]
// GENERATED case class end
{

  def updateLastActivity() = copy(lastActivity = Some(Time.now)).save()

  def clicks: Long = DB.withConnection { implicit c =>
    SQL("SELECT COUNT(*) FROM clicks WHERE leaf_id = {id}")
    .on('id -> id).as(scalar[Long].single)
  }

  def breadcrumbs = DB.withConnection { implicit c =>
    SQL("""
      SELECT * FROM leafs
      WHERE leaf_id < {id}
      AND leaf_fpap_cd @> {fpapCd}
      AND leaf_dpt_dsc = {dptDsc}
      AND leaf_year = {year}
      AND leaf_kind = {kind}
      ORDER BY leaf_fpap_cd
    """).on(
      'id -> id,
      'fpapCd -> fpapCd,
      'dptDsc -> dptDsc,
      'year -> year,
      'kind -> kind
    ).list(Leaf.simple)
  }

  def changeRating(oldStars: Int, newStars: Int) = copy(stars = stars - oldStars + newStars).save()

  def addRating(newStars: Int) = copy(stars = stars + newStars, ratings = ratings + 1).save()

  def toJson(user: User): JsObject = Json.obj(
    // "dptCd" -> dptCd,
    "dptDsc" -> dptDsc,
    // "agyType" -> agyType,
    // "ownerCd" -> ownerCd,
    "ownerDsc" -> ownerDsc,
    // "fpapCd" -> fpapCd,
    "fpapDsc" -> fpapDsc,
    // "areaCd" -> areaCd,
    "areaDsc" -> areaDsc,
    "ps" -> ps,
    "mooe" -> mooe,
    "co" -> co,
    // "net" -> net,
    "year" -> year,
    "xkind" -> kind,
    "id" -> id.get,
    "kind" -> "leaf",
    "id" -> id.get,
    "stars" -> stars,
    "ratings" -> ratings,
    "userRating" -> user.ratingFor(this),
    "userClick" -> user.clickFor(this),
    "clicks" -> clicks
    // "breadcrumbs" -> breadcrumbs.map(c => Json.obj(
    //   "id" -> c.id.get,
    //   "name" -> c.fpapDsc
    // ))
  )
}

// GENERATED object start
trait LeafGen extends EntityCompanion[Leaf] {
  val simple = {
    get[Option[String]]("leaf_dpt_cd") ~
    get[Option[String]]("leaf_dpt_dsc") ~
    get[Option[String]]("leaf_agy_type") ~
    get[Option[String]]("leaf_owner_cd") ~
    get[Option[String]]("leaf_owner_dsc") ~
    get[Option[PGLTree]]("leaf_fpap_cd") ~
    get[Option[String]]("leaf_fpap_dsc") ~
    get[Option[String]]("leaf_area_cd") ~
    get[Option[String]]("leaf_area_dsc") ~
    get[Option[Int]]("leaf_ps") ~
    get[Option[Int]]("leaf_mooe") ~
    get[Option[Int]]("leaf_co") ~
    get[Option[Int]]("leaf_net") ~
    get[Option[Int]]("leaf_year") ~
    get[Option[String]]("leaf_kind") ~
    get[Pk[Int]]("leaf_id") ~
    get[Int]("leaf_stars") ~
    get[Int]("leaf_ratings") ~
    get[Option[Timestamp]]("leaf_last_activity") map {
      case dptCd~dptDsc~agyType~ownerCd~ownerDsc~fpapCd~fpapDsc~areaCd~areaDsc~ps~mooe~co~net~year~kind~id~stars~ratings~lastActivity =>
        Leaf(dptCd, dptDsc, agyType, ownerCd, ownerDsc, fpapCd, fpapDsc, areaCd, areaDsc, ps, mooe, co, net, year, kind, id, stars, ratings, lastActivity)
    }
  }

  def lazyFind(column: String, value: Any) = SQL("select * from leafs where "+column+" = {value}").on('value -> value)

  def findOne(column: String, value: Any): Option[Leaf] = DB.withConnection { implicit c =>
    lazyFind(column, value).singleOpt(simple)
  }

  def findAll(column: String, value: Any): Seq[Leaf] = DB.withConnection { implicit c =>
    lazyFind(column, value).list(simple)
  }

  def findById(id: Int): Option[Leaf] = findOne("leaf_id", id)

  def list(count: Int = 10, offset: Int = 0): Seq[Leaf] = DB.withConnection { implicit c =>
    SQL("select * from leafs limit {count} offset {offset}").on('count -> count, 'offset -> offset).list(simple)
  }

  def insert(o: Leaf): Option[Leaf] = DB.withConnection { implicit c =>
    o.id match {
      case NotAssigned => {
        val id = SQL("""
          insert into leafs (
            leaf_dpt_cd,
            leaf_dpt_dsc,
            leaf_agy_type,
            leaf_owner_cd,
            leaf_owner_dsc,
            leaf_fpap_cd,
            leaf_fpap_dsc,
            leaf_area_cd,
            leaf_area_dsc,
            leaf_ps,
            leaf_mooe,
            leaf_co,
            leaf_net,
            leaf_year,
            leaf_kind,
            leaf_id,
            leaf_stars,
            leaf_ratings,
            leaf_last_activity
          ) VALUES (
            {dptCd},
            {dptDsc},
            {agyType},
            {ownerCd},
            {ownerDsc},
            {fpapCd},
            {fpapDsc},
            {areaCd},
            {areaDsc},
            {ps},
            {mooe},
            {co},
            {net},
            {year},
            {kind},
            DEFAULT,
            {stars},
            {ratings},
            {lastActivity}
          )
        """).on(
          'dptCd -> o.dptCd,
          'dptDsc -> o.dptDsc,
          'agyType -> o.agyType,
          'ownerCd -> o.ownerCd,
          'ownerDsc -> o.ownerDsc,
          'fpapCd -> o.fpapCd,
          'fpapDsc -> o.fpapDsc,
          'areaCd -> o.areaCd,
          'areaDsc -> o.areaDsc,
          'ps -> o.ps,
          'mooe -> o.mooe,
          'co -> o.co,
          'net -> o.net,
          'year -> o.year,
          'kind -> o.kind,
          'id -> o.id,
          'stars -> o.stars,
          'ratings -> o.ratings,
          'lastActivity -> o.lastActivity
        ).executeInsert()
        id.map(i => o.copy(id=Id(i.toInt)))
      }
      case Id(n) => {
        SQL("""
          insert into leafs (
            leaf_dpt_cd,
            leaf_dpt_dsc,
            leaf_agy_type,
            leaf_owner_cd,
            leaf_owner_dsc,
            leaf_fpap_cd,
            leaf_fpap_dsc,
            leaf_area_cd,
            leaf_area_dsc,
            leaf_ps,
            leaf_mooe,
            leaf_co,
            leaf_net,
            leaf_year,
            leaf_kind,
            leaf_id,
            leaf_stars,
            leaf_ratings,
            leaf_last_activity
          ) VALUES (
            {dptCd},
            {dptDsc},
            {agyType},
            {ownerCd},
            {ownerDsc},
            {fpapCd},
            {fpapDsc},
            {areaCd},
            {areaDsc},
            {ps},
            {mooe},
            {co},
            {net},
            {year},
            {kind},
            {id},
            {stars},
            {ratings},
            {lastActivity}
          )
        """).on(
          'dptCd -> o.dptCd,
          'dptDsc -> o.dptDsc,
          'agyType -> o.agyType,
          'ownerCd -> o.ownerCd,
          'ownerDsc -> o.ownerDsc,
          'fpapCd -> o.fpapCd,
          'fpapDsc -> o.fpapDsc,
          'areaCd -> o.areaCd,
          'areaDsc -> o.areaDsc,
          'ps -> o.ps,
          'mooe -> o.mooe,
          'co -> o.co,
          'net -> o.net,
          'year -> o.year,
          'kind -> o.kind,
          'id -> o.id,
          'stars -> o.stars,
          'ratings -> o.ratings,
          'lastActivity -> o.lastActivity
        ).executeInsert().flatMap(x => Some(o))
      }
    }
  }

  def update(o: Leaf): Boolean = DB.withConnection { implicit c =>
    SQL("""
      update leafs set
        leaf_dpt_dsc={dptDsc},
        leaf_agy_type={agyType},
        leaf_owner_cd={ownerCd},
        leaf_owner_dsc={ownerDsc},
        leaf_fpap_cd={fpapCd},
        leaf_fpap_dsc={fpapDsc},
        leaf_area_cd={areaCd},
        leaf_area_dsc={areaDsc},
        leaf_ps={ps},
        leaf_mooe={mooe},
        leaf_co={co},
        leaf_net={net},
        leaf_year={year},
        leaf_kind={kind},
        leaf_id={id},
        leaf_stars={stars},
        leaf_ratings={ratings},
        leaf_last_activity={lastActivity}
      where leaf_id={id}
    """).on(
      'dptCd -> o.dptCd,
      'dptDsc -> o.dptDsc,
      'agyType -> o.agyType,
      'ownerCd -> o.ownerCd,
      'ownerDsc -> o.ownerDsc,
      'fpapCd -> o.fpapCd,
      'fpapDsc -> o.fpapDsc,
      'areaCd -> o.areaCd,
      'areaDsc -> o.areaDsc,
      'ps -> o.ps,
      'mooe -> o.mooe,
      'co -> o.co,
      'net -> o.net,
      'year -> o.year,
      'kind -> o.kind,
      'id -> o.id,
      'stars -> o.stars,
      'ratings -> o.ratings,
      'lastActivity -> o.lastActivity
    ).executeUpdate() > 0
  }

  def delete(id: Int): Boolean = DB.withConnection { implicit c =>
    SQL("delete from leafs where leaf_id={id}").on('id -> id).executeUpdate() > 0
  }
}

trait LeafCCGen {
  val companion = Leaf
}
// GENERATED object end

