import java.rmi.Naming;
import java.util.Scanner;


public class KingsValleyClient {

  public static void main(String[] args) {
    if(args.length < 2) {
      System.out.println("Uso: java KingsValleyClient <servidor> <nome_jogador>");
      System.exit(1);
    }

    try {
      IKingsValley game = (IKingsValley)Naming.lookup("//" + args[0] + "/KingsValley");
      Scanner stdin = new Scanner(System.in);

      /* Registra jogador no servidor remoto */
      int uid = game.registraJogador(args[1]);

      if(uid == -1) {
        System.err.println("Nome de jogador ja em uso!");
        System.exit(1);
      }

      if(uid == -2) {
        System.err.println("Numero maximo de jogadores do servidor excedido!");
        System.exit(1);
      }

      int hasMatch = game.temPartida(uid);

      System.out.println("Procurando partida ...");
      /* Verifica se ha alguma partida ativa */
      while(hasMatch != 1 && hasMatch != 2) {
        if(hasMatch == -2) {
          System.err.println("Tempo de espera esgotado!");
          System.exit(1);
        }

        if(hasMatch == -1) {
          System.err.println("Erro no servidor!");
          System.exit(1);
        }

        /* Verificacao a cada 1 segundo */
        Thread.sleep(1000);
        hasMatch = game.temPartida(uid);
      }

      System.out.println("Segundo jogador " + game.obtemOponente(uid) + " entrou ....");

      /* Verifica se eh a vez deste jogador */
      int isMyTurn = game.ehMinhaVez(uid);
      String message_str = null;

      /* Loop de jogo */
      while(true) {
        if(isMyTurn == -2) {
          System.err.println("Nao existem dois jogadores nesta partida!");
          game.encerraPartida(uid);
          System.exit(1);
        }

        if(isMyTurn == -1) {
          System.err.println("Erro no servidor!");
          game.encerraPartida(uid);
          System.exit(1);
        }

        switch(isMyTurn) {
          case 2 : message_str = "Voce venceu!"; break;
          case 3 : message_str = "Voce perdeu!"; break;
          case 4 : message_str = "Empate!"; break;
          case 5 : message_str = "Voce venceu por WO!"; break;
          case 6 : message_str = "Voce perdeu por WO!"; break;
        }

        /* Final de jogo, exibe resultado e encerra execucao */
        if(isMyTurn > 1 && isMyTurn < 7) {
          System.out.println(message_str);

          if(game.encerraPartida(uid) == -1) {
            System.err.println("Erro ao encerrar jogo!");
            System.exit(1);
          } else {
            System.out.println("Partida finalizada!");
            System.exit(0);
          }
        }

        if(isMyTurn == 1) {
          if(game.hasPutAllPieces(uid)) {
            /* Pode apenas mover as pecas no tabuleiro */
            int ret_movePeca = -1;

            while(ret_movePeca != 1 && ret_movePeca != -2 &&
                 ret_movePeca != -3) {
              System.out.println(game.obtemTabuleiro(uid));

              System.out.print("Informe a posicao da peca para mover: ");
              int posicaoAtual = stdin.nextInt();

              System.out.print("Informe a direcao para mover a peca: ");
              int dirDeslocamento = stdin.nextInt();

              System.out.print("Informe o numero de casas para mover: ");
              int numCasas = stdin.nextInt();

              System.out.print("Informe a orientacao: ");
              int orientacao = stdin.nextInt();

              ret_movePeca = game.movePeca(uid, posicaoAtual, dirDeslocamento,
                                          numCasas, orientacao);

              switch(ret_movePeca) {
                case 2:   System.out.println("Voce perdeu por WO!");
                          game.encerraPartida(uid);
                          System.exit(0);
                case 1:   System.out.println("Jogada concluida com sucesso");
                          System.out.println(game.obtemTabuleiro(uid));
                          break;
                case 0:   System.out.println("Posicao invalida!");
                          break;
                case -1:  System.out.println("Parametros invalidos!");
                          break;
                case -2:  System.err.println("Nao existem dois jogadores nesta partida!");
                          game.encerraPartida(uid);
                          System.exit(1);
                case -3:  System.out.println("Nao eh sua vez de jogar!");
                          break;
              }
            }
          } else {
          /* Tem pecas por adicionar ao tabuleiro */
            int ret_posicionaPeca = -1;

            while(ret_posicionaPeca != 1 && ret_posicionaPeca != -2 &&
                  ret_posicionaPeca != -3) {
              System.out.println(game.obtemTabuleiro(uid));

              System.out.print("Informe a posicao para inserir a peca: ");
              int posicao = stdin.nextInt();

              System.out.print("Informe a orientacao: ");
              int orientacao = stdin.nextInt();
              ret_posicionaPeca = game.posicionaPeca(uid, posicao, orientacao);

              System.out.println("Posiciona peca = " + ret_posicionaPeca);

              switch(ret_posicionaPeca) {
                case 2:   System.out.println("Voce perdeu por WO!");
                          game.encerraPartida(uid);
                          System.exit(0);
                case 1:   System.out.println("Jogada concluida com sucesso");
                          System.out.println(game.obtemTabuleiro(uid));
                          break;
                case 0:   System.out.println("Posicao invalida!");
                          break;
                case -1:  System.out.println("Parametros invalidos!");
                          break;
                case -2:  System.err.println("Nao existem dois jogadores nesta partida!");
                          game.encerraPartida(uid);
                          System.exit(1);
                case -3:  System.out.println("Nao eh sua vez de jogar!");
                          break;
              }
            }
          }
        }
        Thread.sleep(1000);
        isMyTurn = game.ehMinhaVez(uid);
      }

      /* Final normal de jogo */


    } catch (Exception e) {
      System.err.println("KingsValley client failed!");
      System.err.println(e);
    }
  }

}
