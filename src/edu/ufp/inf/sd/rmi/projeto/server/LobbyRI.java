package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface LobbyRI extends Remote {
    public void print(String msg) throws RemoteException;
}
