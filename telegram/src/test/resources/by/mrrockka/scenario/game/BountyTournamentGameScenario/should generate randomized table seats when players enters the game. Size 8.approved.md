### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @nickname6, @nickname7, @nickname8 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.
------------------------------
Table 1
Seats:
  1. @nickname1
  3. @nickname5
  4. @nickname7
  5. @nickname4
  6. @nickname2
  8. @nickname8
  9. @nickname6
  10. @nickname3
                                 
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
/entry @nickname9  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname9 seat 7 -> entry 10.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname10  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname10 seat 2 -> entry 10.00 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname11  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 2
Seats:
  @nickname11 seat 2 -> entry 10.00
  @nickname7 seat 4 -> entry 10.00
  @nickname1 seat 5 -> entry 10.00
  @nickname8 seat 7 -> entry 10.00
  @nickname5 seat 10 -> entry 10.00 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/prize_pool
1 50%, 2 30%, 3 20% 
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
  1. @nickname1 won 65
  2. @nickname2 won 39
  3. @nickname3 won 26
Total: 130 (13 entries * 10 buy in)
------------------------------
Payout to: @nickname3
  Entries: 10
  Total: 16 (won 26 - entries 10)
From:
  @nickname7 -> 10
  @nickname9 -> 6
------------------------------
Payout to: @nickname2
  Entries: 10
  Total: 29 (won 39 - entries 10)
From:
  @nickname6 -> 10
  @nickname5 -> 10
  @nickname9 -> 9
------------------------------
Payout to: @nickname1
  Entries: 30
  Total: 35 (won 65 - entries 30)
From:
  @nickname4 -> 10
  @nickname11 -> 10
  @nickname10 -> 10
  @nickname9 -> 5 
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