FROM debian:bullseye


ENV DEBIAN_FRONTEND=noninteractive
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1

RUN apt-get update && \
    apt-get install -y --no-install-recommends default-jre-headless gnupg2 python3 libjemalloc2 && \
    apt-get clean

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

LABEL apache.cassandra.version="4.0.5"

# Cassandra
ADD cassandra.sources.list /etc/apt/sources.list.d/cassandra.sources.list
ADD https://www.apache.org/dist/cassandra/KEYS /tmp/repo_key
RUN  apt-key add /tmp/repo_key && \
     apt-get update && \
     apt-get install -y --no-install-recommends cassandra cassandra-tools && \
     apt-get clean


# JMX agent
ADD jmx-exporter/jmx_prometheus_javaagent-0.12.0.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.12.0.jar
ADD jmx-exporter/jolokia-jvm-1.6.2-agent.jar /usr/share/cassandra/lib/jolokia-jvm-1.6.2-agent.jar
ADD jmx-exporter/jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Jaeger tracing
ADD jaeger/cassandra-jaeger-tracing-4.0.5.jar /usr/share/cassandra/lib/cassandra-jaeger-tracing-4.0.5.jar

# Our config - base files
ADD etc/cassandra/cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD etc/cassandra/jmxremote.access /etc/cassandra/jmxremote.access
ADD etc/cassandra/cassandra.yaml /etc/cassandra/cassandra.yaml
ADD etc/cassandra/cassandra-rackdc.properties /etc/cassandra/cassandra-rackdc.properties
ADD etc/cassandra/jvm.options /etc/cassandra/jvm.options
ADD etc/cassandra/jvm11-server.options /etc/cassandra/jvm11-server.options
ADD etc/cassandra/jvm.options.log_gc.file /etc/cassandra/jvm.options.log_gc.file
ADD etc/cassandra/jvm.options.log_gc.stdout /etc/cassandra/jvm.options.log_gc.stdout
RUN ln -s /usr/lib/x86_64-linux-gnu/libjemalloc.so.2 /usr/local/lib/libjemalloc.so
# Entry point
ADD entrypoint.py /entrypoint.py
RUN chmod ugo+x /entrypoint.py
ENTRYPOINT ["/entrypoint.py"]

RUN chown -R cassandra:cassandra /var/lib/cassandra

# Health check - this will work only if env HEALTHCHECK_ENABLE is set to some other value than "0"
HEALTHCHECK --start-period=30m --retries=3 CMD ["/entrypoint.py", "healthcheck"]

# Defaults - these are used to alter cassandra.yaml before start
ENV LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    SEED_NODES=auto \
    JAEGER_TRACE_KEY=jaeger-trace \
    GC=G1 \
    LOG_GC=none
