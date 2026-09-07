### 1. Message

&rarr; <ins>User</ins>

```
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

&larr; <ins>Bot</ins>
``` 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 3. Posted

&rarr; <ins>2025-09-24 - WEDNESDAY</ins>

``` 

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
/game 
```
___

### 11. Message

&larr; <ins>Bot</ins>
``` 
What type of game you'd like to play? 
``` 
___

### 12. Message

&rarr; <ins>User</ins>

```
Tournament 
```
___

### 13. Message

&larr; <ins>Bot</ins>
``` 
How much is for buy in? 
``` 
___

### 14. Message

&rarr; <ins>User</ins>

```
10 
```
___

### 15. Message

&larr; <ins>Bot</ins>
``` 
Who's playing? 
``` 
___

### 16. Message

&rarr; <ins>User</ins>

```
[reply to message id 3]
 
```
___

### 17. Message

&larr; <ins>Bot</ins>
``` 
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
message id 17 pinned
``` 
___

### 19. Deleted messages

``` 
message ids 11,12,13,14,15,16 deleted
``` 
___

### 20. Message

&rarr; <ins>User</ins>

```
/game_stats 
```
___

### 21. Message

&larr; <ins>Bot</ins>
``` 
Tournament game statistics:
  - players entered -> 2
  - number of entries -> 2
  - total in game -> 20.00 
``` 
___

### 22. Message

&rarr; <ins>User</ins>

```
[reply to message id 1]
/stop_poll 
```
___

### 23. Message

&larr; <ins>Bot</ins>
``` 
Poll stopped 
``` 
___