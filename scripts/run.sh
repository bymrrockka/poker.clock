#!/bin/bash

stopApp() {
  processId=$(ps ax | grep java | grep telegram-bot | awk {'print $1'})
  echo "Processid is $processId"
  kill "$processId";

  counter=1
  until [ $counter -gt 150 ]
      do
          if ps -p "$processId" > /dev/null; then
              echo "Waiting for the process($processId) to finish on it's own for $(( 300 - $(( $counter*5)) ))seconds..."
              sleep 2s
              ((counter++))
          else
              echo "Telegram bot with PROCESS_ID:$processId is stopped now.."
              exit 0;
          fi
  done
}

startApp() {
  jars=`find ~/app/ -maxdepth 1 -type f -exec stat --format="%w %n" {} + | sort -n | awk {'print $4'}`
  bootfile=${jars[0]}
  unset -v 'jars[0]'
  rm "${jars[@]}"

 `nohup java --enable-preview -Xmx512m -Djava.net.preferIPv6Addresses=true -jar ~/app/$bootfile </dev/null >/dev/null 2>&1 &` echo "Telegram bot started"
}

stopApp
dbtools.sh
startApp

