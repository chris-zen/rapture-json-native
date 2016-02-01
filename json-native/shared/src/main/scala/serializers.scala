package rapture.json.jsonBackends.native

import rapture.json._

private[native] trait Serializers {
  
  implicit val nativeJsonNodeSerializer: DirectJsonSerializer[Any] =
      DirectJsonSerializer(NativeAst)
}
