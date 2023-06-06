package edu.ufp.inf.sd.rmi.projeto.server;

import com.rabbitmq.client.Channel;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {

    private GameFactoyRI gameFactoyRI;
    private String username;

    private HashMap<String, LobbyImpl> lobbies;

    private ArrayList<LobbyImpl> lobbiesArray;

    private Channel channel;

    public GameSessionImpl(GameFactoyRI gameFactoyRI, String username, HashMap<String, LobbyImpl> lobbies, ArrayList<LobbyImpl> lobbiesArray) throws RemoteException {
        super();
        this.gameFactoyRI = gameFactoyRI;
        this.username = username;
        this.lobbies = lobbies;
        this.lobbiesArray =  lobbiesArray;
    }

    public GameSessionImpl(GameFactoyRI gameFactoyRI, String username, HashMap<String, LobbyImpl> lobbies, ArrayList<LobbyImpl> lobbiesArray, Channel channel) throws RemoteException {
        super();
        this.gameFactoyRI = gameFactoyRI;
        this.username = username;
        this.lobbies = lobbies;
        this.lobbiesArray =  lobbiesArray;
        this.channel = channel;
    }

    @Override
    public String createLobby(String mapName, ObserverRI observer) throws RemoteException{

        LobbyImpl lobby;

        if(this.channel != null) {
            lobby = new LobbyImpl(mapName, this.lobbies.size(), channel);
        }
        else{
            lobby = new LobbyImpl(mapName, this.lobbies.size());
        }

        LobbyRI lobbyRI = lobby;

        String lobbyName = mapName + "#" + lobby.getId();
        lobbies.put(lobbyName, lobby);
        lobbiesArray.add(lobby);
        lobby.registerObserver(observer);
        observer.setLobby(lobbyRI);
        return lobbyName;
    }

    @Override
    public void leaveLobby(String l, ObserverRI observer) throws RemoteException{
        LobbyImpl lobby = lobbies.get(l);
        lobby.removeObserver(observer);
        if(lobby.getObservers().size() == 0){
            lobbiesArray.remove(lobby);
            lobbies.remove(l);
        }
    }

    @Override
    public boolean joinLobby(String lobbyName, ObserverRI observer) throws RemoteException {

        boolean observerState = lobbies.get(lobbyName).registerObserver(observer);

        if(observerState) {
            observer.setLobby(lobbies.get(lobbyName));
            return true;
        }

        return false;
    }

    @Override
    public HashMap<String, LobbyImpl> getLobbies() throws RemoteException{
        return lobbies;
    }

    @Override
    public ArrayList<LobbyImpl> getArrayLobbies() throws RemoteException{
        return lobbiesArray;
    }

    @Override
    public ArrayList<String> getLobbiesNames() throws RemoteException{
        ArrayList<String> lobbies = new ArrayList<>();
        String str;
        for(LobbyImpl lobby : this.getArrayLobbies()){
            str = lobby.getMapName() + "#" + lobby.getId();
            lobbies.add(str);
        }
        return lobbies;
    }

    @Override
    public int getLobbyMaxPlayers(String lobbyName) throws RemoteException{
        return lobbies.get(lobbyName).getMaxPlayers();
    }

    @Override
    public int getLobbyCurrPlayers(String lobbyName) throws RemoteException{
        return lobbies.get(lobbyName).getCurrentPlayers();
    }

    @Override
    public LobbyImpl getLobby(String lobbyName) throws RemoteException {
        return lobbies.get(lobbyName);
    }

    @Override
    public Channel getChannel() throws RemoteException {
        return channel;
    }

    @Override
    public boolean channelExists() throws RemoteException {
        return this.channel != null;
    }

    @Override
    public void logout() throws RemoteException {
        gameFactoyRI.removeSessions(username);
    }
}
