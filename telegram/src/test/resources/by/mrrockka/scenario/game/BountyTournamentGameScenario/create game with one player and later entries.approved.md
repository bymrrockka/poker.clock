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
Stored entries: 
  - @nickname3 -> 10 
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
Stored entries: 
  - @nickname2 -> 10 
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
/bounty @nickname2 kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @nickname2 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
message id: 9
/finale_places
1 @me 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer 
``` 
___

### 11. Interaction

&rarr; <ins>User message</ins>

```
message id: 10
/bounty @me kicked @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname2 stored for @hong_beer 
``` 
___

### 12. Interaction

&rarr; <ins>User message</ins>

```
message id: 11
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @hong_beer won 50
Total: 50 (5 entries * 10 buy in)
-----------------------------
Payout to: @hong_beer
  Entries: 1
  Bounties: 20 (taken 2 - given 0) 
  Total: 60 (won 50 - entries 10 + bounties 20)
From:
  @nickname3 -> 40
  @nickname1 -> 20
-----------------------------
Players played equally
  @nickname2 
``` 
___