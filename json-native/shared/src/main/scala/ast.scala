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
\******************************************************************************************************************/
package rapture.json.jsonBackends.native

import java.util

import rapture.data.DataTypes
import rapture.json._

import scala.collection.JavaConverters._

/** A type class for Native parsing */
private[native] object NativeAst extends JsonAst {

  override def toString = "<NativeAst>"

  def getArray(array: Any): List[Any] = array match {
    case list: java.util.ArrayList[Any @unchecked] => list.asScala.toList
    case list: List[Any] => list
    case _ => throw TypeMismatchException(getType(array), DataTypes.Array)
  }

  def getBoolean(boolean: Any): Boolean = boolean match {
    case boolean: Boolean => boolean
    case _ => throw TypeMismatchException(getType(boolean), DataTypes.Boolean)
  }
  
  def getDouble(number: Any): Double = number match {
    case number: Int => number.doubleValue
    case number: Long => number.doubleValue
    case number: Double => number
    case number: BigDecimal => number.doubleValue
    case number: java.math.BigDecimal => number.doubleValue
    case number: java.math.BigInteger => number.doubleValue
    case _ => throw TypeMismatchException(getType(number), DataTypes.Number)
  }
  
  def getBigDecimal(number: Any): BigDecimal = number match {
    case number: Int => new java.math.BigDecimal(number)
    case number: Long => new java.math.BigDecimal(number)
    case number: Double => BigDecimal(number)
    case number: BigDecimal => number
    case number: java.math.BigDecimal => number
    case number: java.math.BigInteger => new java.math.BigDecimal(number)
    case _ => throw TypeMismatchException(getType(number), DataTypes.Number)
  }
  
  def getString(string: Any): String = string match {
    case string: java.lang.String => string
    case char: java.lang.Character => char.toString
      // TODO keyword, symbol, ...?
    case _ => throw TypeMismatchException(getType(string), DataTypes.String)
  }
  
  def getObject(obj: Any): Map[String, Any] = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => obj.asScala.toMap
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def getKeys(obj: Any): Iterator[String] = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => obj.keySet().iterator().asScala
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def dereferenceObject(obj: Any, element: String): Any = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => Option(obj.get(element)).get
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def dereferenceArray(array: Any, index: Int): Any = array match {
    case array: java.util.ArrayList[Any @unchecked] => array.get(index)
    case array: List[Any] => array(index)
    case _ => throw TypeMismatchException(getType(array), DataTypes.Array)
  }

  def setObjectValue(obj: Any, name: String, value: Any): Unit = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => obj.put(name, value)
  }
  
  def removeObjectValue(obj: Any, name: String): Unit = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => obj.remove(name)
  }
  
  def addArrayValue(array: Any, value: Any): Unit = array match {
    case array: java.util.ArrayList[Any @unchecked] => array.add(value)
  }
  
  def setArrayValue(array: Any, index: Int, value: Any): Unit = array match {
    case array: java.util.ArrayList[Any @unchecked] => array.set(index, value)
  }

  def nullValue = null

  def fromArray(array: Seq[Any]): Any = {
    val newArray = new java.util.ArrayList[Any @unchecked](array.size)
    for(v <- array) {
      newArray.add(v)
    }
    newArray
  }

  def fromBoolean(boolean: Boolean): Any = boolean

  def fromDouble(number: Double): Any = number

  def fromBigDecimal(number: BigDecimal): Any = number
  
  def fromObject(obj: Map[String, Any]): Any = {
    val newMap = new util.HashMap[String @unchecked, Any @unchecked]()
    for ((k, v) <- obj) {
      newMap.put(k, v)
    }
    newMap
  }

  def fromString(string: String): Any = string

  def isBoolean(any: Any): Boolean = any match {
    case x: Boolean => true
    case _ => false
  }
  
  def isString(any: Any): Boolean = any match {
    case x: String => true
      // TODO keyword, symbol, ...
    case _ => false
  }

  def isNumber(any: Any): Boolean = any match {
    case x: Int => true
    case x: Long => true
    case x: java.math.BigDecimal => true
    case x: java.math.BigInteger => true
    case x: Double => true
    case x: Float => true
    case _ => false
  }
  
  def isObject(any: Any): Boolean = any match {
    case x: java.util.HashMap[String @unchecked, Any @unchecked] => true
    case _ => false
  }
  
  def isArray(any: Any): Boolean = any match {
    case x: java.util.ArrayList[Any @unchecked] => true
    case x: List[Any @unchecked] => true
    case _ => false
  }
  
  def isNull(any: Any): Boolean = any == null
}
