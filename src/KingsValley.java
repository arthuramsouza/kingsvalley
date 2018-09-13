import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class KingsValley extends UnicastRemoteObject implements IKingsValley {
    private List<Player> players;
    private List<Match> matches;
    private int nextId;

    public static final int MAX_MATCHES = 500;
    public static final int MAX_PLAYERS = MAX_MATCHES * 2;

    public static final int TIMEOUT_SECONDPLAYER = 120;
    public static final int TIMEOUT_ROUND = 60;
    public static final int TIMEOUT_MATCH = 60;
    public static final int TIMEOUT_GARBAGE = 30;

    private final ReadWriteLock uid_readWriteLock = new ReentrantReadWriteLock();
    private final Lock uid_readLock = uid_readWriteLock.readLock();
    private final Lock id_writeLock = uid_readWriteLock.writeLock();

    private final ReadWriteLock players_readWriteLock = new ReentrantReadWriteLock();
    private final Lock players_readLock = players_readWriteLock.readLock();
    private final Lock players_writeLock = players_readWriteLock.writeLock();

    private final ReadWriteLock matches_readWriteLock = new ReentrantReadWriteLock();
    private final Lock matches_readLock = matches_readWriteLock.readLock();
    private final Lock matches_writeLock = matches_readWriteLock.writeLock();


    public KingsValley() throws RemoteException {
        this.players = new ArrayList<Player>();
        this.matches = new ArrayList<Match>();

        this.nextId = 0;
    }

    private Player getPlayerById(int uid) {
        players_readLock.lock();
        try {
            for(Player p : players) {
                if(p.getId() == uid) {
                    return p;
                }
            }
            return null;
        }
        finally {
            players_readLock.unlock();
        }
    }

    // Elimina objetos de jogadores e partidas cujo timeout expirou */
    public void garbageCollector() {
        List<Player> playersDelete;
        List<Match> matchesDelete;

        try {
            while(true) {
                /* Cria lista de players para remocao  */
                playersDelete = new ArrayList<Player>();
                players_readLock.lock();

                try {
                    for(Player p : players) {
                        if(p.hasTimedOut()) {
                            playersDelete.add(p);
                        }
                    }
                }
                finally {
                    players_readLock.unlock();
                }

                /* Cria lista de matches para remocao */
                matchesDelete = new ArrayList<Match>();

                matches_readLock.lock();

                try {
                    for(Match m : matches) {
                        if(m.hasTimedOut()) {
                            System.out.println("[Garbage Collector] Removendo match!");
                            matchesDelete.add(m);
                        }
                    }
                }
                finally {
                    matches_readLock.unlock();
                }

                players_writeLock.lock();

                try {
                    for(Player p :playersDelete) {
                        System.out.println("[Garbage Collector] Excluindo player " + p.getName());
                        players.remove(p);
                    }
                }
                finally {
                    players_writeLock.unlock();
                }

                matches_writeLock.lock();

                try {
                    for(Match m : matchesDelete) {
                        System.out.println("Excluindo partida");
                        matches.remove(m);
                    }
                }
                finally {
                    matches_writeLock.unlock();
                }

                System.out.println("Garbage Collector!");
                Thread.sleep(TIMEOUT_GARBAGE * 1000);
            }
        }
        catch (Exception e) {
            System.err.println("Falha no garbage collector do servidor!");
        }
    }

    /* Registra jogadores no servidor */
    public int registraJogador(String name) throws RemoteException {
        if(matches.size() == MAX_PLAYERS) {
            return -2;
        }

        players_readLock.lock();
        try {
            for(Player p : players) {
                if(p.getName().equalsIgnoreCase(name)) {
                    return -1;
                }
            }
        }
        finally {
            players_readLock.unlock();
        }

        id_writeLock.lock();
        players_writeLock.lock();
        try {
            Player p = new Player(nextId++, name);
            players.add(p);

            System.out.println("Usuario " + p.getName() + " (" + p.getId() + ") entrou!");
            p.updateTimestamp();
            return p.getId();
        }
        finally {
            id_writeLock.unlock();
            players_writeLock.unlock();
        }
    }

    /* Finaliza partida de modo normal */
    public int encerraPartida(int uid) throws RemoteException {
        Player p = getPlayerById(uid);

        if(p == null) {
            return -1;
        }

        Match m = p.getMatch();

        if(m == null) {
            return -1;
        }

        players_writeLock.lock();
        System.out.println("Removendo player " + p.getName());
        try {
            if(p == m.getPlayer1()) {
                m.setPlayer1(null);
            }
            else {
                m.setPlayer2(null);
            }
            players.remove(p);
        }
        finally {
            players_writeLock.unlock();
        }


        matches_writeLock.lock();
        try {
            if(m.canDelete()) {
                System.out.println("Removendo Match");
                matches.remove(m);
            }
        }
        finally {
            matches_writeLock.unlock();
        }

        return 0;
    }


    /* Verifica se tem alguma partida aguardando jogador */
    public int temPartida(int uid) throws RemoteException {
        Player p = getPlayerById(uid);

        if(p == null) {
            return -1;  /* Erro, jogador não registrado */
        }

        Match match = p.getMatch();

        if(match != null) {
            if(match.isReady()) {
                if(match.getPlayer1() == p) {
                    return 1; /* Player 1 */
                }
                else {
                    return 2; /* Player 2 */
                }
            }
            else if(p.hasTimedOut()) {
                return -2;  /* Timeout esperando segundo jogador */
            }
            else {
                return 0;   /* Aguardando segundo jogador */
            }
        }

        /* Verifica se existe alguma partida aguardando um segundo jogador */
        matches_writeLock.lock();
        try {
            for(Match m : matches) {
                if(!m.isReady()) {
                    m.setPlayer2(p);
                    p.setMatch(m);
                    p.updateTimestamp();
                    return 2; /* Player 2 */
                }
            }
        }
        finally {
            matches_writeLock.unlock();
        }

        /* Caso contrário, será criada uma nova partida aguardando um segundo jogador */
        matches_writeLock.lock();
        try {
            Match m = new Match(p);
            matches.add(m);
            p.setMatch(m);
            p.updateTimestamp();
            return 0;
        }
        finally {
            matches_writeLock.unlock();
        }
    }


    /* Verifica se eh a vez do jogador */
    public int ehMinhaVez(int uid) throws RemoteException {

        Player p = getPlayerById(uid);

        if(p == null) {
            return -1;   /* Erro */
        }

        p.updateTimestamp();

        Match m = p.getMatch();

        if(m == null) {
            return -1;  /* Erro */
        }

        if(!m.isReady()) {
            return -2;
        }

        Player oponente = (p == m.getPlayer1()) ? m.getPlayer2() : m.getPlayer1();

        if(oponente == null && m.isReady()) {
            return 5;
        }

        if(oponente.hasTimedOut()) {
            return 5;
        }

        if(p.hasTimedOut()) {
            return 6;
        }

        // Verifica se há um ganhador
        Player p_ganhador = p.getMatch().getGanhador();
        if(p_ganhador != null) {
            if(p_ganhador == p) {
                return 2;
            }
            else {
                return 3;
            }
        }

        if(p.getMatch().getCurrentPlayer() == p) {
            return 1; /* Eh a vez do jogador */
        }
        else {
            return 0; /* A vez do outro jogador */
        }
    }

    /* Retorna o tabuleiro atual */
    public String obtemTabuleiro(int uid) throws RemoteException {
    Player p = getPlayerById(uid);

    if(p == null) {
        return null;  /* Erro */
    }
    Match m = p.getMatch();

    if(m == null) {
        return null; /* Erro */
    }

    char[][] board = m.getBoard();

    return  "\n " + board[0][0] + " | " + board[0][1] + " | " + board[0][2] + " | " + board[0][3] + " | " + board[0][4] + "\n" +
            "---+---+---+---+---\n" +
            " " + board[1][0] + " | " + board[1][1] + " | " + board[1][2] + " | " + board[1][3] + " | " + board[1][4] + "\n" +
            "---+---+---+---+---\n" +
            " " + board[2][0] + " | " + board[2][1] + " | " + board[2][2] + " | " + board[2][3] + " | " + board[2][4] + "\n" +
            "---+---+---+---+---\n" +
            " " + board[3][0] + " | " + board[3][1] + " | " + board[3][2] + " | " + board[3][3] + " | " + board[3][4] + "\n" +
            "---+---+---+---+---\n" +
            " " + board[4][0] + " | " + board[4][1] + " | " + board[4][2] + " | " + board[4][3] + " | " + board[4][4];
    }

    public int movePeca(int id, int linha, int coluna,int sentido) throws RemoteException {

        Player p = getPlayerById(id);
        if(p == null) {
            return -3;      // Parêmetros inválidos
        }

        Match m = p.getMatch();
        if(m == null) {
            return -3;      // Parêmetros inválidos
        }

        if(m.getCurrentPlayer() != p) {
            return -4;      // Não é a vez do jogador
        }

        if(!m.isReady()) {
            return -2;        // Partida não iniciada
        }

        if(m.getCurrentPlayer().hasTimedOut()) {
            return 2;         // Time-out
        }

        return m.movePeca(linha, coluna, sentido);

    }

    /* TODO testar */
    public String obtemOponente(int uid) throws RemoteException {
        Player p = getPlayerById(uid);

        if(p == null) {
            return null;
        }

        Match m = p.getMatch();

        if(m == null) {
            return null;
        }

        if (m.isReady()) {
            if(m.getPlayer1() == p) {
                return m.getPlayer2().getName();
            }
            else {
                return m.getPlayer1().getName();
            }
        }
        return null;
    }

}
