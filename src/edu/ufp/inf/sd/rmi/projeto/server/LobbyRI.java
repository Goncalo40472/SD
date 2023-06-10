package edu.ufp.inf.sd.rmi.projeto.server;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface LobbyRI extends Remote {

    int getId () throws RemoteException;

    List<ObserverRI> getObservers() throws RemoteException;

    boolean registerObserver(ObserverRI o) throws RemoteException;

    void removeObserver(ObserverRI o) throws RemoteException;

    void notifyObservers(String message) throws RemoteException;

    boolean getGameState() throws RemoteException;

    void setGameState(String s, ObserverRI observer) throws RemoteException;

    boolean isCurrentPlayer(ObserverRI observer) throws RemoteException;

}
