public class Match {

    private Player player1, player2;
    private char[][] board = new char[5][5];
    private boolean ready;
    private Player currentPlayer;
    private boolean firstMove;

    private static final char VAZIO = '.';
    private static final char PLAYER1_SOLDAOD = 'c';
    private static final char PLAYER1_REI = 'C';
    private static final char PLAYER2_SOLDADO = 'e';
    private static final char PLAYER2_REI = 'E';

    public Match(Player player1) {
        this.player1 = player1;
        this.ready = false;
        this.currentPlayer = this.player1;
        this.firstMove = true;

        // Inicia o tabuleiro

        for(int i = 0; i < 5; i++) {
            if(i == 2)
                this.board[i][0] = PLAYER1_REI;
            else
                this.board[i][0] = PLAYER1_SOLDAOD;
        }

        for(int i = 0; i < 5; i++) {
            if(i == 2)
                this.board[i][4] = PLAYER2_REI;
            else
                this.board[i][4] = PLAYER2_SOLDADO;
        }

        for(int j = 1; j < 4; j++) {
            for (int i = 0; i < 5; i++) {
                this.board[i][j] = VAZIO;
            }
        }

    }

    public void setPlayer1(Player p) { this.player1 = p; }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
        this.ready = true;
    }

    public boolean canDelete() { return this.player1 == null && this.player2 == null; }

    public Player getGanhador() {
        if (board[2][2] == PLAYER1_REI)
            return player1;
        else if (board[2][2] == PLAYER2_REI)
            return player2;
        else
            return null;
    }

    public boolean isReady() { return this.ready; }
    public Player getPlayer1() { return this.player1; }
    public Player getPlayer2() { return this.player2; }
    public char[][] getBoard() { return this.board; }
    public Player getCurrentPlayer() { return this.currentPlayer; }

    private void changeTurn() {
        this.currentPlayer = (this.currentPlayer == this.player1 ? this.player2 : this.player1);
    }

    public int movePeca(int linha, int coluna, int sentido) {

        if ((linha < 0) || (linha > 4) || (coluna < 0) || (coluna > 4) || (sentido < 0) || (sentido > 7)) {
            return 0;
        }

        if(firstMove && ((board[linha][coluna] == PLAYER1_REI) || (board[linha][coluna] == PLAYER2_REI))) {
            return 0;
        }

        if (currentPlayer == player1) {
            if ((board[linha][coluna] == PLAYER2_SOLDADO) || (board[linha][coluna] == PLAYER2_REI)) {
                return 0; // peca do player 2
            }
        }
        else if (currentPlayer == player2) {
            if ((board[linha][coluna] == PLAYER1_SOLDAOD) || (board[linha][coluna] == PLAYER1_REI)) {
                return 0; // peca do player 1
            }
        }

        char peca = board[linha][coluna];
        int aux1 = coluna;
        int aux2 = linha;

        switch(sentido) {

            case 0: // para a direita

                if ((coluna + 1) > 4)
                    return 0;
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

                if (((linha + 1) > 4) || ((coluna + 1) > 4))
                    return 0;
                else if (board[linha + 1][coluna + 1] != VAZIO)
                    return 0;
                else {
                    while (++aux1 <= 4 && ++aux2 <= 4 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2 - 1][aux1 - 1] = VAZIO;
                    }
                }
                break;

            case 2: // para baixo

                if ((linha + 1) > 4)
                    return 0;
                else if (board[linha + 1][coluna] != VAZIO)
                    return 0;
                else {
                    while (++aux2 <= 4 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2 - 1][aux1] = VAZIO;
                    }
                }
                break;

            case 3: // diagonal esquerda-inferior

                if (((linha + 1) > 4) || ((coluna - 1) < 0))
                    return 0;
                else if (board[linha + 1][coluna - 1] != VAZIO)
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

                if ((coluna - 1) < 0)
                    return 0;
                else if (board[linha][coluna - 1] != VAZIO)
                    return 0;
                else {
                    while (--aux1 >= 0 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2][aux1 + 1] = VAZIO;
                    }
                }
                break;

            case 5: // diagonal esquerda-superior

                if (((linha - 1) < 0) || ((coluna - 1) < 0))
                    return 0;
                else if (board[linha - 1][coluna - 1] != VAZIO)
                    return 0;
                else {
                    while (--aux1 >= 0 && --aux2 >= 0 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2 + 1][aux1 + 1] = VAZIO;
                    }
                }
                break;

            case 6: // para cima

                if ((linha - 1) < 0)
                    return 0;
                else if (board[linha - 1][coluna] != VAZIO)
                    return 0;
                else {
                    while (--aux2 >= 0 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2 + 1][aux1] = VAZIO;
                    }
                }
                break;

            case 7: // diagonal direita-superior

                if (((linha - 1) < 0) || ((coluna + 1) > 4))
                    return 0;
                if (board[linha - 1][coluna + 1] != VAZIO)
                    return 0;
                else {
                    while (++aux1 <= 4 && --aux2 >= 0 && board[aux2][aux1] == VAZIO) {
                        board[aux2][aux1] = peca;
                        board[aux2 + 1][aux1 - 1] = VAZIO;
                    }
                }
                break;
        }
        firstMove = false;
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
