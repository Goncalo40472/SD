package edu.ufp.inf.sd.rmi.projeto.client;

import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.client.game.menus.MenuHandler;
import edu.ufp.inf.sd.rmi.projeto.client.game.menus.Pause;
import edu.ufp.inf.sd.rmi.projeto.client.game.players.Base;
import edu.ufp.inf.sd.rmi.projeto.server.LobbyRI;
import edu.ufp.inf.sd.rmi.projeto.server.State;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;


public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    private String id;
    private State lastObserverState;

    private Game game;

    private LobbyRI lobby;

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
            else if (Objects.equals(m, "passTurn")) {
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

    }

    @Override
    public LobbyRI getLobby() {
        return lobby;
    }

    @Override
    public void setLobby(LobbyRI lobby) {
        this.lobby = lobby;
    }
}
