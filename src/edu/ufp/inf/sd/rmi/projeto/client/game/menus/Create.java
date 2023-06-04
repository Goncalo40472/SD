package edu.ufp.inf.sd.rmi.projeto.client.game.menus;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverImpl;
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

public class Create implements ActionListener{

    public GameSessionRI session;
    public ObserverImpl observer;

    public JButton Create = new JButton("Create Lobby");
    public JButton Return = new JButton("Return");

    public String mapName;

    //Map list
    public JList maps_list = new JList();

    DefaultListModel maps_model = new DefaultListModel();

    public Create(String map, GameSessionRI session, ObserverImpl observer) throws RemoteException {
        this.mapName = map;
        this.session = session;
        this.observer = observer;
        Point size = MenuHandler.PrepMenu(400,280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddGui();
        AddListeners();
        MapList(size);
    }

    private void SetBounds(Point size) {
        Create.setBounds(size.x,size.y+10+38*1, 100, 32);
        Return.setBounds(size.x,size.y+10+38*2, 100, 32);
    }
    private void AddGui() {

        Game.gui.add(Create);
        Game.gui.add(Return);
    }
    private void MapList(Point size) {
        maps_model = Game.finder.GrabMaps();
        JScrollPane maps_pane = new JScrollPane(maps_list = new JList(maps_model));
        maps_pane.setBounds(size.x+220, size.y+10, 140, 260);//220,10
        Game.gui.add(maps_pane);
        maps_list.setBounds(0, 0, 140, 260);
        maps_list.setSelectedIndex(0);
    }
    private void AddListeners() {
        Create.addActionListener(this);
        Return.addActionListener(this);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s==Create) {
            try {
                String lobbyName = session.createLobby(maps_list.getSelectedValue()+"", observer);
                new Lobby(lobbyName, session, observer, maps_list.getSelectedValue()+"");
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