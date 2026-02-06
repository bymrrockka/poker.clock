### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/bounty_game
buyin: 10
bounty: 10
@me, @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty tournament game started.
------------------------------
Table 1
Seats:
  3. @hong_beer
  7. @nickname1
                                 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 10.00
entries number: 1
bounties:
  taken: 0
  given: 0
game total: -10.00 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/bounty @me kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @hong_beer 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 10.00
entries number: 1
bounties:
  taken: 1
  given: 0
game total: 0.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Entry stored 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/bounty @nickname1 kicked @me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @hong_beer stored for @nickname1 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 10.00
entries number: 1
bounties:
  taken: 1
  given: 1
game total: -10.00 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/entry @me  
```

&rarr; <ins>Bot message</ins>

``` 
Entry stored 
``` 
___

### 9. Interaction

&rarr; <ins>User message</ins>

```
message id: 8
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 20.00
entries number: 2
bounties:
  taken: 1
  given: 1
game total: -20.00 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
message id: 9
/bounty @nickname1 kicked @me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @hong_beer stored for @nickname1 
``` 
___

### 11. Interaction

&rarr; <ins>User message</ins>

```
message id: 10
/player_stats 
```

&rarr; <ins>Bot message</ins>

``` 
@hong_beer game statistics:
entries: 20.00
entries number: 2
bounties:
  taken: 1
  given: 2
game total: -30.00 
``` 
___