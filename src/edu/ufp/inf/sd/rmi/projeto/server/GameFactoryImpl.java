package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoyRI {

    private edu.ufp.inf.sd.rmi.projeto.server.DBMockup db;
    private HashMap<String, GameSessionImpl> sessions;

    private HashMap<String, LobbyImpl> lobbies;

    public GameFactoryImpl() throws RemoteException {
        super();
        db = new edu.ufp.inf.sd.rmi.projeto.server.DBMockup();
    }

    @Override
    public boolean register(String username, String pwd) throws RemoteException {

        if(getDB().exists(username, pwd)) return false;

        getDB().register(username, pwd);
        return getDB().exists(username, pwd);
    }

    @Override
    public GameSessionRI login(String username, String pwd) throws RemoteException {

        if(sessions == null) sessions = new HashMap<>();

        if(getDB().exists(username, pwd) && !sessions.containsKey(username)){

            this.lobbies = new HashMap<>();
            GameSessionImpl session = new GameSessionImpl(this, username, this.lobbies);
            sessions.put(username, session);
            return session;

        } else if (getDB().exists(username, pwd) && sessions.containsKey(username)) {

            return sessions.get(username);

        }

        return null;

    }

    @Override
    public DBMockup getDB() {
        return db;
    }

    @Override
    public void removeSessions(String username) throws RemoteException {
        sessions.remove(username);
    }
}
