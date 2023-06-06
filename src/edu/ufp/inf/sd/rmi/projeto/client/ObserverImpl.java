package edu.ufp.inf.sd.rmi.projeto.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.client.game.menus.MenuHandler;
import edu.ufp.inf.sd.rmi.projeto.client.game.menus.Pause;
import edu.ufp.inf.sd.rmi.projeto.client.game.players.Base;
import edu.ufp.inf.sd.rmi.projeto.server.LobbyRI;
import edu.ufp.inf.sd.rmi.projeto.server.State;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.concurrent.TimeoutException;


public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    private String id;
    private State lastObserverState;
    private LobbyRI lobby;
    private Connection connection;
    private Channel channel;

    public ObserverImpl(String id) throws RemoteException {
        super();
        this.id = id;
        lastObserverState = new State(id, "waiting");
    }

    @Override
    public State getLastObserverState() throws RemoteException {
        return lastObserverState;
    }

    @Override
    public void update(String m) throws RemoteException {

        if (Game.GameState==Game.State.PLAYING) {

            Base ply = Game.player.get(Game.btl.currentplayer);

            if (Objects.equals(m, "up")) {
                ply.selecty--;
                if (ply.selecty<0) {
                    ply.selecty++;
                }
            }
            else if (Objects.equals(m, "down")) {
                ply.selecty++;
                if (ply.selecty>=Game.map.height) {
                    ply.selecty--;
                }
            }
            else if (Objects.equals(m, "left")) {
                ply.selectx--;
                if (ply.selectx<0) {
                    ply.selectx++;
                }
            }
            else if (Objects.equals(m, "right")) {
                ply.selectx++;
                if (ply.selectx>=Game.map.width) {
                    ply.selectx--;
                }
            }
            else if (Objects.equals(m, "select")) {
                Game.btl.Action();
            }
            else if (Objects.equals(m, "cancel")) {
                Game.player.get(Game.btl.currentplayer).Cancle();
            }
            else if (Objects.equals(m, "start")) {
                new Pause();
            }
            else if (Objects.equals(m, "endRound")) {
                MenuHandler.CloseMenu();
                Game.btl.EndTurn();
            }
        }
    }

    @Override
    public void startGame(String map) throws RemoteException {

        boolean[] npc = {false,false,false,false};
        int[] plyer = {0,1,2,3};

        MenuHandler.CloseMenu();
        Game.btl.NewGame(map);
        Game.btl.AddCommanders(plyer, npc, 100, 50);
        Game.gui.InGameScreen();

        bindQueue();

        if(Game.gameFactoyRI.channelExists()){
            listenQueue();
        }

    }

    public void listenQueue() {

        try{

            Channel channel = this.connection.createChannel();

            channel.exchangeDeclare("lobbyFanout-" + lobby.getId(), "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "lobbyFanout-" + lobby.getId(), "");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                this.rabbitUpdate(message);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void bindQueue() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            this.connection = factory.newConnection();
            this.channel = this.connection.createChannel();
            getChannel().queueDeclare("lobbyQueue-" + lobby.getId(), false, false, false, null);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void rabbitUpdate(String m) {

        if (Game.GameState == Game.State.PLAYING) {

            Base ply = Game.player.get(Game.btl.currentplayer);

            if (Objects.equals(m, "up")) {
                ply.selecty--;
                if (ply.selecty<0) {
                    ply.selecty++;
                }
            }
            else if (Objects.equals(m, "down")) {
                ply.selecty++;
                if (ply.selecty>=Game.map.height) {
                    ply.selecty--;
                }
            }
            else if (Objects.equals(m, "left")) {
                ply.selectx--;
                if (ply.selectx<0) {
                    ply.selectx++;
                }
            }
            else if (Objects.equals(m, "right")) {
                ply.selectx++;
                if (ply.selectx>=Game.map.width) {
                    ply.selectx--;
                }
            }
            else if (Objects.equals(m, "select")) {
                Game.btl.Action();
            }
            else if (Objects.equals(m, "cancel")) {
                Game.player.get(Game.btl.currentplayer).Cancle();
            }
            else if (Objects.equals(m, "start")) {
                new Pause();
            }
            else if (Objects.equals(m, "endRound")) {
                MenuHandler.CloseMenu();
                Game.btl.EndTurn();
            }
        }

    }

    @Override
    public LobbyRI getLobby() throws RemoteException {
        return lobby;
    }

    @Override
    public void setLobby(LobbyRI lobby) throws RemoteException {
        this.lobby = lobby;
    }

    @Override
    public String getID() throws RemoteException {
        return id;
    }

    @Override
    public Channel getChannel() throws RemoteException{
        return channel;
    }
}
