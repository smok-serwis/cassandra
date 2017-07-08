FROM debian:jessie-slim


# Oracle Java. Accept no substitutes.
ADD webupd8team-java.list /etc/apt/sources.list.d/webupd8team-java.list
RUN echo debconf shared/accepted-oracle-license-v1-1 select true |  debconf-set-selections && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
    apt-get update && \
    mkdir /usr/share/man/man1 && \
    apt-get install -y --no-install-recommends python-minimal debconf-utils apt-utils oracle-java8-installer && \
    apt-get clean

# Install jemalloc
RUN apt-get update && \
    apt-get install -y libjemalloc1 && \
    apt-get clean

# Cassandra 3.0
ADD cassandra.sources.list /etc/apt/sources.list.d/cassandra.sources.list
ADD http://debian.datastax.com/debian/repo_key /tmp/repo_key
RUN  cat /tmp/repo_key | apt-key add - && \
     apt-get update && \
     apt-get install --no-install-recommends -y cassandra=3.0.9 dsc30 cassandra-tools && \
     apt-get clean


# JMX agent
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.9/jmx_prometheus_javaagent-0.9.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.9.jar
ADD jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Our config - base files
ADD cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD cassandra.yaml /etc/cassandra/cassandra.yaml

# Entry point
ADD entrypoint.py /entrypoint.py
ENTRYPOINT ["/usr/bin/python", "/entrypoint.py"]

# Exports

## Volumes - data and commit log and logs
VOLUME /var/lib/cassandra /var/lib/cassandra/commitlog /var/log/cassandra

## JMX exporter port
EXPOSE 7198

## Native transport
EXPOSE 9042

## Internode
EXPOSE 7000

# Defaults - these are used to alter cassandra.yaml before start
ENV MAX_HEAP_SIZE=1G \
    HEAP_NEWSIZE=100M \
    LISTEN_ADDRESS=127.0.0.1 \
    BROADCAST_ADDRESS=127.0.0.1 \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=127.0.0.1 \
    CLUSTER_NAME=TestCluster \
    SEED_NODES=127.0.0.1 \
    STREAMING_SOCKET_TIMEOUT_IN_MS=360000000 \
    NUM_TOKENS=256
