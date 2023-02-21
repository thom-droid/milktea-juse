#!/usr/bin/bash

REPOSITORY=/home/ec2-user/codedeploy
PROJECT_NAME=juse

echo "> current time: $(date)" >> $REPOSITORY/log/deploy.log

echo "> copy built file.."

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

echo "> current application pid"

CURRENT_PIDS=$(pgrep -fl juse | grep jar | awk '{print $1}')

echo "> current application pids: ${CURRENT_PIDS[*]}"

if [ -z "$CURRENT_PIDS" ]; then
  echo "> no application is running."
else
  for PID in "${CURRENT_PIDS[@]}"
  do
    echo "> kill -15 $PID"
    kill -15 "$PID"
    sleep 5
  done
fi

echo "> checking new application"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | grep -E '.*SNAPSHOT\.jar$')

#JAR_NAME=$(ls -tr $REPOSITORY/ | grep SNAPSHOT.jar | tail -n 1)

echo "> jar name: $JAR_NAME"

echo "> grant permission to jar"

chmod +x "$JAR_NAME"

echo "> running $JAR_NAME ..."

nohup java -jar \
-Dspring.profiles.active=prod \
-Dserver.port=80 $REPOSITORY/"$JAR_NAME" > /home/ec2-user/app/tmp/log/nohup.out 2>&1 &

