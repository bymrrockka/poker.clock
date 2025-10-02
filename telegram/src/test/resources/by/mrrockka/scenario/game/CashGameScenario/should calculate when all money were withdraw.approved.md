### 1. Interaction

&rarr; <ins>User message</ins>

```
/cash_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @me 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
/withdrawal @nickname1 20 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname1 -> 20 
``` 
___

### 3. Interaction

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

### 4. Interaction

&rarr; <ins>User message</ins>

```
/entry @nickname4 20 
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname4 -> 20 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
/withdrawal @nickname3 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname3 -> 30 
``` 
___

### 6. Interaction

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
  @nickname5 -> 10
  @hong_beer -> 10
-----------------------------
Payout to: @nickname3
  Entries: 10
  Withdrawals: 30
  Total: 20 
From:
  @nickname4 -> 20
-----------------------------
Payout to: @nickname1
  Entries: 10
  Withdrawals: 20
  Total: 10 
From:
  @nickname4 -> 10 
``` 
___