FROM debian:stretch-slim


ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends gnupg dirmngr debconf-utils apt-utils ca-certificates && \
    APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 && \


# Oracle Java. Accept no substitutes.
ADD oraclejava9.sources.list /etc/apt/sources.list.d/oraclejava9sources.list


RUN APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
RUN APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv C2518248EEA14886
RUN APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 gpg --export --armor C2518248EEA14886 | apt-key add -
RUN apt-get update && \
    echo debconf shared/accepted-oracle-license-v1-1 select true |  debconf-set-selections && \
    apt-get install -y -v --no-install-recommends python-minimal oracle-java9-installer && \
    apt-get install -y -v oracle-java9-set-default && \
    apt-get clean

# Install jemalloc
RUN apt-get update && \
    apt-get install -y --no-install-recommends libjemalloc1 && \
    apt-get clean

LABEL CassandraVersion="3.0.9"


# Install python-support that dsc30 whine about not having

ADD http://launchpadlibrarian.net/109052632/python-support_1.0.15_all.deb /tmp/python-support_1.0.15_all.deb
RUN apt-get update && \
    apt-get install --upgrade -y python>=2.5 && \
    dpkg -i /tmp/python-support_1.0.15_all.deb && \
    apt-get clean

# Cassandra
ADD cassandra.sources.list /etc/apt/sources.list.d/cassandra.sources.list
ADD http://debian.datastax.com/debian/repo_key /tmp/repo_key
RUN  cat /tmp/repo_key | APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1 apt-key add - && \
     apt-get update && \
     apt-get install -y --no-install-recommends cassandra dsc30 cassandra-tools && \
     apt-get clean


# JMX agent
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.9/jmx_prometheus_javaagent-0.9.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.9.jar
ADD jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Our config - base files
ADD cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD cassandra.yaml /etc/cassandra/cassandra.yaml
ADD cassandra-rackdc.properties /etc/cassandra/cassandra-rackdc.properties

# Entry point
ADD entrypoint.py /entrypoint.py
RUN chmod ugo+x /entrypoint.py
ENTRYPOINT ["/entrypoint.py"]

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
    LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    CLUSTER_NAME=TestCluster \
    SEED_NODES=auto \
    STREAMING_SOCKET_TIMEOUT_IN_MS=360000000 \
    NUM_TOKENS=256
