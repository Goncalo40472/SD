package edu.ufp.inf.sd.rmi.projeto.server;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface LobbyRI extends Remote {

    boolean registerObserver(ObserverRI o) throws RemoteException;

    void removeObserver(ObserverRI o) throws RemoteException;

    void notifyObservers(String message) throws RemoteException;

    boolean getGameState() throws RemoteException;

    void setGameState(String s, ObserverRI observer) throws RemoteException;
}
