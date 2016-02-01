package rapture.json.jsonBackends.native

import rapture.data._
import rapture.json._
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

private[native] object NativeParser extends Parser[String, JsonAst] {
  
  val ast = NativeAst
  
  override def toString = "<NativeParser>"
  
  private val mapper = new ObjectMapper()
  
  def parse(s: String): Option[Any] = {
    val node = mapper.readTree(s)
    val typeRef = node match {
      case n: JsonNode if n.isObject =>
        new TypeReference[java.util.HashMap[String, Object]]() {}
      case n: JsonNode if n.isArray =>
        new TypeReference[java.util.ArrayList[Object]]() {}
      case _ =>
        new TypeReference[java.lang.Object]() {}
    }
    val parser = mapper.treeAsTokens(node)
    Option(parser.readValueAs[Any](typeRef))
  }
}

