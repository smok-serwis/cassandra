# coding=UTF-8

import os, sys


SUBST_WITH_ENVS = [
    'BROADCAST_ADDRESS',
    'LISTEN_ADDRESS',
    'RPC_ADDRESS',
    'RPC_BROADCAST_ADDRESS',
    'CLUSTER_NAME',
    'SEED_NODES',
    'STREAMING_SOCKET_TIMEOUT_IN_MS'
]


def replace(path, q):
    with open(path, 'rb') as fin:
        data = fin.read()

    for k in q:
        data = data.replace(os.environ[k])

    with open(path, 'wb') as fout:
        fout.write(data)


if __name__ == '__main__':

    replace('/etc/cassandra/cassandra.yaml', SUBST_WITH_ENVS)
