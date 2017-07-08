# coding=UTF-8
import os, sys, socket

CFG_FILE = '/etc/cassandra/cassandra.yaml'
SUBST_WITH_ENVS = [
    'BROADCAST_ADDRESS', 'LISTEN_ADDRESS', 'RPC_ADDRESS', 'RPC_BROADCAST_ADDRESS',
    'CLUSTER_NAME', 'SEED_NODES', 'STREAMING_SOCKET_TIMEOUT_IN_MS', 'NUM_TOKENS'
]



if __name__ == '__main__':

    if 'ADDRESS_FOR_ALL' in os.environ:
        sys.stderr.write('ADDRESS_FOR_ALL set, substituting')

        addr = os.environ['ADDRESS_FOR_ALL']
        os.environ['SEED_NODES'] = addr
        os.environ['RPC_ADDRESS'] = addr
        os.environ['RPC_BROADCAST_ADDRESS'] = addr
        os.environ['BROADCAST_ADDRESS'] = addr
        os.environ['LISTEN_ADDRESS'] = addr

    if 'I_ACCEPT_ORACLE_JAVA_LICENSE' not in os.environ:
        sys.stderr.write('No license accepted, no game.\n')
        sys.exit(1)

    # "auto"
    for k in SUBST_WITH_ENVS:
        if os.environ.get(k, '').upper() == 'AUTO':
            os.environ[k] = socket.gethostbyname(socket.gethostname())

    # modify cassandra.yaml
    with open(CFG_FILE, 'rb') as fin:
        data = fin.read()
    for k in SUBST_WITH_ENVS:
        data = data.replace('$'+k, os.environ[k])
    with open(CFG_FILE, 'wb') as fout:
        fout.write(data)

    i = 1
    extras = []
    while ('EXTRA%s' % (i, )) in os.environ:
        extras.append('JVM_OPTS="$JVM_OPTS %s"\n' % (os.environ['EXTRA%s' % (i, )], ))
        i += 1

    # modify cassandra-env.sh
    with open('/etc/cassandra/cassandra-env.sh', 'rb') as fin:
        data = fin.read()
    data = data.replace('$$$EXTRA_ARGS', ''.join(extras))
    with open('/etc/cassandra/cassandra-env.sh', 'wb') as fout:
        fout.write(data)


    # Run Cassandra proper
    os.execv("/usr/sbin/cassandra", ["/usr/sbin/cassandra", "-f"])

