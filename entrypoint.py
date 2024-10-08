#!/usr/bin/python3
"""
Copyright (c) 2019-2024 SMOK sp. z o. o.
See LICENSE.md for details
Author: Piotr Maślanka <pmaslanka@smok.co>
"""
from __future__ import division

import logging
import os
import socket
import subprocess
import stat
import sys

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

CFG_FILE = '/etc/cassandra/cassandra.yaml'
CFG_RACK_FILE = '/etc/cassandra/cassandra-rackdc.properties'
CFG_ENV_FILE = '/etc/cassandra/cassandra-env.sh'
SUBST_WITH_ENVS = set([
    'BROADCAST_ADDRESS', 'LISTEN_ADDRESS', 'RPC_ADDRESS', 'RPC_BROADCAST_ADDRESS',
    'SEED_NODES',
])

if __name__ == '__main__':
    # Try to read environment variables from ${JAVA_HOME}/release
    with open(os.path.join(os.environ['JAVA_HOME'], 'release'), 'r') as fin:
        for line in fin:
            key, value_quoted = line.split('=', 1)
            os.environ[key] = value_quoted.strip('"')

    if 'ADDRESS_FOR_ALL' in os.environ:
        logger.warning('ADDRESS_FOR_ALL set, substituting all addresses for this one')

        addr = os.environ['ADDRESS_FOR_ALL']
        os.environ['SEED_NODES'] = addr
        os.environ['RPC_ADDRESS'] = addr
        os.environ['RPC_BROADCAST_ADDRESS'] = addr
        os.environ['BROADCAST_ADDRESS'] = addr
        os.environ['LISTEN_ADDRESS'] = addr

    # Replace the "auto" keyword with current IP address
    for k in SUBST_WITH_ENVS:
        if os.environ.get(k, '').upper() == 'AUTO':
            os.environ[k] = socket.gethostbyname(socket.gethostname())


    def setdefault(**kwargs):
        for key, value in kwargs.items():
            SUBST_WITH_ENVS.add(key)
            os.environ.setdefault(key, value)


    # define sane defaults
    setdefault(HEALTHCHECK_ENABLE='0',
               HEAP_NEWSIZE='180M',
               BATCH_SIZE_FAIL_THRESHOLD_IN_KB='50',
               CLUSTER_NAME='Test Cluster',
               NUM_TOKENS='256',
               CASSANDRA_HOME='/usr/share/cassandra',
               CASSANDRA_DC='dc1',
               MAX_HEAP_SIZE='48G',
               PARTITIONER='org.apache.cassandra.dht.Murmur3Partitioner',
               ROW_CACHE_SIZE_IN_MB='0',
               CASSANDRA_RACK='rack1',
               AUTHORIZER='AllowAllAuthorizer',
               ENDPOINT_SNITCH='SimpleSnitch',
               DISK_OPTIMIZATION_STRATEGY='ssd',
               KEY_CACHE_SIZE_IN_MB='',
               STREAM_THROUGHPUT_OUTBOUND_MEGABITS_PER_SEC='25',
               FILE_CACHE_SIZE_IN_MB='512',
               AUTHENTICATOR='AllowAllAuthenticator',
               HINTEDHANDOFFENABLE='true',
               AUTOBOOTSTRAP='true',
               JMX_AUTH='yes',
               BATCHLOG_REPLAY_THROTTLE='524288',           # 512 MBit should suffice
               TOMBSTONE_WARN_THRESHOLD='10000',
               TOMBSTONE_FAIL_THRESHOLD='100000',
               COMMITLOG_TOTAL_SPACE_IN_MB='4096',
               READ_REQUEST_TIMEOUT_IN_MS='5000',
               RANGE_REQUEST_TIMEOUT_IN_MS='10000',
               WRITE_REQUEST_TIMEOUT_IN_MS='10000',
               COUNTER_WRITE_REQUEST_TIMEOUT_IN_MS='5000',
               CAS_CONTENTION_TIMEOUT_IS_MS='2000',
               TRUNCATE_REQUEST_TIMEOUT_IN_MS='60000',
               REQUEST_TIMEOUT_IN_MS='15000',
               COMPACTION_THROUGHPUT_MB_PER_SEC='64',
               COMPACTION_LARGE_PARTITION_WARNING_THRESHOLD_MB='100',
               MAX_HINT_WINDOW_IN_MS='10800000',    # 3 hours
               COLUMN_INDEX_SIZE_IN_KB='64',
               REQUEST_SCHEDULER='org.apache.cassandra.scheduler.NoScheduler',
               ENABLE_SCRIPTED_USER_DEFINED_FUNCTIONS='true',
               ENABLE_USER_DEFINED_FUNCTIONS='true',
               COMMITLOG_SEGMENT_SIZE='32',
               COMMITLOG_SYNC='periodic',
               MEMTABLE_HEAP_SIZE_IN_MB='1024MB')

    if os.environ['DISK_OPTIMIZATION_STRATEGY'] not in ('ssd', 'spinning'):
        print('Invalid DISK_OPTIMIZATION_STRATEGY valid options are ssd and spinning')
        sys.exit(1)

    # Calculate commitlog total space in MB, as to quote cassandra.yaml:
    # The default value is the smaller of 8192, and 1/4 of the total space
    # of the commitlog volume.
    try:
        commitlog = os.statvfs('/var/lib/cassandra/commitlog')
    except OSError:
        commitlog = os.statvfs('/')

    free_space_in_mb = min(8192, commitlog.f_frsize * commitlog.f_blocks // 1024 // 1024 // 4)
    setdefault(COMMITLOG_TOTAL_SPACE_IN_MB=str(free_space_in_mb))

    # Do the optional health check
    if len(sys.argv) > 1:
        if sys.argv[1] == 'healthcheck':
            if os.environ.get('HEALTHCHECK_ENABLE', '0') == '0':
                sys.exit(0)

            if os.environ.get('LOCAL_JMX', 'yes') == 'no':
                try:
                    subprocess.check_call(['nodetool', 'status', '-u', 'monitorRole', '-p',
                                           os.environ['JMX_REMOTE_PASSWORD']])
                except subprocess.CalledProcessError:
                    sys.exit(1)
                else:
                    sys.exit(0)
            else:
                try:
                    subprocess.check_call(['nodetool', 'status'])
                except subprocess.CalledProcessError:
                    sys.exit(1)
                else:
                    sys.exit(0)
        elif sys.argv[1] == 'bash':
            os.execv('/bin/bash', ['/bin/bash'])


    # modify cassandra.yaml
    with open(CFG_FILE, 'rb') as fin:
        data = fin.read()
    for k in SUBST_WITH_ENVS:
        data = data.replace(b'$' + k.encode('utf-8'), os.environ[k].encode('utf-8'))

    with open(CFG_FILE, 'wb') as f_out:
        f_out.write(data)

    log_gc = os.environ.get('LOG_GC', 'none')
    if log_gc == 'file':
        with open('/etc/cassandra/jvm.options.log_gc.file', 'r') as f_in:
            data2 = f_in.read()
    elif log_gc == 'stdout':
        with open('/etc/cassandra/jvm.options.log_gc.stdout', 'r') as f_in:
            data2 = f_in.read()
    else:
        data2 = ''

    with open('/etc/cassandra/jvm.options', 'a') as f_out:
        f_out.write(data2)

    # modify cassandra-env.sh
    with open(CFG_ENV_FILE, 'rb') as fin:
        data = fin.read()
        i = 1
        extras = []
        while ('EXTRA%s' % (i,)) in os.environ:
            extras.append('JVM_OPTS="$JVM_OPTS %s"\n' % (os.environ['EXTRA%s' % (i,)],))
            i += 1
        extras.append(f'JVM_OPTS="$JVM_OPTS -Xms{os.environ["NEW_HEAP_SIZE"]} -Xmx{os.environ["MAX_HEAP_SIZE"]}"\n')
        data = data.replace(b'$$$EXTRA_ARGS', ''.join(extras).encode('utf-8'))

    if os.environ.get('LOCAL_JMX', 'yes') == 'no':
        with open('/etc/cassandra/jmxremote.password', 'w') as f_out:
            f_out.write('controlRole ' + os.environ['JMX_REMOTE_PASSWORD'] + '\n')
            f_out.write('monitorRole ' + os.environ['JMX_REMOTE_PASSWORD'] + '\n')
        os.chmod('/etc/cassandra/jmxremote.password', stat.S_IRUSR)

    if 'DISABLE_PROMETHEUS_EXPORTER' in os.environ:
        data = data.split('\n')
        newdata = []
        for line in data:
            if 'prometheus_javaagent' not in line:
                newdata.append(line)
        data = '\n'.join(newdata).encode('utf-8')

    with open(CFG_ENV_FILE, 'wb') as f_out:
        f_out.write(data)

    # modify cassandra-rackdc.properties
    with open(CFG_RACK_FILE, 'rb') as fin:
        data = fin.read()
    data = data.replace(b'dc=dc1', ('dc=' + os.environ['CASSANDRA_DC']).encode('utf-8'))
    data = data.replace(b'rack=rack1', ('rack=' + os.environ['CASSANDRA_RACK']).encode('utf-8'))
    with open(CFG_RACK_FILE, 'wb') as f_out:
        f_out.write(data)

    # Run Cassandra proper
    os.execv("/usr/sbin/cassandra", ["/usr/sbin/cassandra", "-f", '-R'] + sys.argv[1:])
