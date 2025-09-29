#!/bin/bash

dbBackup(){
#  keep not more then 3 backups
  pg_dump -U psuser -d pokerclock -F tar -f "$HOME/backup/pokerclock.$(date '+%d-%m-%Y').tar"
  fileSize=$(($(find ~/backup/ -type f | wc -l)-3))

  if [ "$fileSize" -gt 0 ]; then
    echo "$fileSize old backups removed"
    find ~/backup/ -type f -printf "%Cx.%CX %p\n" | sort -n | awk '{print $3}' | head -$fileSize | xargs -0 rm
  else
    echo "Backups contain less than 3 files"
  fi
}

dbBackup