#!/bin/bash

mvn clean install -Dmaven.test.skip=true

cd target
scp springBoot-0.0.1-SNAPSHOT.jar root@49.235.178.21:/data/java

