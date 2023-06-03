package edu.ufp.inf.sd.rmi.projeto.client;

import edu.ufp.inf.sd.rmi.projeto.server.State;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ObserverRI extends Remote {

    public State getLastObserverState() throws RemoteException;

    public void update(String m) throws RemoteException;
}
