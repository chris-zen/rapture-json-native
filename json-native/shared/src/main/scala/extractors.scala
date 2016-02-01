package rapture.json.jsonBackends.native

import rapture.json._
import rapture.data._

private[native] trait Extractors {
  implicit val nativeJsonNodeExtractor: JsonCastExtractor[Any] =
    JsonCastExtractor(NativeAst, DataTypes.Any)
}
