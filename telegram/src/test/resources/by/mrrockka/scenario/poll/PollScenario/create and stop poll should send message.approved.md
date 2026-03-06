### 1. Message

&rarr; <ins>User</ins>

```
Processed message id: 1
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
___

### 2. Message

&rarr; <ins>Bot</ins>
``` 
message id: 2 
Poll created.
Will be triggered next WEDNESDAY 00:00 
``` 
___

### 3. Posted

&rarr; <ins>2025-09-24 - WEDNESDAY</ins>

``` 
message id 3
Test poll
1. 'Yes'
2. 'Noooooo'
3. 'Hell yeah 12123'
4. ';.!@#$%^&*()(_+=<>.,/{}[]`~'
5. 'I don't know'
``` 
___

### 4. Pinned

``` 
message id 3 pinned
``` 
___

### 5. Message

&rarr; <ins>User</ins>

```
Processed [reply to message id 1]
message id: 4
/stop_poll 
```
___

### 6. Message

&rarr; <ins>Bot</ins>
``` 
message id: 5 
Poll stopped 
``` 
___