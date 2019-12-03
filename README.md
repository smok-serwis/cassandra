# Oracle(c) Java(tm) Server JRE 8u221 + Cassandra 3.0.9 + Prometheus JMX exporter
+ jemalloc1
 
Current version: [Cassandra v3.0.9](https://github.com/smok-serwis/cassandra/releases/tag/v3.0.8), how with more configurability through the envs!
Even better than [v3.0.8](https://github.com/smok-serwis/cassandra/releases/tag/v3.0.8).

Due to myriad of different licenses employed here, please take a look at the [license](/LICENSE.md).

# Usage

Since this uses Oracle(c) Java(tm) Server JRE 8u221, you need to **define environment variable `I_ACCEPT_ORACLE_JAVA_LICENSE` in order for container to run.**
This means that you accept the [Oracle Technology Network License Agreement for Oracle Java SE](https://www.oracle.com/downloads/licenses/javase-license1.html).


You don't need to make your images basing off this one.
`cassanra.yaml` will be set as you set particular environment variables.
Just set envs as needed. See [Dockerfile](/Dockerfile) and [entrypoint.py](/entrypoint.py) for details.

This exports three volumes - 
1. for data (_/var/lib/cassandra_),
2. for commitlog (_/var/lib/cassandra/commitlog_),
3. for logs (_/var/log/cassandra_)

Best mount them as bind.

Recommended options are `--network host --privileged`

Any arguments passed to the entry point will be called as through a Cassandra was called. Any extra arguments
will be passed there, after a `cassandra -f`.

# Parameters

Set `ADDRESS_FOR_ALL` for a variable that will replace all _ADDRESS.

Following env's values will be placed in _cassandra.yaml_ verbatim (ie, withouting quotes)
* **BROADCAST_ADDRESS**, **LISTEN_ADDRESS**, **RPC_ADDRESS**, **RPC_BROADCAST_ADDRESS** (unless `ADDRESS_FOR_ALL` was given, in that case it will take precedence)
* **CLUSTER_NAME** (will be automatically escaped with quotes), default is _Test Cluster_
* **SEED_NODES** - list of comma separated IP addresses to bootstrap the cluster from
* **STREAMING_SOCKET_TIMEOUT_IN_MS** - prereably set it to a large large timeout to prevent disconnections during streaming large fixes. Minimally 24 hours. Default is one hour


## Extra parameters for [RTFM](etc/cassandra/cassandra.yaml)

* **NUM_TOKENS** - by default 256, but take care
* **START_RPC** - whether to start classic Cassandra Thrift RPC. Default is _false_, but you might wish to use _true_
* **RPC_PORT** - port to which start Thrift RPC, if it is requested.
* **DISK_OPTIMIZATION_STRATEGY** - pass _solid_ or _ssd_
* **ENDPOINT_SNITCH** - endpoint snitch to use, by default it's _SimpleSnitch_
* **AUTHENTICATOR** - by default _AllowAllAuthenticator_, can use also _PasswordAuthenticator_
* **AUTHORIZER** - by default _AllowAllAuthorizer_, can use also _CassandraAuthorizer_
* **PARTITIONER** - partitioner to use, by default _org.apache.cassandra.dht.Murmur3Partitioner_
* **ROW_CACHE_SIZE_IN_MB** - row cache size to use. By default is 0, which means disabled
* **TOMBSTONE_WARN_THRESHOLD** and **TOMBSTONE_FAIL_THRESHOLD** - [RTFM](etc/cassandra/cassandra.yaml)
* **COLUMN_INDEX_SIZE_IN_KB** - [RTFM](etc/cassandra/cassandra.yaml)
* **BATCH_SIZE_FAIL_THRESHOLD_IN_KB** - maximum size of the batch that Cassandra will fail. [RTFM](etc/cassandra/cassandra.yaml) 
* **REQUEST_SCHEDULER** - defaults to _org.apache.cassandra.scheduler.NoScheduler_
* **ENABLE_USER_DEFINED_FUNCTIONS'** - defaults to _false_
* **DISABLE_PROMETHEUS_EXPORTER** - if set, Prometheus' exporter will be disabled

# Optionals

Following env's would be nice to have, but are not required:

* **CASSANDRA_DC** - name of this DC that Cassandra is in. _dc1_ by default.
* **CASSANDRA_RACK** - name of the rack that Cassandra is in, _rack1_ by default.

If you set the environment value of **ENABLE_MX4J** to any value, Cassandra will have it's MX4J enabled.
It will listen on port 8081.

# Extra arguments
This simply launches cassandra with a -f flag, and passes any extra arguments to that cassandra.

## Extra JVM_OPTS

If you set an env called **EXTRA1** it will get automatically appended to [cassandra-env.sh](/cassandra-env.sh),
producing an extra line of:
```bash
JVM_OPTS="$JVM_OPTS ${EXTRA1}"\n
```
You can add any number, starting from numbering them EXTRA1, without any limit.
It's important that they are consecutive numbers. These will simply enlarge your `JVM_OPTS`. You can for example
use it to [replace a dead node](https://docs.datastax.com/en/archived/cassandra/3.0/cassandra/operations/opsReplaceNode.html).

# MX4J and caveat emptor.

So far MX4J doesn't seem to work. It follows with such a traceback:
```
ERROR:  'Use of the extension function 'http://xml.apache.org/xslt/java:encode' is not allowed when the secure processing feature is set to true.'
Transformation exception
javax.xml.transform.TransformerException: java.lang.RuntimeException: Use of the extension function 'http://xml.apache.org/xslt/java:encode' is not allowed when the secure processing feature is set to true.
        at com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl.transform(TransformerImpl.java:737)
        at com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl.transform(TransformerImpl.java:343)
        at mx4j.tools.adaptor.http.XSLTProcessor.writeResponse(XSLTProcessor.java:149)
        at mx4j.tools.adaptor.http.HttpAdaptor.postProcess(HttpAdaptor.java:766)
        at mx4j.tools.adaptor.http.HttpAdaptor$HttpClient.run(HttpAdaptor.java:981)
Caused by: java.lang.RuntimeException: Use of the extension function 'http://xml.apache.org/xslt/java:encode' is not allowed when the secure processing feature is set to true.
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.runTimeError(BasisLibrary.java:1618)
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.runTimeError(BasisLibrary.java:1622)
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.unallowed_extension_functionF(BasisLibrary.java:450)
        at GregorSamsa.uri$dash$encode()
        at GregorSamsa.mbean()
        at GregorSamsa.domain()
        at GregorSamsa.template$dot$14()
        at GregorSamsa.applyTemplates()
        at GregorSamsa.applyTemplates()
        at GregorSamsa.transform()
        at com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet.transform(AbstractTranslet.java:620)
        at com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl.transform(TransformerImpl.java:730)
        ... 4 more
---------
java.lang.RuntimeException: Use of the extension function 'http://xml.apache.org/xslt/java:encode' is not allowed when the secure processing feature is set to true.
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.runTimeError(BasisLibrary.java:1618)
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.runTimeError(BasisLibrary.java:1622)
        at com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary.unallowed_extension_functionF(BasisLibrary.java:450)
        at GregorSamsa.uri$dash$encode()
        at GregorSamsa.mbean()
        at GregorSamsa.domain()
        at GregorSamsa.template$dot$14()
        at GregorSamsa.applyTemplates()
        at GregorSamsa.applyTemplates()
        at GregorSamsa.transform()
        at com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet.transform(AbstractTranslet.java:620)
        at com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl.transform(TransformerImpl.java:730)
        at com.sun.org.apache.xalan.internal.xsltc.trax.TransformerImpl.transform(TransformerImpl.java:343)
        at mx4j.tools.adaptor.http.XSLTProcessor.writeResponse(XSLTProcessor.java:149)
        at mx4j.tools.adaptor.http.HttpAdaptor.postProcess(HttpAdaptor.java:766)
        at mx4j.tools.adaptor.http.HttpAdaptor$HttpClient.run(HttpAdaptor.java:981)
```