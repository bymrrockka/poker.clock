### 1. Message

&rarr; <ins>User</ins>

```
Processed message id: 1
/create_poll
cron: 0 0 0 * * 3
message: Test poll
options: 
1. Yes - participant 
```
___

### 2. Message

&rarr; <ins>Bot</ins>
``` 
message id: 2 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 3. Message

&rarr; <ins>User</ins>

```
Processed message id: 3
/tournament_game
buyin: 30

@me 
```
___

### 4. Message

&rarr; <ins>Bot</ins>
``` 
message id: 4 
Tournament game started.
------------------------------
Table 1
Seats:
  5. @sergio_cartwright
                                 
``` 
___

### 5. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 3]
message id: 5
/stop_poll 
```
___

### 6. Message

&rarr; <ins>Bot</ins>
``` 
message id: 6 
Poll was not found 
``` 
___