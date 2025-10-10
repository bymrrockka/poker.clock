### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/create_poll
cron: 0 0 0 * * 3
message: Test poll
options: 
1. Yes - participant 
```

&rarr; <ins>Bot message</ins>

``` 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/tournament_game
buyin: 30

@me 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.

Seats:
  4. @hong_beer 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
[reply to message id 1]
message id: 2
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Poll was not found 
``` 
___