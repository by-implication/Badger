import java.sql.{Connection, DriverManager, ResultSet}
import java.io.{File, FileWriter, FilenameFilter}
import scala.io.Source

object DatabaseSupport {
  Class.forName("org.postgresql.Driver")

  val dbname = "budget"
  val username = "postgres"
  val password = "postgres"

  var _conn: java.sql.Connection = null

  def conn = {
    if(_conn == null || _conn.isClosed()) {
      _conn = DriverManager.getConnection("jdbc:postgresql:"+dbname, username, password)
    }
    _conn
  }

  def recreate() = {
    val conn = DriverManager.getConnection("jdbc:postgresql", "postgres", "postgres")
    conn.createStatement().execute("DROP DATABASE "+dbname+"; CREATE DATABASE "+dbname)
    conn.close()
  }
}

object Evolutions {
  implicit val ord = new Ordering[File] {
    def compare(a: File, b: File): Int = {
      val aInt = a.getName.takeWhile(_.isDigit).toInt
      val bInt = b.getName.takeWhile(_.isDigit).toInt
      aInt compare bInt
    }
  }

  val sqlFilter = new FilenameFilter() {
    def accept(dir: File, name: String) = name.endsWith(".sql")
  }

  def load(evolutionsRoot: File) = {
    val sql = evolutionsRoot.listFiles(sqlFilter).sorted.map { f =>
      Source.fromFile(f).getLines
        .dropWhile(!_.contains("!Ups"))
        .drop(1).takeWhile(!_.contains("!Downs"))
        .reduceLeftOption(_+"\n"+_)
        .getOrElse("");
    }
    .reduceLeftOption(_+"\n"+_)
    .getOrElse("")
    Evolutions(sql, evolutionsRoot)
  }
}

case class Evolutions(sql: String, root: File) {
  val hashFile = new File(root, ".hash")

  def needsUpdate: Boolean = {
    if(hashFile.exists) {
      Source.fromFile(hashFile).getLines.toSeq.headOption.map { hash =>
        try {
          sql.hashCode != hash.toInt
        }
        catch {
          case e: Exception => true
        }
      }.getOrElse(true)
    }
    else {
      true
    }
  }

  def apply() = {
    import scala.collection.JavaConversions._

    println("[info] Reapplying evolutions to dummy database, PostgreSQL errors may follow")
    DatabaseSupport.recreate()
    var stmt = DatabaseSupport.conn.createStatement
    stmt.execute(sql.replaceAll(";;", ";"))
    for {
      initial <- Option(stmt.getWarnings)
      warning <- initial
    } println(warning)
    val fws = new FileWriter(hashFile)
    fws.write(sql.hashCode.toString)
    fws.close()
    DatabaseSupport.conn.close();
  }
}
