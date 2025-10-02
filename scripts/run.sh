#!/bin/bash

stopApp() {
  processId=$(ps aux | grep java | grep telegram-bot | awk '{print $2}')
  if [ -z "${processId}" ]; then
    echo "Telegram bot wasn't started"
    return 0
  else
    echo "Processid is $processId"
    kill "$processId";
  fi

  counter=1
  until [ $counter -gt 150 ]
      do
          if ps -p "$processId" > /dev/null; then
              echo "Waiting for the process($processId) to finish on it's own for $(( 300 - $(( $counter*5)) ))seconds..."
              sleep 2s
              ((counter++))
          else
              echo "Telegram bot with PROCESS_ID:$processId is stopped now.."
              return 0
          fi
  done
}

startApp() {
  fileSize=$(($(find ~/app/*.jar -type f | wc -l)-1))

  if [ "$fileSize" -gt 0 ]; then
    find ~/app/*.jar -type f -printf "%Cx.%CX %p\n" | sort -n | awk '{print $2}' | head -$fileSize | xargs rm
  fi
  commandOpts="-Xmx512m -Djava.net.preferIPv6Addresses=true"

  `nohup java $commandOpts -jar ~/app/*.jar </dev/null >/dev/null 2>&1 &` echo "Telegram bot started"
}

stopApp
source ~/app/scripts/dbtools.sh
startApp

