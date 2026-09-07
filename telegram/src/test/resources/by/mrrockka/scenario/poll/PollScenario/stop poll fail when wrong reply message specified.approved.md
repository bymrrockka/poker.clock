### 1. Message

&rarr; <ins>User</ins>

```
/create_poll
cron: 0 0 0 * * 3
message: Test poll
options: 
1. Yes - participant 
```
___

### 2. Message

&larr; <ins>Bot</ins>
``` 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 3. Message

&rarr; <ins>User</ins>

```
/game 
```
___

### 4. Message

&larr; <ins>Bot</ins>
``` 
What type of game you'd like to play? 
``` 
___

### 5. Message

&rarr; <ins>User</ins>

```
Tournament 
```
___

### 6. Message

&larr; <ins>Bot</ins>
``` 
How much is for buy in? 
``` 
___

### 7. Message

&rarr; <ins>User</ins>

```
30 
```
___

### 8. Message

&larr; <ins>Bot</ins>
``` 
Who's playing? 
``` 
___

### 9. Message

&rarr; <ins>User</ins>

```
@nickname1 
```
___

### 10. Message

&larr; <ins>Bot</ins>
``` 
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
[reply to message id 10]
/stop_poll 
```
___

### 14. Message

&larr; <ins>Bot</ins>
``` 
Poll was not found 
``` 
___