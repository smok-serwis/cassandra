#!/usr/bin/python
# coding=UTF-8
import os
import sys
import socket

CFG_FILE = '/etc/cassandra/cassandra.yaml'
SUBST_WITH_ENVS = [
    'BROADCAST_ADDRESS', 'LISTEN_ADDRESS', 'RPC_ADDRESS', 'RPC_BROADCAST_ADDRESS',
    'CLUSTER_NAME', 'SEED_NODES', 'STREAMING_SOCKET_TIMEOUT_IN_MS', 'NUM_TOKENS',
    'AUTHENTICATOR', 'DISK_OPTIMIZATION_STRATEGY', 'AUTHORIZER', 'ENPOINT_SNITCH',
    'TOMBSTONE_WARN_THRESHOLD', 'TOMBSTONE_FAIL_THRESHOLD'
]

if __name__ == '__main__':
    if 'ADDRESS_FOR_ALL' in os.environ:
        sys.stderr.write('ADDRESS_FOR_ALL set, substituting\n')

        addr = os.environ['ADDRESS_FOR_ALL']
        os.environ['SEED_NODES'] = addr
        os.environ['RPC_ADDRESS'] = addr
        os.environ['RPC_BROADCAST_ADDRESS'] = addr
        os.environ['BROADCAST_ADDRESS'] = addr
        os.environ['LISTEN_ADDRESS'] = addr

    if 'I_ACCEPT_ORACLE_JAVA_LICENSE' not in os.environ:
        sys.stderr.write('No license accepted, no game.\n')
        sys.exit(1)

    # define sane defaults
    os.environ.setdefaults('NUM_TOKENS', '256')
    os.environ.setdefault('CASSANDRA_DC', 'dc1')
    os.environ.setdefault('CASSANDRA_RACK', 'rack1')
    os.environ.setdefault('DISK_OPTIMIZATION_STRATEGY', 'solid')
    os.environ.setdefault('AUTHENTICATOR', 'AllowAllAuthenticator')
    os.environ.setdefault('TOMBSTONE_WARN_THRESHOLD', '1000')
    os.environ.setdefault('TOMBSTONE_FAIL_THRESHOLD', '100000')
    os.environ.setdefault('COMMITLOG_TOTAL_SPACE_IN_MB', '4096')
    os.environ.setdefault('START_RPC', 'false')
    os.environ.setdefault('RPC_PORT', '9160')
    os.environ.setdefault('COLUMN_SIZE_INDEX_IN_KB', '64')
    os.environ.setdefault('REQUEST_SCHEDULER', 'org.apache.cassandra.scheduler.NoScheduler')
    os.environ.setdefault('ENABLE_USER_DEFINED_FUNCTIONS', 'false')

    # "auto"
    for k in SUBST_WITH_ENVS:
        if os.environ.get(k, '').upper() == 'AUTO':
            os.environ[k] = socket.gethostbyname(socket.gethostname())

    # modify cassandra.yaml
    with open(CFG_FILE, 'rb') as fin:
        data = fin.read()
    for k in SUBST_WITH_ENVS:
        data = data.replace('$' + k, os.environ[k])

    if 'ENABLE_INTERHOST_JMX' in os.environ:
        data = data[:data.find('#$$STRIP_JMX_START')] + data[
                                                        data.find('###$STRIP_JMX_STOP') + len('###$STRIP_JMX_STOP'):]

    with open(CFG_FILE, 'wb') as fout:
        fout.write(data)

    i = 1
    extras = []
    while ('EXTRA%s' % (i,)) in os.environ:
        extras.append('JVM_OPTS="$JVM_OPTS %s"\n' % (os.environ['EXTRA%s' % (i,)],))
        i += 1

    # modify cassandra-env.sh
    with open('/etc/cassandra/cassandra-env.sh', 'rb') as fin:
        data = fin.read()
    data = data.replace('$$$EXTRA_ARGS', ''.join(extras))
    with open('/etc/cassandra/cassandra-env.sh', 'wb') as fout:
        fout.write(data)
    with open('/etc/cassandra/cassandra-rackdc.properties', rb) as fin:
        data = fin.read()
    data = data.replace('dc=dc1', 'dc=' + os.environ['CASSANDRA_DC'])
    data = data.replace('rack=rack1', 'rack=' + os.environ['CASSANDRA_RACK'])
    with open('/etc/cassandra/cassandra-rackdc.properties', wb) as fout:
        fout.write(data)
    # Run Cassandra proper
    os.execv("/usr/sbin/cassandra", ["/usr/sbin/cassandra", "-f"] + sys.argv[1:])
