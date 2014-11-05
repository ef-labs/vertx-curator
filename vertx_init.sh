#!/bin/sh

DIRNAME=$(dirname $0)
cd ${DIRNAME}
DIRNAME=${PWD}
pwd

# Run init in each module to create vertx_classpath.txt and module.link files

cd ${DIRNAME}/vertx-mod-zookeeper
mvn vertx:init
