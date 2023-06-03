package edu.ufp.inf.sd.rmi.projeto.server;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;


public interface GameSessionRI extends Remote {
    void leaveLobby(String l, ObserverRI o) throws RemoteException;

    void joinLobby(String l, ObserverRI o) throws RemoteException;

    HashMap<String, LobbyImpl> getLobbies() throws RemoteException;

    ArrayList<LobbyImpl> getArrayLobbies() throws RemoteException;

    ArrayList<String> getLobbiesNames() throws RemoteException;

    int getLobbyMaxPlayers(String lobbyName) throws RemoteException;

    int getLobbyCurrPlayers(String lobbyName) throws RemoteException;

    public void logout() throws RemoteException;

    public String createLobby(String mapName, ObserverRI o) throws RemoteException;
}