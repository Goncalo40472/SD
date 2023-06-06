package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;


public interface GameFactoyRI extends Remote {
    public boolean register(String username, String pwd) throws RemoteException;

    public GameSessionRI login(String username, String pwd) throws RemoteException;

    String generateToken(String username, String password, Date expirationDate) throws RemoteException;

    public void removeSessions(String username) throws RemoteException;

    boolean channelExists() throws RemoteException;
}
