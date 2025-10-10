### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/cash_game
buyin: 10

@nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started.

Seats:
  5. @nickname1 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
/entry @nickname2  
```

&rarr; <ins>Bot message</ins>

``` 
Stored entries: 
  - @nickname2 -> 10 
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 2
/withdrawal @nickname1 20 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname1 -> 20 
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
-----------------------------
Payout to: @nickname1
  Entries: 10
  Withdrawals: 20
  Total: 10 
From:
  @nickname2 -> 10 
``` 
___