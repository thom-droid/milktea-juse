#!/usr/bin/bash

REPOSITORY=/home/ec2-user/codedeploy

{
  echo "> ==================================================
  > current time: $(date)"
} >> $REPOSITORY/log/deploy.log

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

CURRENT_JAVA_PIDS=$(pgrep -fl java | awk '{print $1}')

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

echo "> checking new application"

#JAR_NAME=$(ls -tr $REPOSITORY/juse*-SNAPSHOT.jar | head -1);
JAR_NAME=$(find $REPOSITORY -name "juse*-SNAPSHOT.jar" -print -quit);

{
  echo "> jar name: $JAR_NAME
  > grant permission to jar and run"
} >> $REPOSITORY/log/deploy.log

chmod +x "$JAR_NAME"

echo "> running $JAR_NAME ..." >> $REPOSITORY/log/deploy.log

nohup java -jar \
-Dspring.profiles.active=prod \
-Dspring.config.import=file:///home/ec2-user/app/config/ \
-Dserver.port=80 \
"$JAR_NAME" \
> /home/ec2-user/app/tmp/log/nohup.out 2>&1 &

