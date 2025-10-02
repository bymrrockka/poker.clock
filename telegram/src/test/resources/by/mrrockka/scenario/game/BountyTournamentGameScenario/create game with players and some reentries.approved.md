### 1. Interaction

&rarr; <ins>User message</ins>

```
/bounty_game
buyin: 10
bounty: 10
@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty tournament game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
/bounty @me kicked @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname3 stored for @hong_beer 
``` 
___

### 3. Interaction

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

### 4. Interaction

&rarr; <ins>User message</ins>

```
/bounty @me kicked @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname3 stored for @hong_beer 
``` 
___

### 5. Interaction

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

### 6. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname1 kicked @nickname4 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname4 stored for @nickname1 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname1 kicked @nickname5 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname5 stored for @nickname1 
``` 
___

### 8. Interaction

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

### 9. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname3 kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @nickname3 
``` 
___

### 10. Interaction

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

### 11. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname3 kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @nickname3 
``` 
___

### 12. Interaction

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

### 13. Interaction

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

### 14. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname2 kicked @nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname1 stored for @nickname2 
``` 
___

### 15. Interaction

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

### 16. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname2 kicked @nickname3 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname3 stored for @nickname2 
``` 
___

### 17. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname2 kicked @me 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @hong_beer stored for @nickname2 
``` 
___

### 18. Interaction

&rarr; <ins>User message</ins>

```
/bounty @nickname1 kicked @nickname2 
```

&rarr; <ins>Bot message</ins>

``` 
Bounty amount 10.00 from @nickname2 stored for @nickname1 
``` 
___

### 19. Interaction

&rarr; <ins>User message</ins>

```
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Finale summary:
  1. @nickname1 won 55
  2. @nickname2 won 55
Total: 110 (11 entries * 10 buy in)
-----------------------------
Payout to: @nickname2
  Entries: 1
  Bounties: 20 (taken 3 - given 1) 
  Total: 65 (won 55 - entries 10 + bounties 20)
From:
  @nickname3 -> 40
  @nickname4 -> 20
  @nickname5 -> 5
-----------------------------
Payout to: @nickname1
  Entries: 4
  Bounties: 0 (taken 3 - given 3) 
  Total: 15 (won 55 - entries 40 + bounties 0)
From:
  @nickname5 -> 15
-----------------------------
Players played equally
  @hong_beer 
``` 
___