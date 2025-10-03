### 1. Interaction

&rarr; <ins>User message</ins>

```
/create_poll
cron: 0 0 0 * * WED
message: Test poll
options: 
1. Yes - participant
2. No
3. I don't know 
```

&rarr; <ins>Bot message</ins>

``` 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 2. Posted

&rarr; <ins>2025-09-24 - WEDNESDAY</ins>

``` 
Test poll
1. 'Yes'
2. 'No'
3. 'I don't know'
``` 
___

### 3. Posted

``` 
chatPoll pinned
``` 
___

### 4. Poll answer

``` 
jackie_rau chosen 2
``` 
___

### 5. Poll answer

``` 
terisa_johnston chosen 2
``` 
___

### 6. Poll answer

``` 
santo_welch chosen 3
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
[reply to chatPoll]
/tournament_game
buyin: 10
 
```

&rarr; <ins>Bot message</ins>

``` 
Game participants not found according to poll 
``` 
___