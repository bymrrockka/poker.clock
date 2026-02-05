### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @nickname6, @nickname7, @nickname8, @nickname9, @nickname10, @nickname11 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.
------------------------------
Table 1
Seats:
  1. @nickname2
  2. @nickname5
  5. @nickname10
  6. @nickname4
  7. @nickname11
  10. @nickname9
                                
------------------------------
Table 2
Seats:
  2. @nickname6
  7. @nickname8
  8. @nickname1
  9. @nickname7
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
/entry @nickname12  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
                                    
------------------------------
Table 2
Seats:
  @nickname12 seat 1 -> entry 10.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname13  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 2
Seats:
                                    
------------------------------
Table 1
Seats:
  @nickname13 seat 3 -> entry 10.00 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname14  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
                                    
------------------------------
Table 2
Seats:
  @nickname14 seat 6 -> entry 10.00 
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
  1. @nickname1 won 80
  2. @nickname2 won 48
  3. @nickname3 won 32
Total: 160 (16 entries * 10 buy in)
------------------------------
Payout to: @nickname3
  Entries: 10
  Total: 22 (won 32 - entries 10)
From:
  @nickname8 -> 10
  @nickname7 -> 10
  @nickname9 -> 2
------------------------------
Payout to: @nickname2
  Entries: 10
  Total: 38 (won 48 - entries 10)
From:
  @nickname6 -> 10
  @nickname5 -> 10
  @nickname4 -> 10
  @nickname9 -> 8
------------------------------
Payout to: @nickname1
  Entries: 30
  Total: 50 (won 80 - entries 30)
From:
  @nickname14 -> 10
  @nickname13 -> 10
  @nickname12 -> 10
  @nickname11 -> 10
  @nickname10 -> 10 
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