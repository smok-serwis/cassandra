FROM debian:stretch


ENV DEBIAN_FRONTEND=noninteractive
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1

RUN apt-get update && \
    apt-get install -y --no-install-recommends gnupg apt-transport-https dirmngr debconf-utils apt-utils ca-certificates wget && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886 && \
    apt-get clean


ADD java.sources.list /etc/apt/sources.list.d/cassandra.sources.list
RUN wget -O - http://debian.opennms.org/OPENNMS-GPG-KEY | apt-key add - && \
    apt-get update && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections && \
    apt-get install -y oracle-java8-installer

# Install jemalloc
RUN apt-get update && \
    apt-get install -y --no-install-recommends libjemalloc1 && \
    apt-get clean

LABEL apache.cassandra.version="3.11.6"


# Install python-support that dsc30 whine about not having

ADD python-support_1.0.15_all.deb /tmp/python-support_1.0.15_all.deb
RUN apt-get update && \
    apt-get install -y python>=2.5 && \
    dpkg -i /tmp/python-support_1.0.15_all.deb && \
    apt-get clean

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

# Our config - base files
ADD etc/cassandra/cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD etc/cassandra/jmxremote.access /etc/cassandra/jmxremote.access
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

## Prometheus exporter port
EXPOSE 7198

## Jolokia exporter port
EXPOSE 8080

## Native transport
EXPOSE 9042

## Internode
EXPOSE 7000

# Defaults - these are used to alter cassandra.yaml before start
ENV LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    SEED_NODES=auto

