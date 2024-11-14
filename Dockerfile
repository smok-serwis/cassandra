FROM debian:bullseye


ENV DEBIAN_FRONTEND=noninteractive
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1


RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-17-jre-headless gnupg2 python3 libjemalloc2 wget && \
    apt-get clean

RUN mkdir -p /etc/ssl/certs/java/ && \
    apt install --reinstall -o Dpkg::Options::="--force-confask,confnew,confmiss" --reinstall ca-certificates-java ssl-cert openssl ca-certificates && \
    apt-get clean


ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

LABEL apache.cassandra.version="5.0.2"

WORKDIR /tmp
COPY apache-cassandra-5.0.2-bin.tar.gz /tmp/apache-cassandra-5.0.2-bin.tar.gz
RUN tar zxf apache-cassandra-5.0.2-bin.tar.gz && \
    cd apache-cassandra-5.0.2 && \
    chmod ugo+rx bin/* && \
    cp bin/* /usr/sbin/ && \
    mv conf /etc/cassandra && \
    mkdir -p /usr/share/cassandra && \
    mv lib /usr/share/cassandra/lib/ && \
    cd pylib && \
    apt-get update && \
    apt-get install -y --no-install-recommends python3-distutils python3-pip python3-dev build-essential && \
    pip install Cython && \
    apt-get clean && \
    python3 setup.py install && \
    cd / && \
    rm -rf /tmp/apache*


# JMX agent
COPY jmx-exporter/jmx_prometheus_javaagent-0.12.0.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.12.0.jar
COPY jmx-exporter/jolokia-jvm-1.6.2-agent.jar /usr/share/cassandra/lib/jolokia-jvm-1.6.2-agent.jar
COPY jmx-exporter/jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Jaeger tracing
COPY jaeger/cassandra-jaeger-tracing-4.1.0.jar /usr/share/cassandra/lib/cassandra-jaeger-tracing-4.1.0.jar

# Our config - base files

ADD etc/cassandra/* /etc/cassandra/
RUN ln -s /usr/lib/x86_64-linux-gnu/libjemalloc.so.2 /usr/local/lib/libjemalloc.so
# Entry point
ADD entrypoint.py /entrypoint.py
RUN chmod ugo+x /entrypoint.py /usr/share/cassandra/lib/*.jar /usr/share/cassandra/lib/*.jar && \
    ln -s /usr/share/cassandra/lib/jamm-0.4.0.jar /usr/sbin/jamm-0.4.0.jar

ENTRYPOINT ["/entrypoint.py"]

WORKDIR /usr/share/cassandra
# Health check - this will work only if env HEALTHCHECK_ENABLE is set to some other value than "0"
HEALTHCHECK --start-period=30m --retries=3 CMD ["/entrypoint.py", "healthcheck"]


ENV CASSANDRA_HOME=/usr/share/cassandra \
    CASSANDRA_CONF=/etc/cassandra \
    LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    SEED_NODES=auto \
    JAEGER_TRACE_KEY=jaeger-trace \
    GC=G1 \
    LOG_GC=none
