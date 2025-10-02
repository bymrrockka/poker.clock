### 1. Interaction

&rarr; <ins>User message</ins>

```
/tournament_game
buyin: 10

@me 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
/entry @nickname3  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname3 -> 10 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
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
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
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
/entry @nickname2  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname2 -> 10 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @hong_beer won 40
Total: 40 (4 entries * 10 buy in)
-----------------------------
Payout to: @hong_beer
  Entries: 1
  Total: 30 (won 40 - entries 10)
From:
  @nickname3 -> 10
  @nickname2 -> 10
  @nickname1 -> 10 
``` 
___