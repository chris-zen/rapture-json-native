/******************************************************************************************************************\
* Rapture JSON, version 2.0.0. Copyright 2010-2015 Jon Pretty, Propensive Ltd.                                     *
*                                                                                                                  *
* The primary distribution site is http://rapture.io/                                                              *
*                                                                                                                  *
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in complance    *
* with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.            *
*                                                                                                                  *
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed *
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License    *
* for the specific language governing permissions and limitations under the License.                               *
  * \******************************************************************************************************************/

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

