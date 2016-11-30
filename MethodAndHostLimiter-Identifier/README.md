# Header Classifier

This is an HTTP response classifier plugin for [linkerd](https://linkerd.io)
to serve as an example of how to build and install linkerd plugins.  This
classifer inspects a response header (named "status" by default) to determine if
the response should be classified as a success or failure and if the request
should be retried.  A value of "success" means success, a value of "retry" means
retryable failure, and any other value means non-retryable failure.

## Building

This plugin is built with sbt.  Run sbt from the plugins directory.

```
./sbt header-classifer/assembly
```

This will produce the plugin jar at
`header-classifier/target/scala-2.11/header-classifier-assembly-0.1-SNAPSHOT.jar`.

## Installing

To install this plugin with linkerd, simply move the plugin jar into linkerd's
plugin directory (`$L5D_HOME/plugins`).  Then add a classifier block to the
router in your linkerd config:

```
routers:
- ...
  classifier:
    kind: io.buoyant.headerClassifier
    headerName: status
```
