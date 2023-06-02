package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {

    private GameFactoyRI gameFactoyRI;
    private String username;

    private HashMap<String, LobbyImpl> lobbies;

    public GameSessionImpl(GameFactoyRI gameFactoyRI, String username, HashMap<String, LobbyImpl> lobbies) throws RemoteException {
        super();
        this.gameFactoyRI = gameFactoyRI;
        this.username = username;
        this.lobbies = lobbies;
    }

    public void createLobby(){

        

    }

    @Override
    public void logout() throws RemoteException {
        gameFactoyRI.removeSessions(username);
    }
}
