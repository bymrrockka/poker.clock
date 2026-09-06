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
Processed message id: 4
/game 
```
___

### 11. Message

&rarr; <ins>Bot</ins>
``` 
message id: 5 
What type of game you'd like to play? 
``` 
___

### 12. Message

&rarr; <ins>User</ins>

```
Processed message id: 6
Tournament 
```
___

### 13. Message

&rarr; <ins>Bot</ins>
``` 
message id: 7 
How much is for buy in? 
``` 
___

### 14. Message

&rarr; <ins>User</ins>

```
Processed message id: 8
10 
```
___

### 15. Message

&rarr; <ins>Bot</ins>
``` 
message id: 9 
Who's playing? 
``` 
___

### 16. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 3]
message id: 10
 
```
___

### 17. Message

&rarr; <ins>Bot</ins>
``` 
message id: 11 
Game type: TOURNAMENT
Buy in: 10
                
------------------------------
Table 1
Seats:
  3. @jackie_rau
  7. @terisa_johnston
                                 
``` 
___

### 18. Pinned

``` 
message id 11 pinned
``` 
___

### 19. Deleted messages

``` 
message ids 5,6,7,8,9,10 deleted
``` 
___

### 20. Message

&rarr; <ins>User</ins>

```
Processed message id: 12
/game_stats 
```
___

### 21. Message

&rarr; <ins>Bot</ins>
``` 
message id: 13 
Tournament game statistics:
  - players entered -> 2
  - number of entries -> 2
  - total in game -> 20.00 
``` 
___

### 22. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 1]
message id: 14
/stop_poll 
```
___

### 23. Message

&rarr; <ins>Bot</ins>
``` 
message id: 15 
Poll stopped 
``` 
___