### 1. Message

&rarr; <ins>User</ins>

```
Processed message id: 1
/create_poll
cron: 0 0 0 * * WED
message: Test poll
options: 
1. Yes - participant
2. No
3. I don't know 
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

### 3. Posted

&rarr; <ins>2025-09-24 - WEDNESDAY</ins>

``` 
message id 3
Test poll
1. 'Yes'
2. 'No'
3. 'I don't know'
``` 
___

### 4. Poll answer

``` 
jackie_rau chosen 1
``` 
___

### 5. Poll answer

``` 
terisa_johnston chosen 1
``` 
___

### 6. Poll answer

``` 
santo_welch chosen 2
``` 
___

### 7. Poll answer

``` 
ashlee_lang chosen 2
``` 
___

### 8. Poll answer

``` 
stanton_boyer chosen 3
``` 
___

### 9. Message

&rarr; <ins>User</ins>

```
Processed message id: 4
/game 
```
___

### 10. Message

&rarr; <ins>Bot</ins>
``` 
message id: 5 
What type of game you'd like to play? 
``` 
___

### 11. Message

&rarr; <ins>User</ins>

```
Processed message id: 6
Tournament 
```
___

### 12. Message

&rarr; <ins>Bot</ins>
``` 
message id: 7 
How much is for buy in? 
``` 
___

### 13. Message

&rarr; <ins>User</ins>

```
Processed message id: 8
10 
```
___

### 14. Message

&rarr; <ins>Bot</ins>
``` 
message id: 9 
Who's playing? 
``` 
___

### 15. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 3]
message id: 10
@terisa_johnston 
```
___

### 16. Message

&rarr; <ins>Bot</ins>
``` 
message id: 11 
Game type: TOURNAMENT
Buy in: 10
                
------------------------------
Table 1
Seats:
  5. @jackie_rau
                                 
``` 
___

### 17. Pinned

``` 
message id 11 pinned
``` 
___

### 18. Deleted messages

``` 
message ids 5,6,7,8,9,10 deleted
``` 
___

### 19. Message

&rarr; <ins>User</ins>

```
Processed message id: 12
/game_stats 
```
___

### 20. Message

&rarr; <ins>Bot</ins>
``` 
message id: 13 
Tournament game statistics:
  - players entered -> 1
  - number of entries -> 1
  - total in game -> 10.00 
``` 
___