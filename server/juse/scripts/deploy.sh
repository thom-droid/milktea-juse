#!/usr/bin/bash

REPOSITORY=/home/ec2-user/codedeploy

{
  echo "> ==================================================
  > current time: $(date)"
} >> $REPOSITORY/log/deploy.log

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

CURRENT_JAVA_PIDS=$(pgrep -fl java | awk '{print $1}')
CURRENT_DHCLIENT_PIDS=$(pgrep -fl juse | awk '{print $1}')

echo "> current application pids: ${CURRENT_JAVA_PIDS[*]}" >> $REPOSITORY/log/deploy.log

if [ -z "$CURRENT_JAVA_PIDS" ]; then
  echo "> no application is running. new application is being prepared to launch."
else
  for PID in ${CURRENT_JAVA_PIDS[*]}
  do
    echo "> kill -15 $PID"
    kill -15 "$PID"
    sleep 5
  done
fi

echo "> kill any dhclient with juse."

if [ -z "$CURRENT_DHCLIENT_PIDS" ]; then
  echo "> no dhclient is running. "
else
  for PID in ${CURRENT_DHCLIENT_PIDS[*]}
  do
    echo "> kill -15 $PID"
    kill -15 "$PID"
    sleep 5
  done
fi

echo "> checking new application"

#JAR_NAME=$(ls -tr $REPOSITORY/*.jar | grep -E '.*SNAPSHOT\.jar$')
JAR_NAME=$(ls -tr $REPOSITORY/'.*SNAPSHOT\.jar$');

echo "> jar name: $JAR_NAME" >> $REPOSITORY/log/deploy.log

echo "> grant permission to jar" >> $REPOSITORY/log/deploy.log

chmod +x "$JAR_NAME"

echo "> running $JAR_NAME ..." >> $REPOSITORY/log/deploy.log

nohup java -jar \
-Dspring.profiles.active=prod \
-Dspring.config.import=file:///home/ec2-user/app/config/ \
-Dserver.port=80 \
"$JAR_NAME" \
> /home/ec2-user/app/tmp/log/nohup.out 2>&1 &

