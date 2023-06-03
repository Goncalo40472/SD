package edu.ufp.inf.sd.rmi.projeto.client.game.menus;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;
import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.projeto.server.LobbyImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class Join implements ActionListener{

    public GameSessionRI session;
    public ObserverRI observer;

    public JButton Next = new JButton("Next");

    public JButton Return = new JButton("Return");

    public String mapName;

    //Map list
    public JList lobbies_list = new JList();

    DefaultListModel lobbies_model = new DefaultListModel();

    public Join(String map, GameSessionRI session, ObserverRI observer) throws RemoteException {
        this.mapName = map;
        this.session = session;
        this.observer = observer;
        Point size = MenuHandler.PrepMenu(400,280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddGui();
        AddListeners();
        LobbyList(size);
    }

    private void SetBounds(Point size) {
        Next.setBounds(size.x,size.y+10+38*1, 100, 32);
        Return.setBounds(size.x,size.y+10+38*2, 100, 32);
    }
    private void AddGui() {

        Game.gui.add(Next);
        Game.gui.add(Return);
    }
    private void LobbyList(Point size) throws RemoteException {

        for(String lobby : this.session.getLobbiesNames()){
            if(Objects.equals(lobby.substring(0, lobby.indexOf("#")), mapName)){
                int currentPlayers = session.getLobbyCurrPlayers(lobby);
                int maxPlayers = session.getLobbyMaxPlayers(lobby);
                lobbies_model.addElement(lobby + "     "  + currentPlayers + "/" + maxPlayers);
            }
        }

        JScrollPane maps_pane = new JScrollPane(lobbies_list = new JList<>(lobbies_model));
        maps_pane.setBounds(size.x+220, size.y+10, 140, 260);//220,10
        Game.gui.add(maps_pane);
        lobbies_list.setBounds(0, 0, 140, 260);
        lobbies_list.setSelectedIndex(0);
    }
    private void AddListeners() {
        Next.addActionListener(this);
        Return.addActionListener(this);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s==Next) {
            try {
                String lobby = (String) lobbies_list.getSelectedValue();
                String lobbyName = lobby.substring(0, lobby.indexOf(" "))+"";

                boolean joinLobby = session.joinLobby(lobbyName, observer);

                if(joinLobby && !session.getLobby(lobbyName).getGameState()){
                    new Lobby(lobbyName, session, observer, mapName);
                }
                else if(!joinLobby){
                    MenuHandler.CloseMenu();
                    Game.gui.MainScreen(session, observer);
                }

            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (s == Return) {
            MenuHandler.CloseMenu();
            Game.gui.MainScreen(session, observer);
        }
    }

}
