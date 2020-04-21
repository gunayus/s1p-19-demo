#!/usr/bin/env bash
docker rm -f $(docker ps -a -q)

docker rmi $(sudo docker images | grep "^<none>" | awk "{print $3}")

docker images
