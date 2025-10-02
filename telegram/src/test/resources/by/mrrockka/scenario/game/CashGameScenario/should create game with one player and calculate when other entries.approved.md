### 1. Interaction

&rarr; <ins>User message</ins>

```
/cash_game
buyin: 10

@nickname1 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started. 
``` 
___

### 2. Interaction

&rarr; <ins>User message</ins>

```
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