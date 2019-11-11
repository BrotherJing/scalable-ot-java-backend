#!/usr/bin/env bash

docker run -d -p 27017-27019:27017-27019 --name mongodb --rm mongo
