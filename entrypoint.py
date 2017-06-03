# coding=UTF-8
import os

CFG_FILE = '/etc/cassandra/cassandra.yaml'
SUBST_WITH_ENVS = [
    'BROADCAST_ADDRESS', 'LISTEN_ADDRESS', 'RPC_ADDRESS', 'RPC_BROADCAST_ADDRESS',
    'CLUSTER_NAME', 'SEED_NODES', 'STREAMING_SOCKET_TIMEOUT_IN_MS'
]


if __name__ == '__main__':
    # modify cassandra.yaml
    with open(CFG_FILE, 'rb') as fin:
        data = fin.read()

    for k in SUBST_WITH_ENVS:
        data = data.replace(os.environ[k])

    with open(CFG_FILE, 'wb') as fout:
        fout.write(data)

    # Run Cassandra proper
    os.execl("/usr/sbin/cassandra", ["/usr/sbin/cassandra", "-f"])

