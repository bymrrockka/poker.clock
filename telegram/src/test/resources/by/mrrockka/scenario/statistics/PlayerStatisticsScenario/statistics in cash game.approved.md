### 1. Interaction

&rarr; <ins>User message</ins>

```
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
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 10.00
entries number: 1
withdrawals: 0
game total: -10.00 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
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
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 40.00
entries number: 2
withdrawals: 0
game total: -40.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
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
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 40.00
entries number: 2
withdrawals: 30.00
game total: -10.00 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
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
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 40.00
entries number: 2
withdrawals: 50.00
game total: 10.00 
``` 
___