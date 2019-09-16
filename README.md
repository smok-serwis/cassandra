# Oracle Java 9 + Cassandra 3.0.19 + Prometheus JMX exporter

Even better than [v3.0.8](https://github.com/smok-serwis/cassandra/releases/tag/v3.0.8), how with more configurability through the envs!

# Usage

Since this uses Oracle Java, you need to **define environment variable `I_ACCEPT_ORACLE_JAVA_LICENSE` in order for container to run.**
This means that you accept the [Oracle Binary Code License Agreement for the Java SE Platform Products and JavaFX](http://www.oracle.com/technetwork/java/javase/terms/license/index.html).


You don't need to make your images basing off this one.
`cassanra.yaml` will be set as you set particular environment variables.
Just set envs as needed. See [Dockerfile](/Dockerfile) for details.

This exports two volumes - 
one for data (_/var/lib/cassandra_),
and one for commitlog (_/var/lib/cassandra/commitlog_),
and one for logs (_/var/log/cassandra_)

Best mount them as bind.

Recommended options are `--network host --privileged`

If you need to pass any extra options, just put them in environment variables `EXTRA1`, `EXTRA2` and so on.

# cassandra-env.sh

If you set an env called **EXTRA1** it will get automatically appended to [cassandra-env.sh],
producing an extra line of:
```bash
JVM_OPTS="$JVM_OPTS ${EXTRA1}"\n
```
You can add any number, starting from numbering them EXTRA1, without any limit.
It's important that they are consecutive numbers.

# Parameters

Set `ADDRESS_FOR_ALL` for a variable that will replace all _ADDRESS.

Following env's values will be placed in _cassandra.yaml_ verbatim (ie, withouting quotes)L
* **BROADCAST_ADDRESS**, **LISTEN_ADDRESS**, **RPC_ADDRESS**, **RPC_BROADCAST_ADDRESS** (unless `ADDRESS_FOR_ALL` was given, in that case it will take precedence)
* **CLUSTER_NAME** (will be automatically escaped with quotes)
* **SEED_NODES** - list of comma separated IP addresses to bootstrap the cluster from
* **STREAMING_SOCKET_TIMEOUT_IN_MS** - prereably set it to a large large timeout to prevent disconnections during streaming large fixes. Minimally 24 hours.
* **NUM_TOKENS** - by default 256, but take care
* **START_RPC** - whether to start classic Cassandra Thrift RPC
* **RPC_PORT** - port to which stanrt Thrift RPC
* **DISK_OPTIMIZATION_STRATEGY** - pass _solid_ or _ssd_'
* **ENABLE_INTERHOST_JMX** - make JMX available over other hosts that localhost

## Parameters for [RTFM](cassandra.yaml)

* **AUTHENTICATOR** - by default _AllowAllAuhenticator_, can use also _PasswordAuthenticator_
* **TOMBSTONE_WARN_THRESHOLD** and **TOMBSTONE_FAIL_THRESHOLD** - [RTFM](cassandra.yaml)
* **COLUMN_INDEX_SIZE_IN_KB** - [RTFM](cassandra.yaml)
* **BATCH_SIZE_FAIL_THRESHOLD_IN_KB** - maximum size of the batch that Cassandra will fail. [RTFM](cassandra.yaml) 
* **REQUEST_SCHEDULER** - defaults to _org.apache.cassandra.scheduler.NoScheduler_
* **ENABLE_USER_DEFINED_FUNCTIONS'** - defaults to _false_

# Optionals

Following env's would be nice to have, but are not required:

* **CASSANDRA_DC** - name of this DC that Cassandra is in. _dc1_ by default.
* **CASSANDRA_RACK** - name of the rack that Cassandra is in, _rack1_ by default.

# Extra arguments
This simply launches cassandra with a -f flag, and passes any extra arguments to that cassandra.