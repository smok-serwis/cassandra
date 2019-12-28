FROM debian:stretch-slim


ENV DEBIAN_FRONTEND=noninteractive
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1

RUN apt-get update && \
    apt-get install -y --no-install-recommends gnupg dirmngr debconf-utils apt-utils ca-certificates && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 && \
    apt-get clean


# Oracle Java. Accept no substitutes.
ADD java-jre/server-jre-8u221-linux-x64.tar.gz /usr/local/java/

ENV JAVA_HOME=/usr/local/java/jdk1.8.0_221
RUN update-alternatives --install "/usr/bin/java" "java" "${JAVA_HOME}/bin/java" 1 && \
    update-alternatives --install "/usr/bin/javac" "javac" "${JAVA_HOME}/bin/javac" 1
ENV PATH="${PATH}:${JAVA_HOME}/bin"

# Install jemalloc
RUN apt-get update && \
    apt-get install -y --no-install-recommends libjemalloc1 && \
    apt-get clean

LABEL apache.cassandra.version="3.0.9"


# Install python-support that dsc30 whine about not having

ADD python-support_1.0.15_all.deb /tmp/python-support_1.0.15_all.deb
RUN apt-get update && \
    apt-get install -y python>=2.5 && \
    dpkg -i /tmp/python-support_1.0.15_all.deb && \
    apt-get clean

# Cassandra
ADD cassandra.sources.list /etc/apt/sources.list.d/cassandra.sources.list
ADD http://debian.datastax.com/debian/repo_key /tmp/repo_key
RUN  cat /tmp/repo_key |  apt-key add - && \
     apt-get update && \
     apt-get install -y --no-install-recommends cassandra dsc30 cassandra-tools && \
     apt-get clean


# JMX agent
ADD jmx-exporter/jmx_prometheus_javaagent-0.12.0.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.12.0.jar
ADD jmx-exporter/mx4j-tools.jar /usr/share/cassandra/lib/mx4j-tools.jar
ADD jmx-exporter/jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Our config - base files
ADD etc/cassandra/cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD etc/cassandra/cassandra.yaml /etc/cassandra/cassandra.yaml
ADD etc/cassandra/cassandra-rackdc.properties /etc/cassandra/cassandra-rackdc.properties

# Entry point
ADD entrypoint.py /entrypoint.py
RUN chmod ugo+x /entrypoint.py
ENTRYPOINT ["/entrypoint.py"]

# Health check - this will work only if env HEALTHCHECK_ENABLE is set to some other value than "0"
HEALTHCHECK --start-period=30m --retries=3 CMD ["/entrypoint.py", "healthcheck"]

# Exports

## Volumes - data and commit log and logs
VOLUME /var/lib/cassandra /var/lib/cassandra/commitlog /var/log/cassandra

## JMX exporter port
EXPOSE 7199

## Native transport
EXPOSE 9042

## Internode
EXPOSE 7000

## MX4J
EXPOSE 8001

# Defaults - these are used to alter cassandra.yaml before start
ENV LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    SEED_NODES=auto

