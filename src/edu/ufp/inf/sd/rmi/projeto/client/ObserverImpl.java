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
            else if (Objects.equals(m, "endRound")) {
                MenuHandler.CloseMenu();
                Game.btl.EndTurn();
            }
            else if (Objects.equals(m.substring(0, m.indexOf("-")), "unit")) {
                int type = Integer.parseInt(m.substring(m.indexOf("-") + 1, m.indexOf("x")));
                int x = Integer.parseInt(m.substring(m.indexOf("x") + 1, m.indexOf("y")));
                int y = Integer.parseInt(m.substring(m.indexOf("y") + 1, m.indexOf(".")));
                Game.units.add(Game.list.CreateUnit(type, Game.btl.currentplayer, x, y, false));
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
}
