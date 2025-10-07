### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tg
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @me 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
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
Stored entries: 
  - @nickname3 -> 10 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/entry @nickname3  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname3 -> 10 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
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

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/finale_places
1 @nickname1, 2 @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @nickname1
2. @nickname2 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 9. Interaction

&rarr; <ins>User message</ins>

```
message id: 8
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
message id: 9
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname1 -> 10 
``` 
___

### 11. Interaction

&rarr; <ins>User message</ins>

```
message id: 10
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @nickname1 won 65
  2. @nickname2 won 65
Total: 130 (13 entries * 10 buy in)
-----------------------------
Payout to: @nickname2
  Entries: 1
  Total: 55 (won 65 - entries 10)
From:
  @nickname3 -> 30
  @nickname4 -> 10
  @hong_beer -> 10
  @nickname5 -> 5
-----------------------------
Payout to: @nickname1
  Entries: 6
  Total: 5 (won 65 - entries 60)
From:
  @nickname5 -> 5 
``` 
___