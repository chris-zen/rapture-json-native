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
    case list: java.util.List[Any @unchecked] => list.asScala.toList
    case list: List[Any] => list
    case list: collection.immutable.Seq[Any] => list.toList
    case list: collection.mutable.Seq[Any @unchecked] => list.toList
    case list: collection.immutable.Set[Any @unchecked] => list.toList
    case list: collection.mutable.Set[Any @unchecked] => list.toList
    case _ => throw TypeMismatchException(getType(array), DataTypes.Array)
  }

  def getBoolean(boolean: Any): Boolean = boolean match {
    case boolean: Boolean => boolean
    case _ => throw TypeMismatchException(getType(boolean), DataTypes.Boolean)
  }
  
  def getDouble(number: Any): Double = number match {
    case number: Double => number
    case number: java.math.BigDecimal => number.doubleValue
    case number: BigDecimal => number.toDouble
    case number: java.math.BigInteger => number.doubleValue
    case number: BigInt => number.toDouble
    case number: Long => number.toDouble
    case number: Int => number.toDouble
    case number: Short => number.toDouble
    case number: Byte => number.toDouble
    case _ => throw TypeMismatchException(getType(number), DataTypes.Number)
  }
  
  def getBigDecimal(number: Any): BigDecimal = number match {
    case number: java.math.BigDecimal => BigDecimal(number)
    case number: BigDecimal => number
    case number: Double => BigDecimal(number)
    case number: java.math.BigInteger => BigDecimal(number)
    case number: BigInt => BigDecimal(number)
    case number: Long => BigDecimal(number)
    case number: Int => BigDecimal(number)
    case number: Short => BigDecimal(number)
    case number: Byte => BigDecimal(number)
    case _ => throw TypeMismatchException(getType(number), DataTypes.Number)
  }
  
  def getString(string: Any): String = string match {
    case string: String => string
    case char: Character => char.toString
      // TODO keyword, symbol, ...?
    case _ => throw TypeMismatchException(getType(string), DataTypes.String)
  }
  
  def getObject(obj: Any): Map[String, Any] = obj match {
    case obj: java.util.HashMap[String @unchecked, Any @unchecked] => obj.asScala.toMap
    case obj: java.util.Map[String @unchecked, Any @unchecked] => obj.asScala.toMap
    case obj: collection.immutable.Map[String @unchecked, Any @unchecked] => obj
    case obj: collection.mutable.Map[String @unchecked, Any @unchecked] => obj.toMap
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def getKeys(obj: Any): Iterator[String] = obj match {
    case obj: java.util.Map[String @unchecked, Any @unchecked] => obj.keySet().iterator().asScala
    case obj: collection.immutable.Map[String @unchecked, Any @unchecked] => obj.keysIterator
    case obj: collection.mutable.Map[String @unchecked, Any @unchecked] => obj.keysIterator
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def dereferenceObject(obj: Any, element: String): Any = obj match {
    case obj: java.util.Map[String @unchecked, Any @unchecked] => Option(obj.get(element)).get
    case obj: collection.immutable.Map[String @unchecked, Any @unchecked] => obj(element)
    case obj: collection.mutable.Map[String @unchecked, Any @unchecked] => obj(element)
    case _ => throw TypeMismatchException(getType(obj), DataTypes.Object)
  }

  override def dereferenceArray(array: Any, index: Int): Any = array match {
    case array: java.util.List[Any @unchecked] => array.get(index)
    case array: List[Any] => array(index)
    case _ => throw TypeMismatchException(getType(array), DataTypes.Array)
  }

  def setObjectValue(obj: Any, name: String, value: Any): Unit = obj match {
    case obj: java.util.Map[String @unchecked, Any @unchecked] => obj.put(name, value)
    case obj: collection.mutable.Map[String @unchecked, Any @unchecked] => obj.put(name, value)
  }
  
  def removeObjectValue(obj: Any, name: String): Unit = obj match {
    case obj: java.util.Map[String @unchecked, Any @unchecked] => obj.remove(name)
    case obj: collection.mutable.Map[String @unchecked, Any @unchecked] => obj.remove(name)
  }
  
  def addArrayValue(array: Any, value: Any): Unit = array match {
    case array: java.util.List[Any @unchecked] => array.add(value)
    case array: collection.mutable.Buffer[Any @unchecked] => array += value
  }
  
  def setArrayValue(array: Any, index: Int, value: Any): Unit = array match {
    case array: java.util.List[Any @unchecked] => array.set(index, value)
    case array: collection.mutable.Seq[Any @unchecked] => array(index) = value
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
    case x: java.util.Map[String @unchecked, Any @unchecked] => true
    case x: collection.Map[String @unchecked, Any @unchecked] => true
    case _ => false
  }
  
  def isArray(any: Any): Boolean = any match {
    case x: java.util.ArrayList[Any @unchecked] => true
    case x: collection.Seq[Any @unchecked] => true
    case _ => false
  }
  
  def isNull(any: Any): Boolean = any == null
}
