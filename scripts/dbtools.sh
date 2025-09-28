#!/bin/bash

dbBackup(){
#  keep not more then 3 backups
  pg_dump -U psuser -d pokerclock -F tar -f "$HOME/backup/pokerclock.$(date '+%d-%m-%Y').tar"

  fileSize=$(find ~/backup/ -type f | wc -l)

  find ~/backup/ -type f -printf "%Cx.%CX %p\n" | sort -n | awk '{print $3}' | head -"$($fileSize-3)" | xargs -0 rm
}

dbBackup