### 1. Interaction

&rarr; <ins>User message</ins>

```
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
/tournament_game
buyin: 30

@me 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Message doesn't contain any attached messages. 
Please reply to poll creation message to stop poll 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
[reply to /create_poll]
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Poll stopped 
``` 
___