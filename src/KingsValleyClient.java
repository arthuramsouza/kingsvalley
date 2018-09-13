import java.rmi.Naming;
import java.util.Scanner;


public class KingsValleyClient {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java KingsValleyClient <servidor> <nome_jogador>");
            System.exit(1);
        }

        try {
            IKingsValley game = (IKingsValley) Naming.lookup("//" + args[0] + "/KingsValley");
            Scanner stdin = new Scanner(System.in);

            /* Registra jogador no servidor remoto */
            int id = game.registraJogador(args[1]);

            if (id == -1) {
                System.err.println("Nome de jogador já em uso!");
                System.exit(1);
            }

            if (id == -2) {
                System.err.println("Número máximo de jogadores do servidor excedido!");
                System.exit(1);
            }

            int hasMatch = game.temPartida(id);

            System.out.println("Procurando partida ...");

            // Verifica se há alguma partida ativa
            while (hasMatch != 1 && hasMatch != 2) {
                if (hasMatch == -2) {
                    System.err.println("Tempo de espera esgotado!");
                    System.exit(1);
                }

                if (hasMatch == -1) {
                    System.err.println("Erro no servidor!");
                    System.exit(1);
                }

                /* Verificacao a cada 1 segundo */
                Thread.sleep(1000);
                hasMatch = game.temPartida(id);
            }

            System.out.println("Segundo jogador " + game.obtemOponente(id) + " entrou ....");

            /* Verifica se eh a vez deste jogador */
            int isMyTurn = game.ehMinhaVez(id);
            String message_str = null;

            /* Loop de jogo */
            while (true) {
                if (isMyTurn == -2) {
                    System.err.println("Não existem dois jogadores nesta partida!");
                    game.encerraPartida(id);
                    System.exit(1);
                }

                if (isMyTurn == -1) {
                    System.err.println("Erro no servidor!");
                    game.encerraPartida(id);
                    System.exit(1);
                }

                switch (isMyTurn) {
                    case 2:
                        message_str = "Você venceu!";
                        break;
                    case 3:
                        message_str = "Você perdeu!";
                        break;
                    case 4:
                        message_str = "Empate!";
                        break;
                    case 5:
                        message_str = "Você venceu por WO!";
                        break;
                    case 6:
                        message_str = "Você perdeu por WO!";
                        break;
                }

                /* Final de jogo, exibe resultado e encerra execucao */
                if (isMyTurn > 1 && isMyTurn < 7) {
                    System.out.println(message_str);

                    if (game.encerraPartida(id) == -1) {
                        System.err.println("Erro ao encerrar jogo!");
                        System.exit(1);
                    } else {
                        System.out.println("Partida finalizada!");
                        System.exit(0);
                    }
                }
                /* Pode apenas mover as pecas no tabuleiro */
                int ret_movePeca = -1;

                while (ret_movePeca != 1 && ret_movePeca != -2 &&
                        ret_movePeca != -3) {
                    System.out.println(game.obtemTabuleiro(id));

                    System.out.println("Informe a posição da peça a ser movida.");
                    System.out.print("Linha: ");
                    int linha = stdin.nextInt();

                    System.out.print("Coluna: ");
                    int coluna = stdin.nextInt();

                    System.out.print("Informe o sentido do movimento: ");
                    int sentido = stdin.nextInt();

                    ret_movePeca = game.movePeca(id, linha, coluna, sentido);

                    switch (ret_movePeca) {
                        case 2:
                            System.out.println("Voce perdeu!");
                            game.encerraPartida(id);
                            System.exit(0);
                        case 1:
                            System.out.println("Jogada concluída com sucesso");
                            System.out.println(game.obtemTabuleiro(id));
                            break;
                        case 0:
                            System.out.println("Posicão invalida!");
                            break;
                        case -1:
                            System.out.println("Parâmetros inválidos!");
                            break;
                        case -2:
                            System.err.println("Nao existem dois jogadores nesta partida!");
                            game.encerraPartida(id);
                            System.exit(1);
                        case -3:
                            System.out.println("Nao é a sua vez de jogar!");
                            break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("KingsValley client failed!");
            System.err.println(e.toString());
        }
    }
}