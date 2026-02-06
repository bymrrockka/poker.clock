### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/cash_game
buyin: 10

@nickname1, @nickname2, @nickname3, @nickname4, @nickname5, @me 
```

&rarr; <ins>Bot message</ins>

``` 
Cash game started.
------------------------------
Table 1
Seats:
  2. @nickname5
  3. @hong_beer
  5. @nickname2
  6. @nickname3
  8. @nickname4
  10. @nickname1
                                 
``` 
___

### 2. Pinned

``` 
message id 0 pinned
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
message id: 1
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
message id: 2
/withdrawal @nickname2 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname2 -> 30 
``` 
___

### 5. Interaction

&rarr; <ins>User message</ins>

```
message id: 3
/entry @nickname4 20 
```

&rarr; <ins>Bot message</ins>

``` 
Entry stored 
``` 
___

### 6. Interaction

&rarr; <ins>User message</ins>

```
message id: 4
/withdrawal @nickname3 30 
```

&rarr; <ins>Bot message</ins>

``` 
Stored withdrawals: 
  - @nickname3 -> 30 
``` 
___

### 7. Interaction

&rarr; <ins>User message</ins>

```
message id: 5
/calculate 
```

&rarr; <ins>Bot message</ins>

``` 
------------------------------
You can support me using this link. 
https://buymeacoffee.com/mrrockka
------------------------------
Payout to: @nickname2
  Entries: 10
  Withdrawals: 30
  Total: 20 
From:
  @nickname5 -> 10
  @hong_beer -> 10
------------------------------
Payout to: @nickname3
  Entries: 10
  Withdrawals: 30
  Total: 20 
From:
  @nickname4 -> 20
------------------------------
Payout to: @nickname1
  Entries: 10
  Withdrawals: 20
  Total: 10 
From:
  @nickname4 -> 10 
``` 
___

### 8. Pinned

``` 
message id 5 pinned
``` 
___

### 9. Unpinned

``` 
message id 0 unpinned
``` 
___