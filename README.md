# OpenJDK 11 + Cassandra 5.0.2 + Prometheus JMX exporter + Jolokia exporter + jemalloc2 
 
Current version: [Cassandra v5.0.2](https://github.com/smok-serwis/cassandra/releases/tag/5.0.2), 
now with more configurability through the envs!

Due to myriad of different licenses employed here, please take a look at
the [summary detailed here](/LICENSE.md).

## Ports it listens on

* 7199 - JMX
* 7198 - Prometheus exporter
* 9042 - Native transport
* 7000 - Internode communications
* 9160 - Thrift client (disabled by default, set env `START_RPC` to `true` to enable it)

## Volumes of interest

* _/var/lib/cassandra_ - data partition
* _/var/lib/cassandra/commitlog_ - commitlog partition
* _/var/lib/cassandra/logs_ - logs

## Usage

Since this uses OpenJDK 17, you do not need to set anymore any weird environment variables. Just enjoy!

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

Or, if you pass `bash` command, a bash shell will be set for you with required envs.

## Parameters

Set `ADDRESS_FOR_ALL` for a variable that will replace all _ADDRESS.

Following env's values will be placed in _cassandra.yaml_ verbatim (ie, withouting quotes)
* **BROADCAST_ADDRESS**, **LISTEN_ADDRESS**, **RPC_ADDRESS**, **RPC_BROADCAST_ADDRESS** (unless `ADDRESS_FOR_ALL` was given, in that case it will take precedence)
* **CLUSTER_NAME** (will be automatically escaped with quotes), default is _Test Cluster_
* **SEED_NODES** - list of comma separated IP addresses to bootstrap the cluster from

In general, if it's found in [cassandra.yaml](/etc/cassandra/cassandra.yaml) with a dollar sign preceding it, it is safe to assume
that environment variable with a given name will be substituted for it.

If you need quotes, bring them with you. See for example how `CLUSTER_NAME` is set.

## Extra parameters for [RTFM](etc/cassandra/cassandra.yaml)

Note that where sizes are required, you should postfix them with MiB or KiB. Where tiems are requires, use milliseconds (ms)

* **NUM_TOKENS** - by default 256, but take care
* **START_RPC** - whether to start classic Cassandra Thrift RPC. Default is _false_, but you might wish to use _true_
* **RPC_PORT** - port to which start Thrift RPC, if it is requested.
* **DISK_OPTIMIZATION_STRATEGY** - pass _spinning_ or _ssd_, any other option will fail with an error. Default is _ssd_
* **ENDPOINT_SNITCH** - endpoint snitch to use, by default it's _SimpleSnitch_
* **AUTHENTICATOR** - by default _AllowAllAuthenticator_, can use also _PasswordAuthenticator_
* **AUTHORIZER** - by default _AllowAllAuthorizer_, can use also _CassandraAuthorizer_
* **PARTITIONER** - partitioner to use, by default _org.apache.cassandra.dht.Murmur3Partitioner_
* **ROW_CACHE_SIZE** - row cache size to use. By default is 0MiB, which means disabled.
* **TOMBSTONE_WARN_THRESHOLD** and **TOMBSTONE_FAIL_THRESHOLD** - there's no unit. [RTFM](etc/cassandra/cassandra.yaml)
* **COLUMN_INDEX_SIZE** - [RTFM](etc/cassandra/cassandra.yaml), default is 64KiB
* **BATCH_SIZE_FAIL_THRESHOLD** - maximum size of the batch that Cassandra will fail. Unit is KiB. [RTFM](etc/cassandra/cassandra.yaml)
* **BATCHLOG_REPLAY_THROTTLE** - maximum speed at which commit log will be replayed. Default is 512 MiB, which means 512 MiB/s.
* **REQUEST_SCHEDULER** - defaults to _org.apache.cassandra.scheduler.NoScheduler_
* **READ_REQUEST_TIMEOUT** - defaults to _5000ms_
* **RANGE_REQUEST_TIMEOUT** - defaults to _10000ms_
* **STREAM_THROUGHPUT_OUTBOUND** - defaults to _25MiB/s_
* **WRITE_REQUEST_TIMEOUT** - defaults to _2000_
* **MAX_HEAP_SIZE** - defaults to _48g_
* **NEW_HEAP_SIZE** - defaults to _10g_ **don't confuse with HEAP_NEWSIZE**!!
* **COUNTER_WRITE_REQUEST_TIMEOUTS** - defaults to _5000ms_
* **JMX_AUTH** - defaults to _yes_, set to _no_ to disable JMX auth
* **CAS_CONTENTION_TIMEOUT** - defaults to _2000ms_
* **TRUNCATE_REQUEST_TIMEOUT** - defaults to _60000ms_
* **REQUEST_TIMEOUT** - defaults to _15000ms_
* **COMPACTION_THROUGHPUT** - defaults to _64MiB/s_
* **MAX_HINT_WINDOW** - defaults to _3h_
* **ENABLE_USER_DEFINED_FUNCTIONS'** - defaults to _false_
* **ENABLE_SCRIPTED_USER_DEFINED_FUNCTIONS** - defaults to _false_
* **COMMITLOG_SEGMENT_SIZE** - size of a commit log segment. Defaults to 32MiB.
* **DISABLE_PROMETHEUS_EXPORTER** - if set, Prometheus' exporter will be disabled
* **KEY_CACHE_SIZE** - default is *auto*, unit is MiB
* **FILE_CACHE_SIZE** - size of chunk cache, unit is MiB
* **COMMITLOG_TOTAL_SPACE** - space to use for commit log. Please specify the values, the defaults are difficult to explain.
* **COMMITLOG_SYNC** - [RTFM](etc/cassandra/cassandra.yaml). Defaults to _periodic_
* **MEMTABLE_HEAP_SIZE** - size of heap size for memtables. Default is 1024MiB. Postfix it with MiB please.
* **MEMTABLE_OFF_HEAP_SIZE** - size of off-heap memtables. Default is 512MiB. Postfix it with MiB please.
* **STORAGE_COMPATIBILITY_MODE** - one used for updating. Please read the end of this article.

## Enabling JMX

To enable JMX [without SSL] set the environment variable _LOCAL_JMX_ to _no_, and the
environment variable _JMX_REMOTE_PASSWORD_ to target remote password.

This way you will have two users created - `monitorRole` with read-only permissions, and `controlRole`
with read-write JMX permissions, both having the password that you set.

## Optionals

Following env's would be nice to have, but are not required:

* **CASSANDRA_DC** - name of this DC that Cassandra is in. _dc1_ by default.
* **CASSANDRA_RACK** - name of the rack that Cassandra is in, _rack1_ by default.

## Jolokia

Jolokia is enabled by default and listens on port 8080. If you define the env `DISABLE_JOLOKIA` 
t won't be loaded.

## Extra arguments
This simply launches cassandra with a -f flag, and passes any extra arguments to that cassandra.

## Health check

This container spots a built-in healthcheck. It is done by invoking "nodetool status" and seeing it's exit code.
This assumes that 30 minutes will be a sufficient time for your Cassandra to get up and read it's commit logs and initialize.
If this is not the case, start the container with suitable `docker run --health-start-period`.

To enable health check just set the environment variable `HEALTHCHECK_ENABLE` to `1`.

If you choose not to enable the health check, the container will always be marked as healthy.

# Enabling Jaeger tracing

In order to enable Jaeger tracing just define the envs `JAEGER_AGENT_HOST`, and optionally
`JAEGER_AGENT_PORT`, which is 6831 by default.

Note that this uses [our custom](https://github.com/smok-serwis/cassandra-jaeger-tracing)
version of `cassandra-jaeger-tracing`.

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

## Using the G1 Garbage Collector

The G1 garbage collector is shipped as default by Cassandra 4.1.7. There's no need to set it explicitly,
as there's no need to revert to earlier concurrent mark'n'sweep.

## Enabling assertions

Assertions are disabled by default in order to provide a modest speed-up. To enable them, use an
env called `ENABLE_ASSERTIONS` and set it to `1`.

## Logging GC

GC can be logged to:

* `not logged` (default value of `LOG_GC=none`)
* file /var/log/cassandra.gc (`LOG_GC=file`)
* standard output (`LOG_GC=stdout`)


## correct sysctl settings

* vm.max_map_count = 1048575
* echo 8 > /sys/block/sda/queue/read_ahead_kb for the drive storing Cassandra data


# Migrating to Cassandra 5

For every node:
    1. Stop it
    2. Check that environment variables match (they changed a lot, they added units, this README details them all)
    3. Set `STORAGE_COMPATIBILITY_MODE` to `UPGRADING`
    4. Start the node, wait for it to join the cluster.
    5. Run `nodetool upgradesstables` on all SSTables that this node has.

Now for every node:
    1. Stop it
    2. Change `STORAGE_COMPATIBILITY_MODE` to `NONE`
    3. Start it