package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GameFactoyRI extends Remote {
    public boolean register(String username, String pwd) throws RemoteException;

    public GameSessionRI login(String username, String pwd) throws RemoteException;

    public DBMockup getDB() throws RemoteException;

    public void removeSessions(String username) throws RemoteException;
}
