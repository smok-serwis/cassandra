Oracle Java + Cassandra 3.0 + Prometheus JMX exporter

# Usage

Since this uses Oracle Java, you need to **define environment variable `I_ACCEPT_ORACLE_JAVA_LICENSE` in order for container to run.**
This means that you accept the [Oracle Binary Code License Agreement for the Java SE Platform Products and JavaFX](http://www.oracle.com/technetwork/java/javase/terms/license/index.html).


You don't need to make your images basing off this one.
`cassanra.yaml` will be set as you set particular environment variables.
Just set envs as needed. See [Dockerfile](/Dockerfile) for details.

This exports two volumes - 
one for data (_/var/lib/cassandra_),
and one for commitlog (_/var/lib/cassandra/commitlog_). 
Best mount them as bind.

Use host networking, or your addresses might go crazy.

Run with `--cap add CAP_IPC_LOCK` and `--cap-add RLIMIT_MEMLOCK`.

NTP client is provided and started as part of the set :)