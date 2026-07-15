#!/usr/bin/env bash
set -Eeuo pipefail

APP_NAME="telegram-bot"
APP_DIR="${APP_DIR:-$HOME/app}"
SCRIPTS_DIR="$APP_DIR/scripts"
PID_FILE="$APP_DIR/logs/$APP_NAME.pid"
LOG_FILE="$APP_DIR/logs/$APP_NAME.log"
JAVA_OPTS="${JAVA_OPTS:--Xmx512m -Djava.net.preferIPv6Addresses=true}"

findLatestJar() {
  find "$APP_DIR" -maxdepth 1 -type f -name "*.jar" -printf "%T@ %p\n" \
    | sort -nr \
    | awk 'NR == 1 { $1=""; sub(/^ /, ""); print; exit }'
}

removeOldJars() {
  mapfile -t jars < <(
    find "$APP_DIR" -maxdepth 1 -type f -name "*.jar" -printf "%T@ %p\n" \
      | sort -nr \
      | awk '{ $1=""; sub(/^ /, ""); print }'
  )

  if [ "${#jars[@]}" -le 1 ]; then
    return 0
  fi

  for jar in "${jars[@]:1}"; do
    rm -f "$jar"
  done
}

loadDbTools() {
  if [ -f "$SCRIPTS_DIR/dbtools.sh" ]; then
    # shellcheck disable=SC1091
    source "$SCRIPTS_DIR/dbtools.sh"
  fi
}

getProcessId() {
  if [ -f "$PID_FILE" ]; then
    local pid
    pid="$(cat "$PID_FILE")"

    if [ -n "$pid" ] && ps -p "$pid" > /dev/null 2>&1; then
      echo "$pid"
      return 0
    fi
  fi

  pgrep -f "java .*${APP_NAME}.*\.jar" || true
}

isRunning() {
  local processId
  processId="$(getProcessId)"

  [ -n "$processId" ]
}

stopApp() {
  local processId
  processId="$(getProcessId)"

  if [ -z "$processId" ]; then
    echo "Poker Clock app wasn't started"
    rm -f "$PID_FILE"
    return 0
  fi

  echo "Stopping Poker Clock app. Process ID: $processId"

  kill "$processId" 2>/dev/null || true

  for counter in $(seq 1 60); do
    if ps -p "$processId" > /dev/null 2>&1; then
      echo "Waiting for process $processId to stop..."
      sleep 2
    else
      echo "Poker Clock app with PROCESS_ID:$processId is stopped now."
      rm -f "$PID_FILE"
      return 0
    fi
  done

  echo "Process $processId did not stop gracefully. Killing it..."
  kill -9 "$processId" 2>/dev/null || true
  rm -f "$PID_FILE"
}

prepareStart() {
  mkdir -p "$APP_DIR"

  removeOldJars
  loadDbTools
}

startAppBackground() {
  local processId
  processId="$(getProcessId)"

  if [ -z "$processId" ]; then
    echo "Poker Clock app is already running. Process ID: $processId"
    return 0
  fi

  local jarFile
  jarFile="$(findLatestJar)"

  if [ -z "$jarFile" ]; then
    echo "No jar file found in $APP_DIR"
    exit 1
  fi

  echo "Starting Poker Clock app from $jarFile"

  read -r -a javaOptsArray <<< "$JAVA_OPTS"

  nohup "java" "${javaOptsArray[@]}" -jar "$jarFile" >> "$LOG_FILE" 2>&1 &
  echo "$!" > "$PID_FILE"

  echo "Poker Clock app started. Process ID: $(cat "$PID_FILE")"
}

startAppForeground() {
  local jarFile
  jarFile="$(findLatestJar)"

  if [ -z "$jarFile" ]; then
    echo "No jar file found in $APP_DIR"
    exit 1
  fi

  echo "Starting Poker Clock app in foreground from $jarFile"

  read -r -a javaOptsArray <<< "$JAVA_OPTS"

  exec "java" "${javaOptsArray[@]}" -jar "$jarFile"
}

statusApp() {
  local processId
  processId="$(getProcessId)"

  if [ -z "$processId" ]; then
    echo "Poker Clock app is not running"
  else
    echo "Poker Clock app is running. Process ID: $processId"
  fi
}

case "${1:-restart}" in
  start)
    prepareStart
    startAppBackground
    ;;

  start-foreground)
    prepareStart
    startAppForeground
    ;;

  stop)
    stopApp
    ;;

  restart)
    stopApp
    prepareStart
    startAppBackground
    ;;

  status)
    statusApp
    ;;

  *)
    echo "Usage: $0 {start|start-foreground|stop|restart|status}"
    exit 1
    ;;
esac