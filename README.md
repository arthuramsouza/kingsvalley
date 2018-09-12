# King's Valley

Java RMI implementation of King's Valley, a strategy board game conceptualized by Mitsuo Yamamoto in 2006.

## The Board

As illustrated by the picture below, the board consists of five rows and five columns:

![alt text](http://www.gift-box.co.jp/english/kingsvalley/KV15.jpg)  

Each player receives five pieces:
- Four soldiers
- One king

All board cells (positions) present the same behavior, except for the one in the middle (position 3x3). That particular cell can only receive king pieces.

## The Rules

On every turn, one of the players must reallocate one of their pieces. Pieces can move in any direction. The only restraint is that the pieces must move the maximum amount of cells as it possibly can (as long as there are empty cells along the way), stopping only when it reaches the border or if it bumps into another piece.
The goal is to place your king piece on the center cell (position 3x3). 
