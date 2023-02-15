#!/usr/bin/env bash
REPOSITORY=/home/ec2-user/codedeploy/build
JAR_NAME=$(ls -tr $REPOSITORY/ | grep SNAPSHOT.jar | tail -n 1)
nohup sudo java -jar -Dspring.config.import=file:/home/ec2-user/app/config/ \
-Dspring.profiles.active=prod -Dserver.port=80 $REPOSITORY/$JAR_NAME > /home/ec2-user/app/tmp/log/nohup.out  2>&1 &
