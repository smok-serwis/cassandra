Oracle Java + Cassandra 3.0 + Prometheus JMX exporter

See [main branch](https://github.com/smok-serwis/cassandra) for full description.
 
This is a "development use"-Cassandra, mostly for unit tests. It auto detect addresses,
and has no JMX exporter or jemalloc.
 
You still need to set  `I_ACCEPT_ORACLE_JAVA_LICENSE` in order for container to run.**
This means that you accept the [Oracle Binary Code License Agreement for the Java SE Platform Products and JavaFX](http://www.oracle.com/technetwork/java/javase/terms/license/index.html).

This image _has no volumes_, which means you can build your custom preloaded Cassandra images.
