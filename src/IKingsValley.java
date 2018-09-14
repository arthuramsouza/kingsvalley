import java.rmi.Remote;

public interface IKingsValley extends Remote {
    int registraJogador(String nome);
    int encerraPartida(int id);
    int temPartida(int id);
    String obtemOponente(int id);
    int ehMinhaVez(int id);
    String obtemTabuleiro(int id);
    int movePeca(int id, int linha, int coluna, int sentido);
}
