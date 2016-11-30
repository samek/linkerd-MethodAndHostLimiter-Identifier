# Method and Host Limiter Identifier

This is an identifier plugin for [linkerd](https://linkerd.io)



## Building

This plugin is built with sbt.  Run sbt from the root directory.

```
./sbt MethodAndHostLimiter-Identifier:assembly
```

This will produce the plugin jar at
`MethodAndHostLimiter-Identifier/target/scala-2.11/MethodAndHostLimiter-Identifier-assembly-0.1-SNAPSHOT.jar `.

## Installing

To install this plugin with linkerd, simply move the plugin jar into linkerd's
plugin directory (`$L5D_HOME/plugins`).  Then add a classifier block to the
router in your linkerd config:

```
routers:
- protocol: http
  label: incoming
  identifier:
    kind: si.poponline.MethodAndHostLimiter
    banThreshold: 30
    banIntervalCleanUp: 600
    banWindowTime: 10
```

Where banThreshold is how many times one ip can access any service in banWindowTime (seconds). 
If it passes banThreshold it will be banned for the duration of banIntervalCleanUp (seconds)
