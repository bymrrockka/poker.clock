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

### 4. Pinned

``` 
message id 3 pinned
``` 
___

### 5. Poll answer

``` 
jackie_rau chosen 1
``` 
___

### 6. Poll answer

``` 
terisa_johnston chosen 1
``` 
___

### 7. Poll answer

``` 
santo_welch chosen 2
``` 
___

### 8. Poll answer

``` 
ashlee_lang chosen 2
``` 
___

### 9. Poll answer

``` 
stanton_boyer chosen 3
``` 
___

### 10. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 3]
message id: 4
/tournament_game
buyin: 10

 
```
___

### 11. Message

&rarr; <ins>Bot</ins>
``` 
message id: 5 
Tournament game started.
------------------------------
Table 1
Seats:
  3. @jackie_rau
  7. @terisa_johnston
                                 
``` 
___

### 12. Message

&rarr; <ins>User</ins>

```
Processed message id: 6
/game_stats 
```
___

### 13. Message

&rarr; <ins>Bot</ins>
``` 
message id: 7 
Tournament game statistics:
  - players entered -> 2
  - number of entries -> 2
  - total in game -> 20.00 
``` 
___

### 14. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 1]
message id: 8
/stop_poll 
```
___

### 15. Message

&rarr; <ins>Bot</ins>
``` 
message id: 9 
Poll stopped 
``` 
___