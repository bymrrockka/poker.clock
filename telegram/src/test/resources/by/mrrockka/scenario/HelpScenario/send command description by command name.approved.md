### 1. Message

&rarr; <ins>User</ins>

```
/help game 
```
___

### 2. Message

&larr; <ins>Bot</ins>
``` 
You will be asked to input parameters of the game you want to start.

There are three types of games that are supported: Tournament, Cash and Bounty tournament.

In order to start a game you should specify a type, buy in, bounty if applicable and players.
Also if you are aware of automatic poll creation feature `/create_poll` then you can have poll as a reply message when bot will ask about players so all the people that answered `participant` option will be added to game automatically. 

For the re-entries and new players `/entry @me` could be used, for tournaments buy in will be game buy in as default, for cash games you could specify the number in this message which will be used instead of default.

For all tournaments in order to calculate payouts finale places and prize pool should be specified.

And after all set you can call `/calculate` command to see game summary and all the calculations.
 
``` 
___