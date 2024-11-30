#!/bin/bash

if [ -z "${HEAP_NEWSIZE}" ]; then
  export HEAP_NEWSIZE=100M
fi

exec /usr/sbin/_nodetool "$@"
