# Oracle(c) Java(tm) Server JRE 8u221 + Cassandra 3.11.6 + Prometheus JMX exporter + Jolokia exporter
+ jemalloc1
 
Current version: [Cassandra v3.11.6](https://github.com/smok-serwis/cassandra/releases/tag/3.11.6), 
now with more configurability through the envs!

Due to myriad of different licenses employed here, please take a look at
the [summary detailed here](/LICENSE.md).

# Usage

Since this uses Oracle(c) Java(tm) JDK 8, you need to **define environment variable `I_ACCEPT_ORACLE_JAVA_LICENSE` in order for container to run.**
This means that you accept the [Oracle Technology Network License Agreement for Oracle Java SE](/java-jre/LICENSE.md).
This is the only environment variable you need to set in order for this Cassandra to run.

You don't need to make your images basing off this one.
`cassandra.yaml` will be set as you set particular environment variables.
Just set envs as needed. See [Dockerfile](/Dockerfile) and [entrypoint.py](/entrypoint.py) for details.

This exports three volumes - 
1. for data (_/var/lib/cassandra_),
2. for commitlog (_/var/lib/cassandra/commitlog_),
3. for logs (_/var/log/cassandra_)

Best mount them as bind.

Recommended options are `--network host --privileged`, althrough passing the external host
IP in _BROADCAST_ADDRESSes_ and using _auto_ for normal addresses works fine with a bridge network.

Any arguments passed to the entry point will be called as through a Cassandra was called. Any extra arguments
will be passed there, after a `cassandra -f`.

# Parameters

Set `ADDRESS_FOR_ALL` for a variable that will replace all _ADDRESS.

Following env's values will be placed in _cassandra.yaml_ verbatim (ie, withouting quotes)
* **BROADCAST_ADDRESS**, **LISTEN_ADDRESS**, **RPC_ADDRESS**, **RPC_BROADCAST_ADDRESS** (unless `ADDRESS_FOR_ALL` was given, in that case it will take precedence)
* **CLUSTER_NAME** (will be automatically escaped with quotes), default is _Test Cluster_
* **SEED_NODES** - list of comma separated IP addresses to bootstrap the cluster from
* **STREAMING_SOCKET_TIMEOUT_IN_MS** - prereably set it to a large large timeout to prevent disconnections during streaming large fixes. Minimally 24 hours. Default is one hour

In general, if it's found in [cassandra.yaml](/etc/cassandra/cassandra.yaml) with a dollar sign preceding it, it is safe to assume
that environment variable with a given name will be substituted for it.

If you need quotes, bring them with you. See for example how `CLUSTER_NAME` is set.

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
* **COMMITLOG_SEGMENT_SIZE** - size of a commit log segment, in MB. Defaults to 32
* **DISABLE_PROMETHEUS_EXPORTER** - if set, Prometheus' exporter will be disabled
* **COMMITLOG_SYNC** - [RTFM](etc/cassandra/cassandra.yaml). Defaults to _periodic_

# Enabling JMX

To enable JMX [without SSL] set the environment variable _LOCAL_JMX_ to _no_, and the
environment variable _JMX_REMOTE_PASSWORD_ to target remote password.

This way you will have two users created - `monitorRole` with read-only permissions, and `controlRole`
with read-write JMX permissions, both having the password that you set.

# Optionals

Following env's would be nice to have, but are not required:

* **CASSANDRA_DC** - name of this DC that Cassandra is in. _dc1_ by default.
* **CASSANDRA_RACK** - name of the rack that Cassandra is in, _rack1_ by default.

# Jolokia

Jolokia is enabled by default and listens on port 8080. If you define the env `DISABLE_JOLOKIA` 
t won't be loaded.

# Extra arguments
This simply launches cassandra with a -f flag, and passes any extra arguments to that cassandra.

# Health check

This container spots a built-in healthcheck. It is done by invoking "nodetool status" and seeing it's exit code.
This assumes that 30 minutes will be a sufficient time for your Cassandra to get up and read it's commit logs and initialize.
If this is not the case, start the container with suitable `docker run --health-start-period`.

To enable health check just set the environment variable `HEALTHCHECK_ENABLE` to `1`.

If you choose not to enable the health check, the container will be always marked as healthy.

# Bash

If you invoke this container with a single argument of "bash", it will drop you to a shell
without starting anything.

## Extra JVM_OPTS

If you set an env called **EXTRA1** it will get automatically appended to [cassandra-env.sh](/cassandra-env.sh),
producing an extra line of:
```bash
JVM_OPTS="$JVM_OPTS ${EXTRA1}"\n
```
You can add any number, starting from numbering them EXTRA1, without any limit.
It's important that they are consecutive numbers. These will simply enlarge your `JVM_OPTS`. You can for example
use it to [replace a dead node](https://docs.datastax.com/en/archived/cassandra/3.0/cassandra/operations/opsReplaceNode.html).
