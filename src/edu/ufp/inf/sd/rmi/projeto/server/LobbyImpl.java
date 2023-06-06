package edu.ufp.inf.sd.rmi.projeto.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class LobbyImpl extends UnicastRemoteObject implements LobbyRI {

    private String mapName;
    private int id;
    private int maxPlayers;
    private int currentPlayers;
    private List<ObserverRI> observers;
    private String name;

    private String state;

    private boolean gameState = false;

    private TokenRing token;

    private Channel channel;

    public LobbyImpl(String mapName, int id) throws RemoteException {
        super();
        this.mapName = mapName;
        this.id = id;
        this.name = mapName + "#" + this.id;

        if(Objects.equals(mapName, "FourCorners")){
            maxPlayers = 4;
        } else if (Objects.equals(mapName, "SmallVs")) {
            maxPlayers = 2;
        }

        this.currentPlayers = 0;

        this.observers = Collections.synchronizedList(new ArrayList<>());

        this.token = new TokenRing(maxPlayers);

    }

    public LobbyImpl(String mapName, int id, Channel channel) throws RemoteException {
        super();
        this.mapName = mapName;
        this.id = id;
        this.name = mapName + "#" + this.id;

        if(Objects.equals(mapName, "FourCorners")){
            maxPlayers = 4;
        } else if (Objects.equals(mapName, "SmallVs")) {
            maxPlayers = 2;
        }

        this.currentPlayers = 0;

        this.observers = Collections.synchronizedList(new ArrayList<>());

        this.token = new TokenRing(maxPlayers);

        this.channel = channel;

    }

    public String getMapName() {
        return mapName;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getId() throws RemoteException {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public List<ObserverRI> getObservers() {
        return observers;
    }

    @Override
    public boolean registerObserver(ObserverRI observer) throws RemoteException {
        if(currentPlayers < maxPlayers && !this.observers.contains(observer)){
            getObservers().add(observer);
            currentPlayers++;

            if(currentPlayers == maxPlayers) {
                gameState = true;
                for(ObserverRI o : observers) {
                    o.startGame(mapName);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void removeObserver(ObserverRI observer) throws RemoteException {
        this.getObservers().remove(observer);
        this.currentPlayers--;
    }

    @Override
    public void notifyObservers(String message) throws RemoteException{
        for (ObserverRI observer: this.getObservers()) {
            observer.update(message);
        }
    }

    @Override
    public boolean getGameState() throws RemoteException {
        return gameState;
    }

    @Override
    public void setGameState(String s, ObserverRI observer) throws RemoteException {
        if(token.getHolder() == this.observers.indexOf(observer)) {
            state = s;
            notifyObservers(state);
            if (Objects.equals(this.state, "endRound")){

                token.passToken();

            }
        }
    }

    public void createQueues() throws RemoteException {

        try{
            channel.exchangeDeclare("lobbyFanout-" + this.getId(), "fanout");
            channel.queueDeclare("lobbyQueue-" + this.getId(), false, false, false, null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void listenQueues() throws IOException {

        DeliverCallback deliver = (consumerTag, delivery) -> {

            String[] args = new String(delivery.getBody(), "UTF-8").split(",");

            int obs = Integer.parseInt(args[0]);
            String msg = args[1];

            if (this.token.getHolder() == obs) { // check to see if message comes from token holder
                // send command to all clients
                this.channel.basicPublish("lobbyFanout-" + this.getId(), "", null, msg.getBytes("UTF-8"));

                if (msg.compareTo("endRound") == 0) {
                    this.token.passToken();
                }
            }
        };
        this.channel.basicConsume("lobbyQueue-" + this.getId(), true, deliver, consumerTag -> { });

    }

}
