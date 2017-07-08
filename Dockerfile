

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