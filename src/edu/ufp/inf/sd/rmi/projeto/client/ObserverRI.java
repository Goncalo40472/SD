package edu.ufp.inf.sd.rmi.projeto.client;

import edu.ufp.inf.sd.rmi.projeto.server.State;

import java.rmi.Remote;


public interface ObserverRI extends Remote {

    public State getLastObserverState();

    public void update();
}
