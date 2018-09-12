public class Match {

    private Player player1, player2;
    private char[][] board = new char[5][5];
    private boolean ready;
    private Player currentPlayer;

    private static final char VAZIO = '.';
    private static final char PLAYER1_SOLDAOD = 'c';
    private static final char PLAYER1_REI = 'C';
    private static final char PLAYER2_SOLDADO = 'e';
    private static final char PLAYER2_REI = 'E';

    public Match(Player player1) {
        this.player1 = player1;
        this.ready = false;
        this.currentPlayer = this.player1;

        /* Inicia o tabuleiro */

        for(int i = 0; i < 5; i++) {
            if(i == 2)
                this.board[0][i] = PLAYER1_REI;
            else
                this.board[0][i] = PLAYER1_SOLDAOD;
        }

        for(int i = 0; i < 5; i++) {
            if(i == 2)
                this.board[4][i] = PLAYER2_REI;
            else
                this.board[4][i] = PLAYER2_SOLDADO;
        }
    }

    public void setPlayer1(Player p) { this.player1 = p; };

    public void setPlayer2(Player player2) {
        this.player2 = player2;
        this.ready = true;
    }

    public boolean isReady() { return this.ready; }
    public Player getPlayer1() { return this.player1; }
    public Player getPlayer2() { return this.player2; }
    public char[][] getBoard() { return this.board; }
    public Player getCurrentPlayer() { return this.currentPlayer; }

    private void changeTurn() {
    this.currentPlayer = (this.currentPlayer == this.player1 ? this.player2 : this.player1);
    }

    public int movePeca(int id, int linha, int coluna, int sentido) {

    if(!this.isReady()) {
      return -2;  // partida não iniciada
    }

    if(currentPlayer.hasTimedOut()) {
      return 2; // time-out
    }

    if(currentPlayer.getId() != id) {
        return -4; // não é a vez do jogador
    }

    if (linha < 0 || linha > 4 || coluna < 0 || coluna > 4 || sentido < 0 || sentido > 7) {
        return 0;
    }

    if (id == player1.getId()) {
        if (board[linha][coluna] == 's' || board[linha][coluna] == 'r') {
            return 0; // peca do player 2
        }
    }
    else if (id == player2.getId()) {
        if (board[linha][coluna] == 'S' || board[linha][coluna] == 'R') {
            return 0; // peca do player 1
        }
    }
    else {
        return -1; // jogador não encontrado
    }

    char peca = board[linha][coluna];
    int aux1 = coluna;
    int aux2 = linha;

    switch(sentido) {

        case 0: // para a direita

            if (board[linha][coluna + 1] != VAZIO)
                return 0;
            else {
                while (++aux1 <= 4 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2][aux1 - 1] = VAZIO;
                }
            }
            break;

        case 1: // diagonal direita-inferior

            if (board[linha + 1][coluna + 1] != VAZIO)
                return 0;
            else {
                while (++aux1 <= 4 && ++aux2 <= 4 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2 - 1][aux1 - 1] = VAZIO;
                }
            }
            break;

        case 2: // para baixo

            if (board[linha + 1][coluna] != VAZIO)
                return 0;
            else {
                while (++aux2 <= 4 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2 - 1][aux1] = VAZIO;
                }
            }
            break;

        case 3: // diagonal esquerda-inferior

            if (board[linha + 1][coluna - 1] != VAZIO)
                return 0;
            else {
                board[aux2][aux1] = VAZIO;
                while (--aux1 >= 0 && ++aux2 <= 4 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2 - 1][aux1 + 1] = VAZIO;
                }
            }
            break;

        case 4: // para a esquerda

            if (board[linha][coluna - 1] != VAZIO)
                return 0;
            else {
                while (--aux1 >= 0 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2][aux1 + 1] = VAZIO;
                }
            }
            break;

        case 5: // diagonal esquerda-superior

            if (board[linha - 1][coluna - 1] != VAZIO)
                return 0;
            else {
                while (--aux1 >= 0 && --aux2 >= 0 && board[aux2][aux1] == VAZIO) {
                    board[aux2][aux1] = peca;
                    board[aux2 + 1][aux1 + 1] = VAZIO;
                }
            }
            break;

        case 6: // para cima

            if (board[linha - 1][coluna] != VAZIO)
                return 0;
            else {
                while (--aux2 >= 0 && board[aux2][aux1] == VAZIO)
                    board[aux2][aux1] = peca;
                board[aux2 + 1][aux1] = VAZIO;
            }
            break;

        case 7: // diagonal direita-superior

            if (board[linha - 1][coluna + 1] != VAZIO)
                return 0;
            else {
                while (++aux1 >= 0 && --aux2 >= 0 && board[aux2][aux1] == VAZIO)
                    board[aux2][aux1] = peca;
                board[aux2 + 1][aux1 - 1] = VAZIO;
            }
            break;
    }

    currentPlayer.updateTimestamp();
    this.changeTurn();
    return 1;
    }

    public boolean hasTimedOut() {
    return ((player1 == null && player2 == null) ||
            (player1 == null && player2.hasTimedOut()) ||
            (player1.hasTimedOut() && player2 == null) ||
            (player1.hasTimedOut() && player2.hasTimedOut()));
    }
}
