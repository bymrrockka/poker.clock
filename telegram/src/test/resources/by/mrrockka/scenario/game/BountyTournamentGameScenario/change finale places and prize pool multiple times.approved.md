### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/bounty_game
buyin: 10
bounty: 10
@me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty tournament game started.

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
/bounty @me kicked @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname3 stored for @hong_beer 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/entry @nickname3  
```

&rarr; <ins>Bot message</ins>

``` 
Entry: 
  @nickname3: seat 1 -> entry 10 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/prize_pool
1 100% 
```

&rarr; <ins>Bot message</ins>

``` 
Prize pool stored:
1. 100% 
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
  @nickname2: seat 2 -> entry 10 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 6
/bounty @nickname2 kicked @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname3 stored for @nickname2 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
message id: 7
/finale_places
1 @me 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer 
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
Entry: 
  @nickname1: seat 4 -> entry 10 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
message id: 9
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

### 11. Interaction

&rarr; <ins>User message</ins>

```
message id: 10
/bounty @nickname2 kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @nickname2 
``` 
___

### 12. Interaction

&rarr; <ins>User message</ins>

```
message id: 11
/bounty @me kicked @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname2 stored for @hong_beer 
``` 
___

### 13. Interaction

&rarr; <ins>User message</ins>

```
message id: 12
/finale_places
1 @me, 2 @nickname1, 3 @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer
2. @nickname1
3. @nickname2 
``` 
___

### 14. Interaction

&rarr; <ins>User message</ins>

```
message id: 13
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @hong_beer won 25
  2. @nickname1 won 15
  3. @nickname2 won 10
Total: 50 (5 entries * 10 buy in)
-----------------------------
Payout to: @nickname2
  Entries: 1
  Bounties: 10 (taken 2 - given 1) 
  Total: 10 (won 10 - entries 10 + bounties 10)
From:
  @nickname3 -> 10
-----------------------------
Payout to: @hong_beer
  Entries: 1
  Bounties: 20 (taken 2 - given 0) 
  Total: 35 (won 25 - entries 10 + bounties 20)
From:
  @nickname1 -> 5
  @nickname3 -> 30 
``` 
___