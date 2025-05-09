# Noah Clouser's othello_player implementation

This repository showcases my implementation of creating an AI agent to play the game Othello, also known as Reversi. This was the technical coding challenge given by Atomic Object. This project is split into two sections, the server and AI agent. othello.jar is the server and the AI folder contains the files for the AI agent. My task from Atomic Object was to implement the AI.java file such that intelligent moves would be calculated and sent to the server during an active game.

To create this agent, I utilized the Mini-Max Alpha-Beta pruning algorithm to determine the next best move the AI agent should take given the GameState. I used a simple static heuristic to evalulate 
the score of the game board at either a terminal node or limit. This agent performs amazing against the random othello player, but could be improved with a dynamic heuristic.

# Getting Started

## Quick Demo
1. Execute the run_server.sh file in the ```othello_player/``` directory: ```./run_server.sh```
2. Navigate to ```localhost:8080``` in a browser
3. In a separate terminal execute the run_agent.sh file in the ```othello_player/``` directory: ```./run_agent.sh``` 
4. Optional: Refresh the browser tab if gameplay does not start up immediately
## Shell files
If you would want to customize the game (i.e. make 2 random players play against one another), edit the run_server.sh file with the different options listed below
1. run_server.sh:

    Template: ```$ java -jar othello.jar [options]```<br>
    Usage: ```$ java -jar othello.jar --p1-type remote --p2-type random --wait-for-ui```
2. run_agent.sh:<br>
    Usage: <br>```javac -cp AI/lib/gson-2.8.5.jar -d AI/bin "AI/src/com/atomicobject/othello/*.java"```<br>```java -cp "AI\bin;AI\lib\gson-2.8.5.jar" com.atomicobject.othello.Main```
   
## Options

You can specify that the server should invoke your player, use a "robot" player with a predetermined set of moves, or use a random player for one or both players.

The player can be one of three types:
 * remote - the game will listen for a player to connect to the server
 * random - the game will make a random valid move for the player
 * robot - the game use moves specified in the `--p1-moves` or `--p2-moves` argument

You'll most likely want to run with your client as a remote player, and a random player for the opponent.



Use the `--ui-port` to specify a different UI port.
Pass the `--wait-for-ui` option in order to have the server wait for a UI connection before starting the game.

By default the game will time out if a player has not responeded within 15 seconds.
You can change this with the `--max-turn-time arg` (`--max-turn-time 20000` for 20 seconds).

Usage:
```
java -jar othello.jar
java -jar othello.jar --p1-type remote --p2-type random --wait-for-ui
```

Options:
```
      --p1-type TYPE          remote     Player one's type - remote, random, or robot
      --p2-type TYPE          remote     Player two's type - remote, random, or robot
      --p1-name NAME          Player One  Player one's team name
      --p2-name NAME          Player Two  Player two's team name
      --p1-moves MOVES        []          Moves for a P1 robot player
      --p2-moves MOVES        []          Moves for a P2 robot player
      --p1-port PORT          1337        Port number for the P1 client
      --p2-port PORT          1338        Port number for the P2 client
      --ui-port PORT          8080        Port number for UI clients
  -w, --wait-for-ui                       Wait for a UI client to connect before starting game
  -m, --min-turn-time MILLIS  1000        Minimum amount of time to wait between turns
  -x, --max-turn-time MILLIS  15000       Maximum amount of time to allow an AI for a turn
  -h, --help
```

## Moves

When the game server starts, it will wait for players to connect, then begin executing moves until it determines a winner.

When the game server needs a move from your client it will send the game state as JSON, followed by a newline. For example:

`{"board":[[0,0,0,0,0,0,0,0],[0,0,0,0,2,0,0,0],[0,0,0,0,2,0,0,0],[0,0,0,1,2,0,0,0],[0,0,0,1,2,2,0,0],[0,0,0,1,0,0,0,0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0]],"maxTurnTime":15000,"player":1}\n`

The "board" data structure is a list of game board rows, from the top to the bottom of the game board. A "0" indicates an empty square, a "1" indicates a player one piece, and a "2" indicates a player two piece.

Note the "player" field - read this field to determine if you are player one or player two. Your client should not assume that it always plays as player 1 or 2, however when you test it you as shown above you will explicitly choose which player it is assigned.

When you've computed a move, return it as a JSON array, followed by a newline, for example:

`"[1,2]\n"`

The coordinate system begins at the top left of the board. The coordinates are in [row, column] format. So [7,0] would indicate the lower left corner of the board. [0,0] indicates the top left corner and [7,7] indicates the bottom right corner.

Returning an invalid move will forfeit the game. Timing out (the default timeout is 15 seconds) will also forfeit the game.

## License

Copyright © 2018

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
