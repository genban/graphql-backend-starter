package very.util.db.postgre
import com.github.tminglei.slickpg._
import org.json4s.native.Document
import org.json4s.{JValue, JsonMethods}
import slick.jdbc.PostgresProfile

trait PostgresProfileWithJson4S extends PostgresProfile
with PgJson4sSupport
with PgDate2Support
with PgArraySupport
with array.PgArrayJdbcTypes {

  def pgjson = "jsonb"

  type DOCType = org.json4s.native.Document

  override val jsonMethods: JsonMethods[Document] = org.json4s.native.JsonMethods.asInstanceOf[JsonMethods[DOCType]]


  override val api = MyAPI

  object MyAPI extends super.API
    with ArrayImplicits
    with Json4sJsonImplicits
    with DateTimeImplicits {
    implicit val strSetTypeMapper =
      new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val json4sJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JValue](
        pgjson,
        (s) =>
          utils.SimpleArrayUtils
            .fromString[JValue](jsonMethods.parse(_))(s)
            .orNull,
        (v) =>
          utils.SimpleArrayUtils
            .mkString[JValue](j => jsonMethods.compact(jsonMethods.render(j)))(v)
      ).to(_.toList)
  }
}

object PostgresProfileWithJson4S extends PostgresProfileWithJson4S
