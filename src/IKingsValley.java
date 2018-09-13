import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IKingsValley extends Remote {
  public int registraJogador(String nome) throws RemoteException;
  public int encerraPartida(int uid) throws RemoteException;
  public int temPartida(int uid) throws RemoteException;
  public String obtemOponente(int uid) throws RemoteException;
  public int ehMinhaVez(int uid) throws RemoteException;
  public String obtemTabuleiro(int uid) throws RemoteException;
  public int movePeca(int id, int linha, int coluna, int sentido) throws RemoteException;
}
