# King's Valley

Java RMI implementation of King's Valley, a strategy board game conceptualized by Mitsuo Yamamoto in 2006.

[![Java](https://img.shields.io/badge/java-10-blue.svg)](https://www.oracle.com/technetwork/java/javase/10-relnote-issues-4108729.html)
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

## The Board

As illustrated by the picture below, the board consists of five rows and five columns:

![King's Valley board](http://www.gift-box.co.jp/english/kingsvalley/KV15.jpg)  

Each player receives five pieces:
- Four soldiers
- One king

All board cells (positions) present the same behavior, except for the one in the middle (position 3x3). That particular cell can only receive king pieces.

## The Rules

On every turn, one of the players must reallocate one of their pieces. Pieces can move in any direction. The only restraint is that the pieces must move the maximum amount of cells as it possibly can (as long as there are empty cells along the way), stopping only when it reaches the border or if it bumps into another piece.
The goal is to place your king piece on the center cell (position 3x3). 

## Usage

After setting up the server, the client applications must be initialized just like the example below:

```bash
java KingsValleyClient 127.0.0.1 Arthur
```

The IP above should be replaced by the IP of the host where the server application is running. The second argument of the application is simply the player's name.
