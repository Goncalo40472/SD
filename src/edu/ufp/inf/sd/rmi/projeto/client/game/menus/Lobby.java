package edu.ufp.inf.sd.rmi.projeto.client.game.menus;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;
import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.server.GameSessionImpl;
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

public class Lobby implements ActionListener{

    public GameSessionRI session;
    public ObserverImpl observer;

    public JButton Return = new JButton("Return");

    public JLabel Waiting = new JLabel("Waiting for players...");

    public String lobbyName;
    public String map;

    public Lobby(String lobbyName, GameSessionRI session, ObserverImpl observer, String map) throws RemoteException {
        this.lobbyName = lobbyName;
        this.session = session;
        this.observer = observer;
        this.map = map;

        Point size = MenuHandler.PrepMenu(400,280);
        MenuHandler.HideBackground();
        SetBounds(size);
        AddGui();
        AddListeners();

    }

    private void SetBounds(Point size) {
        Waiting.setForeground(Color.WHITE);
        Waiting.setBounds(size.x,size.y+10+38*1, 300, 32);
        Return.setBounds(size.x,size.y+10+38*2, 100, 32);
    }
    private void AddGui() {
        Game.gui.add(Waiting);
        Game.gui.add(Return);
    }

    private void AddListeners() {
        Return.addActionListener(this);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s == Return) {
            try {
                session.leaveLobby(lobbyName, observer);
                MenuHandler.CloseMenu();
                Game.gui.MainScreen(session, observer);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}