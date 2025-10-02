### 1. Interaction

&rarr; <ins>User message</ins>

```
/tournament_game
buyin: 10

@me, @nickname1, @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
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

### 3. Interaction

&rarr; <ins>User message</ins>

```
/finale_places
1 @me, 2 @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @hong_beer
2. @nickname1 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @hong_beer won 15
  2. @nickname1 won 15
Total: 30 (3 entries * 10 buy in)
-----------------------------
Payout to: @nickname1
  Entries: 1
  Total: 5 (won 15 - entries 10)
From:
  @nickname2 -> 5
-----------------------------
Payout to: @hong_beer
  Entries: 1
  Total: 5 (won 15 - entries 10)
From:
  @nickname2 -> 5 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
/my_stats 
```

&rarr; <ins>Bot message</ins>

``` 
nickname: @hong_beer
games played: 1
buy-ins total: 10.00
won total: 5.00 
times in prizes: 1
times in first place: 1 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
/cash_game
buyin: 10

@me, @nickname1, @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started. 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
/withdrawal @nickname2 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname2 -> 30 
``` 
___

### 8. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Payout to: @nickname2
  Entries: 10
  Withdrawals: 30
  Total: 20 
From:
  @nickname1 -> 10
  @hong_beer -> 10 
``` 
___

### 9. Interaction

&rarr; <ins>User message</ins>

```
/my_stats 
```

&rarr; <ins>Bot message</ins>

``` 
nickname: @hong_beer
games played: 2
buy-ins total: 20.00
won total: 5.00 
times in prizes: 1
times in first place: 1 
``` 
___

### 10. Interaction

&rarr; <ins>User message</ins>

```
/bounty_game
buyin: 10
bounty: 10
@me, @nickname1, @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty tournament game started. 
``` 
___

### 11. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname1 kicked @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname2 stored for @nickname1 
``` 
___

### 12. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname1 kicked @me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @hong_beer stored for @nickname1 
``` 
___

### 13. Interaction

&rarr; <ins>User message</ins>

```
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

### 14. Interaction

&rarr; <ins>User message</ins>

```
/finale_places
1 @nickname1, 2 @me 
```

&rarr; <ins>Bot message</ins>

``` 
Finale places stored:
1. @nickname1
2. @hong_beer 
``` 
___

### 15. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @nickname1 won 15
  2. @hong_beer won 15
Total: 30 (3 entries * 10 buy in)
-----------------------------
Payout to: @nickname1
  Entries: 1
  Bounties: 20 (taken 2 - given 0) 
  Total: 25 (won 15 - entries 10 + bounties 20)
From:
  @nickname2 -> 20
  @hong_beer -> 5 
``` 
___

### 16. Interaction

&rarr; <ins>User message</ins>

```
/my_stats 
```

&rarr; <ins>Bot message</ins>

``` 
nickname: @hong_beer
games played: 3
buy-ins total: 30.00
won total: 5.00 
times in prizes: 2
times in first place: 1 
``` 
___

### 17. Interaction

&rarr; <ins>User message</ins>

```
/tournament_game
buyin: 10

@me, @nickname1, @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Tournament game started. 
``` 
___

### 18. Interaction

&rarr; <ins>User message</ins>

```
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

### 19. Interaction

&rarr; <ins>User message</ins>

```
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

### 20. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @nickname1 won 15
  2. @nickname2 won 15
Total: 30 (3 entries * 10 buy in)
-----------------------------
Payout to: @nickname2
  Entries: 1
  Total: 5 (won 15 - entries 10)
From:
  @hong_beer -> 5
-----------------------------
Payout to: @nickname1
  Entries: 1
  Total: 5 (won 15 - entries 10)
From:
  @hong_beer -> 5 
``` 
___

### 21. Interaction

&rarr; <ins>User message</ins>

```
/my_stats 
```

&rarr; <ins>Bot message</ins>

``` 
nickname: @hong_beer
games played: 4
buy-ins total: 40.00
won total: 5.00 
times in prizes: 2
times in first place: 1 
``` 
___