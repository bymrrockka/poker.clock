### 1. Interaction

&rarr; <ins>User message</ins>

```
message id: 0
/create_poll
cron: 0 0 0 * * WED
message: Test poll
options: 
1. Yes - participant
2. Noooooo
3. Hell yeah 12123
4. ;.!@#$%^&*()(_+=<>.,/{}[]`~
5. I don't know 
```

&rarr; <ins>Bot message</ins>

``` 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 2. Posted

&rarr; <ins>2025-09-24 - WEDNESDAY</ins>

``` 
message id 1
Test poll
1. 'Yes'
2. 'Noooooo'
3. 'Hell yeah 12123'
4. ';.!@#$%^&*()(_+=<>.,/{}[]`~'
5. 'I don't know'
``` 
___

### 3. Pinned

``` 
message id 1 pinned
``` 
___

### 4. Interaction

&rarr; <ins>User message</ins>

```
[reply to message id 0]
message id: 2
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Poll stopped 
``` 
___