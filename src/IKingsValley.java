import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IKingsValley extends Remote {
    int registraJogador(String nome) throws RemoteException;
    int encerraPartida(int id) throws RemoteException;
    int temPartida(int id) throws RemoteException;
    String obtemOponente(int id) throws RemoteException;
    int ehMinhaVez(int id) throws RemoteException;
    String obtemTabuleiro(int id) throws RemoteException;
    int movePeca(int id, int linha, int coluna, int sentido) throws RemoteException;
}
