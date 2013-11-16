package budget.models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db._
import play.api.libs.json._
import play.api.Play.current
import budget.support._

object Leaf extends LeafGen {

  def query(id: Int): Option[JsObject] = findById(id).map(_.toJson)

}

// GENERATED case class start
case class Leaf(
  dptCd: Option[String] = None,
  dptDsc: Option[String] = None,
  agyType: Option[String] = None,
  ownerCd: Option[String] = None,
  ownerDsc: Option[String] = None,
  fpapCd: Option[String] = None,
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
  ratings: Int = 0
) extends LeafCCGen with Entity[Leaf]
// GENERATED case class end
{

  lazy val parent: JsObject = {
    Location.findOne("location_name", areaDsc).map(l => Json.obj(
      "id" -> l.id.get,
      "name" -> l.name
    )).get
  }

  lazy val toJson: JsObject = Json.obj(
    "dptCd" -> dptCd,
    "dptDsc" -> dptDsc,
    "agyType" -> agyType,
    "ownerCd" -> ownerCd,
    "ownerDsc" -> ownerDsc,
    "fpapCd" -> fpapCd,
    "fpapDsc" -> fpapDsc,
    "areaCd" -> areaCd,
    "areaDsc" -> areaDsc,
    "ps" -> ps,
    "mooe" -> mooe,
    "co" -> co,
    "net" -> net,
    "year" -> year,
    "xkind" -> kind,
    "id" -> id.get,
    "kind" -> "leaf",
    "parent" -> parent
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
    get[Option[String]]("leaf_fpap_cd") ~
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
    get[Int]("leaf_ratings") map {
      case dptCd~dptDsc~agyType~ownerCd~ownerDsc~fpapCd~fpapDsc~areaCd~areaDsc~ps~mooe~co~net~year~kind~id~stars~ratings =>
        Leaf(dptCd, dptDsc, agyType, ownerCd, ownerDsc, fpapCd, fpapDsc, areaCd, areaDsc, ps, mooe, co, net, year, kind, id, stars, ratings)
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
            leaf_ratings
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
            {ratings}
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
          'ratings -> o.ratings
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
            leaf_ratings
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
            {ratings}
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
          'ratings -> o.ratings
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
        leaf_ratings={ratings}
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
      'ratings -> o.ratings
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

