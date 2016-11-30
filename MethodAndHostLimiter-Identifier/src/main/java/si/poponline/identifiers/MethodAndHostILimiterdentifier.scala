package si.poponline.identifiers

import com.twitter.finagle.buoyant.Dst
import com.twitter.finagle.http.{Request, Version}
import com.twitter.finagle.{Dtab, Path}
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}
import io.buoyant.router.RoutingFactory
import io.buoyant.router.RoutingFactory.{IdentifiedRequest, RequestIdentification, UnidentifiedRequest}

object MethodAndHostLimiterIdentifier {
  def mk(
    prefix: Path,
    baseDtab: () => Dtab = () => Dtab.base
  ): RoutingFactory.Identifier[Request] = MethodAndHostLimiterIdentifier(prefix, false, baseDtab, banThreshold=20, banIntervalCleanUp=600, banWindowTime = 10)
}

object rateLimiter {
  def main(): Unit = {
    val rl = RateLimmiter.getInstance()
  }
}
case class MethodAndHostLimiterIdentifier(
  prefix: Path,
  uris: Boolean = false,
  baseDtab: () => Dtab = () => Dtab.base,
  banThreshold: Integer,
  banIntervalCleanUp: Integer,
  banWindowTime: Integer
) extends RoutingFactory.Identifier[Request] {

  private[this] def suffix(req: Request): Path =
    if (uris) Path.read(req.path) else Path.empty

  private[this] def mkPath(path: Path): Dst.Path =
    Dst.Path(prefix ++ path, baseDtab(), Dtab.local)

  def apply(req: Request): Future[RequestIdentification[Request]] = req.version match {

    case Version.Http10 =>
      val dst = mkPath(Path.Utf8("1.0", req.method.toString) ++ suffix(req))
      Future.value(new IdentifiedRequest(dst, req))

    case Version.Http11 =>
      req.host match {
        case Some(host) if host.nonEmpty =>
          val hm = req.headerMap

          var dst = mkPath(Path.Utf8("1.1", req.method.toString, host.toLowerCase) ++ suffix(req))
          if (hm.contains("Cres-Client-IP")) {
            val remote_addr = hm.get("Cres-Client-IP")
            //req.remoteAddress = remote_addr;
            RateLimmiter.getInstance().setLimits( banThreshold,  banIntervalCleanUp,  banWindowTime)
            if (RateLimmiter.getInstance().check_ip(remote_addr.get, host.toLowerCase)) {
              dst = mkPath(Path.Utf8("1.1", req.method.toString, host.toLowerCase) ++ suffix(req))
            } else {

              dst = mkPath(Path.Utf8("1.1", req.method.toString, "devnull") ++ suffix(req))
            }

          } else {
            dst = mkPath(Path.Utf8("1.1", req.method.toString, host.toLowerCase) ++ suffix(req))
          }

          Future.value(new IdentifiedRequest(dst, req))
        case _ =>
          Future.value(
            new UnidentifiedRequest(
              s"${Version.Http11} request missing hostname"
            )
          )
      }
  }
}
