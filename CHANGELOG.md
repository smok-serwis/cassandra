# v5.0.2

* heap dump is disabled by default (the data just accumulates too fast)
* minor fix for setups that did not define nodetool, which still runs under the CMS collector
* deprecated ADDRESS_FOR_ALL and auto in addresses* minor fix for setups that did not define nodetool, which still runs under the CMS collector
* deprecated ADDRESS_FOR_ALL and auto in addresses* We're now running Cassandra 5.0.2
* Major configuration changes, please read [the end of README](README.md)
* CMS GC was removed
* user scripted defined functions were removed
* [casssandra-jaeger-tracer](https://github.com/smok-serwis/cassandra-jaeger-tracing.git) bumped to 4.1.1
* enabled submitting traces directly to Jaeger-Collector
* it's now possible to disable prometheus
* added configurable JMX interface binding

# v4.1.7

* bump Cassandra to 4.1.7
* added extra options to set memtable size
* added support for large pages
* switched back to OpenJDK 11

# v3.11.6

* still used Oracle JRE

# v4.0.5

* switched to OpenJDK 11
* also you got your MATERIALIZED INDEXES and SASI INDEXES enabled
* [casssandra-jaeger-tracer](https://github.com/smok-serwis/cassandra-jaeger-tracing.git) is ready to boot with version 4.0.5
  with [minor limitations](https://github.com/infracloudio/cassandra-jaeger-tracing/issues/10)
* enabled trickle_fsync
* default TOMBSTONE_WARN_THRESHOLD set to 10000
* switched to G1 garbage collector
* added cross_node_timeout, make sure that your machines run NTP
* restored jemalloc

# v4.0.5.1

* added _NEW_HEAP_SIZE_
* removed write survey by default

# v4.0.5.2

* added _BATCHLOG_REPLAY_THROTTLE_ and set it to more reasonable value
* added _JMX_AUTH_

# v4.0.5.3

* added _STREAM_THROUGHPUT_OUTBOUND_MEGABITS_PER_SEC_
* added _DISK_OPTIMIZATION_STRATEGY_
