### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @nickname6, @nickname7, @nickname8, @nickname9, @nickname10, @nickname11, @nickname12, @nickname13, @nickname14, @nickname15 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.
------------------------------
Table 1
Seats:
  1. @nickname7
  2. @nickname10
  3. @nickname4
  4. @nickname2
  5. @nickname13
  7. @nickname14
  8. @nickname9
  9. @nickname8
                                
------------------------------
Table 2
Seats:
  1. @nickname6
  2. @nickname12
  3. @nickname5
  4. @nickname3
  5. @nickname11
  7. @nickname1
  10. @nickname15
                                 
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
/entry @nickname16  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 2
Seats:
  @nickname16 seat 8 -> entry 10.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname17  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname17 seat 6 -> entry 10.00 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname18  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 2
Seats:
  @nickname18 seat 6 -> entry 10.00 
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
  1. @nickname1 won 100
  2. @nickname2 won 60
  3. @nickname3 won 40
Total: 200 (20 entries * 10 buy in)
------------------------------
Payout to: @nickname3
  Entries: 10
  Total: 30 (won 40 - entries 10)
From:
  @nickname9 -> 10
  @nickname8 -> 10
  @nickname7 -> 10
------------------------------
Payout to: @nickname2
  Entries: 10
  Total: 50 (won 60 - entries 10)
From:
  @nickname6 -> 10
  @nickname5 -> 10
  @nickname4 -> 10
  @nickname18 -> 10
  @nickname17 -> 10
------------------------------
Payout to: @nickname1
  Entries: 30
  Total: 70 (won 100 - entries 30)
From:
  @nickname16 -> 10
  @nickname15 -> 10
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