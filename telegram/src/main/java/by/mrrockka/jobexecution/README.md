# Scheduled service

## Description:

Service performs automatic cron operation on specific events that could be configured using core API.

## Motivation:

Poker tournaments usually have blinds changing during the time, so this service is about to cover this use case.
For each started tournament game there should be separate job to execute to send related blinds changes.

Another use case is to send weekly pools to chats to gather info about who gonna join

## Note:

This package could be moved to separate submodule and treated as one of microservices