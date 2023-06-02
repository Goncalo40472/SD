package edu.ufp.inf.sd.rmi.projeto.client;

import edu.ufp.inf.sd.rmi.projeto.server.State;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    private String id;
    private State lastObserverState;


    public ObserverImpl(String id) throws RemoteException {
        super();
        this.id = id;
    }

    @Override
    public State getLastObserverState() {
        return lastObserverState;
    }

    @Override
    public void update() {

    }
}
