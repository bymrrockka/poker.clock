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
/game 
```
___

### 4. Message

&rarr; <ins>Bot</ins>
``` 
message id: 4 
What type of game you'd like to play? 
``` 
___

### 5. Message

&rarr; <ins>User</ins>

```
Processed message id: 5
Tournament 
```
___

### 6. Message

&rarr; <ins>Bot</ins>
``` 
message id: 6 
How much is for buy in? 
``` 
___

### 7. Message

&rarr; <ins>User</ins>

```
Processed message id: 7
30 
```
___

### 8. Message

&rarr; <ins>Bot</ins>
``` 
message id: 8 
Who's playing? 
``` 
___

### 9. Message

&rarr; <ins>User</ins>

```
Processed message id: 9
@nickname1 
```
___

### 10. Message

&rarr; <ins>Bot</ins>
``` 
message id: 10 
Game type: TOURNAMENT
Buy in: 30
                
------------------------------
Table 1
Seats:
  5. @nickname1
                                 
``` 
___

### 11. Pinned

``` 
message id 10 pinned
``` 
___

### 12. Deleted messages

``` 
message ids 4,5,6,7,8,9 deleted
``` 
___

### 13. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 10]
message id: 11
/stop_poll 
```
___

### 14. Message

&rarr; <ins>Bot</ins>
``` 
message id: 12 
Poll was not found 
``` 
___