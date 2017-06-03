FROM debian:jessie

# Oracle Java. Accept no substitutes.
ADD webupd8team-java.list /etc/apt/sources.list.d/webupd8team-java.list
RUN echo debconf shared/accepted-oracle-license-v1-1 select true |  debconf-set-selections && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
    apt-get update && \
    apt-get install -y --no-install-recommends debconf-utils curl wget apt-utils oracle-java8-installer ca-certificates && \
    apt-get clean


# Cassandra 3.0
RUN  echo "deb http://debian.datastax.com/community stable main" > /etc/apt/sources.list.d/cassandra.sources.list && \
     curl -L http://debian.datastax.com/debian/repo_key | apt-key add - && \
     apt-get update && \
     apt-get install -y cassandra=3.0.9 dsc30 cassandra-tools ntp && \
     apt-get clean

# JMX agent
RUN cd /usr/share/cassandra/lib && \
    wget https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.9/jmx_prometheus_javaagent-0.9.jar
ADD jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Our config - base files
ADD cassandra-env.sh /etc/cassandra/cassandra-env.sh
ADD cassandra.yaml /etc/cassandra/cassandra.yaml
ADD entrypoint.sh /entrypoint.sh

# Customize config
ADD customizecfg.py /tmp/customizecfg.py
RUN apt-get update && \
    apt-get install -y --no-install-recommends python && \
    python /tmp/customizecfg.py && \
    apt-get purge -y && \
    apt-get clean

# Exports

## Volumes - data and commit log
VOLUME /var/lib/cassandra /var/lib/cassandra/commitlog

## JMX exporter port
EXPOSE 7198

## Native transport
EXPOSE 9042

## Internode
EXPOSE 7000

CMD ["/entrypoint.sh"]
