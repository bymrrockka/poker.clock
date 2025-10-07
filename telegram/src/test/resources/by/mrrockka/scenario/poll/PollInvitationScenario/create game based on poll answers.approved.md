### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
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
message id 1
Test poll
1. 'Yes'
2. 'No'
3. 'I don't know'
``` 
___

### 3. Posted

``` 
message id 1 pinned
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

### 9. Interaction

&rarr; <ins>User message</ins>

```
[reply to message id 1]
message id: 2
/tournament_game
buyin: 10
 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
[reply to message id 0]
message id: 3
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Poll stopped 
``` 
___