### 1. Interaction

&rarr; <ins>User message</ins>

```
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
Will be triggered next WEDNESDAY 
``` 
___

### 2. Posted

``` 
Test poll
1. 'Yes'
2. 'Noooooo'
3. 'Hell yeah 12123'
4. ';.!@#$%^&*()(_+=<>.,/{}[]`~'
5. 'I don't know'
``` 
___

### 3. Interaction

&rarr; <ins>User message</ins>

```
[reply to /create_poll]
/stop_poll 
```

&rarr; <ins>Bot message</ins>

``` 
Poll stopped 
``` 
___