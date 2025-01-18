#!/bin/bash
export BOT_NAME="Poker calculator bot"
export BOT_NICKNAME="pokerclc_bot"
export BOT_TOKEN="6602349464:AAHXWR1pnkqmfUj0YRiY1Jy7EmfyMQToYO8"
export DB_PASSWORD="r8E6&r3>"
export DB_URL="jdbc:postgresql://localhost:5432/pokerclock?sslmode=disable"
export DB_USER="psuser"

java --enable-preview -Xmx512m -Djava.net.preferIPv6Addresses=true -jar ~/app/telegram-*.jar