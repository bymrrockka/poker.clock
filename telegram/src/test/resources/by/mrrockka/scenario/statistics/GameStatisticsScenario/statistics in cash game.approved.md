### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/cash_game
buyin: 10

@me, @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/game_stats 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game statistics:
  - players entered -> 2
  - total buy-in amount -> 20.00
  - total withdrawal amount -> 0
  - total in game -> 20.00 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/entry @me 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @hong_beer -> 30 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/game_stats 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game statistics:
  - players entered -> 2
  - total buy-in amount -> 50.00
  - total withdrawal amount -> 0
  - total in game -> 50.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/withdrawal @me 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @hong_beer -> 30 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/game_stats 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game statistics:
  - players entered -> 2
  - total buy-in amount -> 50.00
  - total withdrawal amount -> 30.00
  - total in game -> 20.00 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/withdrawal @me 20 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @hong_beer -> 20 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/game_stats 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game statistics:
  - players entered -> 2
  - total buy-in amount -> 50.00
  - total withdrawal amount -> 50.00
  - total in game -> 0.00 
``` 
___