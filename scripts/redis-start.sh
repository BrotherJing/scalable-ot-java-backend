#!/usr/bin/env bash

REDIS_HOME=/home/jing/.local/redis-5.0.5

pushd ${REDIS_HOME}

src/redis-server &

# Or
# docker run -d --name redis -p 6379:6379 redis

popd
