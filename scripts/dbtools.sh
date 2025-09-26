#!/bin/bash

dbBackup(){
  pg_dump -U psuser -d pokerclock -F tar -f "$HOME/backup/pokerclock.$(date '+%d-%m-%Y').tar"
  files=`find ~/backup/ -type f -exec stat --format="%w %n" {} + | sort -n | awk {'print $4'}`
  until [ ${#files[@]} -gt 3 ]
    do
    rm ${files[0]}
    unset -v 'files[0]'
  done
}

dbBackup