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
Processed [reply to message id 3]
message id: 4
/tournament_game
buyin: 10

@terisa_johnston 
```
___

### 10. Message

&rarr; <ins>Bot</ins>
``` 
message id: 5 
Tournament game started.
------------------------------
Table 1
Seats:
  5. @jackie_rau
                                 
``` 
___

### 11. Message

&rarr; <ins>User</ins>

```
Processed message id: 6
/game_stats 
```
___

### 12. Message

&rarr; <ins>Bot</ins>
``` 
message id: 7 
Tournament game statistics:
  - players entered -> 1
  - number of entries -> 1
  - total in game -> 10.00 
``` 
___