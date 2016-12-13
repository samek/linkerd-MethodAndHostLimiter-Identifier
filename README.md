# Method and Host Limiter Identifier

This is an identifier plugin for [linkerd](https://linkerd.io)



## Building

This plugin is built with sbt.  Run sbt from the root directory.

```
./sbt MethodAndHostLimiter-Identifier:assembly
```

This will produce the plugin jar at
`MethodAndHostLimiter-Identifier/target/scala-2.11/MethodAndHostLimiter-Identifier-assembly-0.2.jar`.

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

Currently It's activated only if header contains Cres-Client-IP with the ip value (which is added by our front loadbalancer)

## Usage

It will look for header (Cres-Client-IP value which is send by our Loadbalancer)
 and monitor how many requests were done to a hostname by that ip. 
 
 So It will ban on requested_hostname+ip pair. 


eg. 
Make sure you change: 
1. config to point to your namerd instance
2. use hostname that your namerd knows about. 
```bash

./sbt MethodAndHostLimiter-Identifier:assembly
mkdir temp
cd temp
wget https://github.com/BuoyantIO/linkerd/releases/download/0.8.4/linkerd-0.8.4-exec
chmod 755 ./linkerd-0.8.4-exec
cp ../MethodAndHostLimiter-Identifier/config-sample.yaml
mkdir plugins
cp  ../MethodAndHostLimiter-Identifier/target/scala-2.11/MethodAndHostLimiter-Identifier-assembly-0.2.jar plugins/
./linkerd-0.8.4-exec  config-sample.yaml 
```
In another window:
```bash
export http_proxy=localhost:4140
while [ 1 == 1 ]; do curl HOSTNAME_TO_CHANGE/v0/news/get/1 -H "Cres-Client-IP: 10.0.0.1" ; sleep 0.2; done;
```