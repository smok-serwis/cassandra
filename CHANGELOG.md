# v3.11.6

* still used Oracle JRE

# v4.0.5

* switched to OpenJDK 11. Jaeger-tracing is botched until I understand it
* also you got your MATERIALIZED INDEXES and SASI INDEXEX enabled
* [casssandra-jaeger-tracer](https://github.com/smok-serwis/cassandra-jaeger-tracing.git) is ready to boot with version 4.0.5
  with [minor limitations](https://github.com/infracloudio/cassandra-jaeger-tracing/issues/10)
* enabled trickle_fsync