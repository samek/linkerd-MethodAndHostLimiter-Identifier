
version := "0.2"
def twitterUtil(mod: String) =
  "com.twitter" %% s"util-$mod" %  "6.40.0"

def finagle(mod: String) =
  "com.twitter" %% s"finagle-$mod" % "6.41.0"

def linkerd(mod: String) =
  "io.buoyant" %% s"linkerd-$mod" % "0.8.6"

val MethodAndHostLimiterIdentifier =
  project.in(file("MethodAndHostLimiter-Identifier")).
    settings(
      scalaVersion := "2.11.7",
      organization := "si.poponline",
      name := "MethodAndHostLimiter-Identifier",
      version := "0.2",
      resolvers ++= Seq(
        "twitter" at "https://maven.twttr.com",
        "local-m2" at ("file:" + Path.userHome.absolutePath + "/.m2/repository")
      ),
      libraryDependencies ++=
        finagle("http") % "provided" ::
        twitterUtil("core") % "provided" ::
        linkerd("core") % "provided" ::
          linkerd("protocol-http") % "provided" ::
        Nil,
      assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
    )
