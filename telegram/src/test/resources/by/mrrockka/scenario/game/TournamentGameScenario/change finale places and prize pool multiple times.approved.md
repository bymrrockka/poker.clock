### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@me 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.

Seats:
  5. @hong_beer 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/entry @nickname3  
```

&rarr; <ins>Bot message</ins>

``` 
Entry: 
  @nickname3: seat 1 -> entry 10 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/prize_pool
1 100% 
```

&rarr; <ins>Bot message</ins>

``` 
Prize pool stored:
1. 100% 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Entry: 
  @nickname1: seat 7 -> entry 10 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/finale_places
1 @me 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname2  
```

&rarr; <ins>Bot message</ins>

``` 
Entry: 
  @nickname2: seat 3 -> entry 10 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/prize_pool
1 50%, 2 50% 
```

&rarr; <ins>Bot message</ins>

``` 
Prize pool stored:
1. 50%
2. 50% 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/finale_places
1 @me, 2 @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer
2. @nickname2 
``` 
___

### 9. Interaction

&rarr; <ins>User message</ins>

```
message id: 8
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @hong_beer won 20
  2. @nickname2 won 20
Total: 40 (4 entries * 10 buy in)
-----------------------------
Payout to: @nickname2
  Entries: 1
  Total: 10 (won 20 - entries 10)
From:
  @nickname3 -> 10
-----------------------------
Payout to: @hong_beer
  Entries: 1
  Total: 10 (won 20 - entries 10)
From:
  @nickname1 -> 10 
``` 
___