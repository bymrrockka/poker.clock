### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/tournament_game
buyin: 10

@nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started.
------------------------------
Table 1
Seats:
  5. @nickname1
                                 
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
/entry @nickname2  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname2 seat 7 -> entry 10.00 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/entry @nickname3  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname3 seat 9 -> entry 10.00 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/entry @nickname4  
```

&rarr; <ins>Bot message</ins>

``` 
Entries: 
------------------------------
Table 1
Seats:
  @nickname4 seat 3 -> entry 10.00 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/prize_pool
1 100% 
```

&rarr; <ins>Bot message</ins>

``` 
Prize pool stored:
1. 100% 
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
1 @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @nickname1 
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
  1. @nickname1 won 60
Total: 60 (6 entries * 10 buy in)
------------------------------
Payout to: @nickname1
  Entries: 30
  Total: 30 (won 60 - entries 30)
From:
  @nickname4 -> 10
  @nickname3 -> 10
  @nickname2 -> 10 
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