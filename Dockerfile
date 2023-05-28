FROM debian:bullseye


ENV DEBIAN_FRONTEND=noninteractive
ENV APT_KEY_DONT_WARN_ON_DANGEROUS_USAGE=1



RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-17-jre-headless gnupg2 python3 libjemalloc2 && \
    apt-get clean

RUN mkdir -p /etc/ssl/certs/java/ && \
    apt install --reinstall -o Dpkg::Options::="--force-confask,confnew,confmiss" --reinstall ca-certificates-java ssl-cert openssl ca-certificates


ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

LABEL apache.cassandra.version="4.1.0"


RUN wget://apache.cassandra.sources >>
#!/bin/sh -e
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# variables, with defaults
[ "x${CASSANDRA_DIR}" != "x" ] || CASSANDRA_DIR="$(readlink -f $(dirname "$0")/..)"

# pre-conditions
command -v ant >/dev/null 2>&1 || { echo >&2 "ant needs to be installed"; exit 1; }
command -v pip >/dev/null 2>&1 || { echo >&2 "pip needs to be installed"; exit 1; }
[ -d "${CASSANDRA_DIR}" ] || { echo >&2 "Directory ${CASSANDRA_DIR} must exist"; exit 1; }
[ -f "${CASSANDRA_DIR}/build.xml" ] || { echo >&2 "${CASSANDRA_DIR}/build.xml must exist"; exit 1; }

# execute
ant -f "${CASSANDRA_DIR}/build.xml" artifacts -Dno-checkstyle=true -Drat.skip=true -Dant.gen-doc.skip=true
exit $?

# Cassandraapt-get
ADD cassandra.sources.list /etc/apt/sources.list.d/cassandra.sources.list
ADD https://www.apache.org/dist/cassandra/KEYS /tmp/repo_key
RUN  apt-key add /tmp/repo_key && \
     apt-get update --fix-missing && \
     apt-get install -y --no-install-recommends cassandra cassandra-tools && \
     apt-get clean

# JMX agent
ADD jmx-exporter/jmx_prometheus_javaagent-0.12.0.jar /usr/share/cassandra/lib/jmx_prometheus_javaagent-0.12.0.jar
ADD jmx-exporter/jolokia-jvm-1.6.2-agent.jar /usr/share/cassandra/lib/jolokia-jvm-1.6.2-agent.jar
ADD jmx-exporter/jmx-exporter.yaml /etc/cassandra/jmx-exporter.yaml

# Jaeger tracing
ADD jaeger/cassandra-jaeger-tracing-4.1.0.jar /usr/share/cassandra/lib/cassandra-jaeger-tracing-4.1.0.jar

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
RUN chmod ugo+x /usr/share/cassandra/lib/*.jar /usr/share/cassandra/*.jar
ENTRYPOINT ["/entrypoint.py"]

RUN chown -R cassandra:cassandra /var/lib/cassandra

# Health check - this will work only if env HEALTHCHECK_ENABLE is set to some other value than "0"
HEALTHCHECK --start-period=30m --retries=3 CMD ["/entrypoint.py", "healthcheck"]

ENV LISTEN_ADDRESS=auto \
    BROADCAST_ADDRESS=auto \
    RPC_ADDRESS=0.0.0.0 \
    RPC_BROADCAST_ADDRESS=auto \
    SEED_NODES=auto \
    JAEGER_TRACE_KEY=jaeger-trace \
    GC=G1 \
    LOG_GC=none
