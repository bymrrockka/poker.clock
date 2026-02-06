### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.
------------------------------
Table 1
Seats:
  1. @nickname1
  2. @nickname4
  6. @nickname3
  7. @nickname2
                                 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Entry stored 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/entry @nickname1  
```

&rarr; <ins>Bot message</ins>

``` 
Entry stored 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/entry @nickname5  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname5 seat 5 -> entry 10.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname6  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname6 seat 4 -> entry 10.00 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname7  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname7 seat 10 -> entry 10.00 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/prize_pool
1 50%
2 30%
3 20% 
```

&rarr; <ins>Bot message</ins>

``` 
Prize pool stored:
1. 50%
2. 30%
3. 20% 
``` 
___

### 8. Pinned

``` 
message id 6 pinned
``` 
___

### 9. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/finale_places
1 @nickname1, 2 @nickname2, 3 @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @nickname1
2. @nickname2
3. @nickname3 
``` 
___

### 10. Pinned

``` 
message id 7 pinned
``` 
___

### 11. Interaction

&rarr; <ins>User message</ins>

```
message id: 8
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
------------------------------
You can support me using this link. 
https://buymeacoffee.com/mrrockka
------------------------------
Finale summary:
  1. @nickname1 won 45
  2. @nickname2 won 27
  3. @nickname3 won 18
Total: 90 (9 entries * 10 buy in)
------------------------------
Payout to: @nickname3
  Entries: 10
  Total: 8 (won 18 - entries 10)
From:
  @nickname7 -> 8
------------------------------
Payout to: @nickname2
  Entries: 10
  Total: 17 (won 27 - entries 10)
From:
  @nickname4 -> 10
  @nickname7 -> 7
------------------------------
Payout to: @nickname1
  Entries: 30
  Total: 15 (won 45 - entries 30)
From:
  @nickname5 -> 10
  @nickname7 -> 5 
``` 
___

### 12. Pinned

``` 
message id 8 pinned
``` 
___

### 13. Unpinned

``` 
message id 0 unpinned
``` 
___

### 14. Unpinned

``` 
message id 6 unpinned
``` 
___

### 15. Unpinned

``` 
message id 7 unpinned
``` 
___