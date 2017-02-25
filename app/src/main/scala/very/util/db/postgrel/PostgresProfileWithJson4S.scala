package very.util.db.postgrel

import com.github.tminglei.slickpg._
import org.json4s.{JValue, JsonMethods}

import slick.jdbc.PostgresProfile

trait PostgresProfileWithJson4S extends PostgresProfile
  with PgJson4sSupport
  with array.PgArrayJdbcTypes
{

  def pgjson = "jsonb"

  type DOCType = org.json4s.native.Document

  override val jsonMethods = org.json4s.native.JsonMethods.asInstanceOf[JsonMethods[DOCType]]

//  override protected def computeCapabilities:Set[Capability] =
//    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  val plainAPI = new API with Json4sJsonPlainImplicits

  object MyAPI extends super.API with JsonImplicits {

    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val json4sJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JValue](jsonMethods.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JValue](j=>jsonMethods.compact(jsonMethods.render(j)))(v)
      ).to(_.toList)

  }

}


object PostgresProfileWithJson4S extends PostgresProfileWithJson4S