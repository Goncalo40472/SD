package edu.ufp.inf.sd.rmi.projeto.client.game.menus;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

import edu.ufp.inf.sd.rmi.projeto.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.projeto.client.ObserverRI;
import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.server.GameSessionRI;

/**
 * This is the opening menu of the game.
 * @author SergeDavid
 * @version 0.2
 */
public class MainMenu implements ActionListener {

    public GameSessionRI session;
    public ObserverImpl observer;

    //Online
    public JButton Create = new JButton("Create Lobby");
    public JButton Join = new JButton("Join Game");

    //Other
    public JButton Options = new JButton("Options");
    public JButton Exit = new JButton("Exit");

    //Map list
    public JList maps_list = new JList();
    DefaultListModel maps_model = new DefaultListModel();

    public MainMenu(GameSessionRI session, ObserverImpl observer) {
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
        Join.setBounds(size.x,size.y+10+38, 100, 32);
        Create.setBounds(size.x,size.y+10+38*2, 100, 32);
        Options.setBounds(size.x,size.y+10+38*3, 100, 32);
        Exit.setBounds(size.x,size.y+10+38*4, 100, 32);
    }
    private void AddGui() {
        Game.gui.add(Join);
        Game.gui.add(Create);
        Game.gui.add(Options);
        Game.gui.add(Exit);
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
        Join.addActionListener(this);
        Create.addActionListener(this);
        Options.addActionListener(this);
        Exit.addActionListener(this);
    }

    @Override public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s==Join) {
            try {
                new Join(maps_list.getSelectedValue()+"", this.session, this.observer);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (s==Create) {
            try {
                new Create(maps_list.getSelectedValue()+"", this.session, this.observer);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (s==Options) {new Options();}
        else if (s==Exit) {System.exit(0);}
    }
}

