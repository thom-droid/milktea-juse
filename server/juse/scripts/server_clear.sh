#!/usr/bin/env bash
PROJECT_NAME=juse
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)
echo ">current running application :  + $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> no other running application.. keep the application running.."
else
        echo "> kill -15 $CURRENT_PID"
        sudo kill -15 "$CURRENT_PID"
        sleep 5
fi

rm -rf /home/ec2-user/codedeploy/build