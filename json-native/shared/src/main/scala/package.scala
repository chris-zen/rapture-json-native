package rapture.json.jsonBackends.native

import rapture.json._
import rapture.data._

object `package` extends Extractors with Serializers {
  implicit val implicitJsonAst: JsonAst = NativeAst
  implicit val implicitJsonStringParser: Parser[String, JsonAst] = NativeParser
}
