# coding=UTF-8
import os, sys

CFG_FILE = '/etc/cassandra/cassandra.yaml'
SUBST_WITH_ENVS = [
    'BROADCAST_ADDRESS', 'LISTEN_ADDRESS', 'RPC_ADDRESS', 'RPC_BROADCAST_ADDRESS',
    'CLUSTER_NAME', 'SEED_NODES', 'STREAMING_SOCKET_TIMEOUT_IN_MS'
]


if __name__ == '__main__':

    if 'I_ACCEPT_ORACLE_JAVA_LICENSE' not in os.environ:
        sys.stderr.write('No license accepted, no game.\n')
        sys.exit(1)

    # modify cassandra.yaml
    with open(CFG_FILE, 'rb') as fin:
        data = fin.read()

    for k in SUBST_WITH_ENVS:
        data = data.replace('$'+k, os.environ[k])

    with open(CFG_FILE, 'wb') as fout:
        fout.write(data)

    # NTP client - because time matters
    os.system('service ntp start')

    # Run Cassandra proper
    os.execv("/usr/sbin/cassandra", ["/usr/sbin/cassandra", "-f"])

