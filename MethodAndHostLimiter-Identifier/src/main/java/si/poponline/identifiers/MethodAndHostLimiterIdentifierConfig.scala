package si.poponline.identifiers

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.{Dtab, Path}
import io.buoyant.linkerd.IdentifierInitializer
import io.buoyant.linkerd.protocol.HttpIdentifierConfig

class MethodAndHostLimiterIdentifierInitializer extends IdentifierInitializer {
  val configClass = classOf[MethodAndHostLimiterIdentifierConfig]
  override val configId = MethodAndHostLimiterIdentifierConfig.kind
}

object MethodAndHostLimiterIdentifierInitializer extends MethodAndHostLimiterIdentifierInitializer

object MethodAndHostLimiterIdentifierConfig {
  val kind = "si.poponline.MethodAndHostLimiter"
}

class MethodAndHostLimiterIdentifierConfig extends HttpIdentifierConfig {
  var httpUriInDst: Option[Boolean] = None

  @JsonIgnore
  override def newIdentifier(
    prefix: Path,
    baseDtab: () => Dtab = () => Dtab.base
  ) = MethodAndHostLimiterIdentifier(prefix, httpUriInDst.getOrElse(false), baseDtab)
}
