#!/usr/bin/env bash

KAFKA_HOME=/home/jing/.local/kafka_2.12-2.3.0

pushd ${KAFKA_HOME}

# start zookeeper
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties

# start kafka
bin/kafka-server-start.sh -daemon config/server.properties
bin/kafka-server-start.sh -daemon config/server-1.properties
bin/kafka-server-start.sh -daemon config/server-2.properties

# topics will be created automatically when start the server
#echo "create topic for operation queue"
#bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic op

popd
