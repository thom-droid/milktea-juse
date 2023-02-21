#!/usr/bin/bash

REPOSITORY=/home/ec2-user/codedeploy
PROJECT_NAME=juse

echo "> current time: $(date)" >> $REPOSITORY/log/deploy.log

echo "> copy built file.." >> $REPOSITORY/log/deploy.log

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

echo "> current application pid" >> $REPOSITORY/log/deploy.log

CURRENT_PIDS=$(pgrep -fl juse | grep jar | awk '{print $1}')

echo "> current application pids: ${CURRENT_PIDS[*]}" >> $REPOSITORY/log/deploy.log

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

echo "> checking new application" >> $REPOSITORY/log/deploy.log

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | grep -E '.*SNAPSHOT\.jar$')

#JAR_NAME=$(ls -tr $REPOSITORY/ | grep SNAPSHOT.jar | tail -n 1)

echo "> jar name: $JAR_NAME" >> $REPOSITORY/log/deploy.log

echo "> grant permission to jar" >> $REPOSITORY/log/deploy.log

chmod +x "$JAR_NAME"

echo "> running $JAR_NAME ..." >> $REPOSITORY/log/deploy.log

nohup java -jar \
-Dspring.profiles.active=prod \
-Dserver.port=80 "$JAR_NAME" > /home/ec2-user/app/tmp/log/nohup.out 2>&1 &

