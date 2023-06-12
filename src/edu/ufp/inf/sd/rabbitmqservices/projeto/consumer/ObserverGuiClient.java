/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2011</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rabbitmqservices.projeto.consumer;

import com.rabbitmq.client.BuiltinExchangeType;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.engine.Game;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.menus.Pause;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.players.Base;
import edu.ufp.inf.sd.rabbitmqservices.projeto.producer.Observer;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.menus.MenuHandler;
import edu.ufp.inf.sd.rabbitmqservices.projeto.producer.TokenRing;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rjm
 */
public class ObserverGuiClient {

    private Observer observer;
    private TokenRing token;
    private int playerId = -1;

    private int numOpenGames = 0;

    /**
     * Creates new form ChatClientFrame
     *
     * @param args
     */
    public ObserverGuiClient(String args[]) {
        try {
            RabbitUtils.printArgs(args);

            //Read args passed via shell command
            String host=args[0];
            int port=Integer.parseInt(args[1]);
            String exchangeName=args[2];
            String lobby = args[3];

            this.observer= new Observer(this, host, port, "guest", "guest", lobby, exchangeName, BuiltinExchangeType.TOPIC, "UTF-8");
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " After initObserver()...");

            sendMsg(lobby);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    //================================================ BEGIN TO CHANGE ================================================

    /**
     * This method will be called by the _05_observer, to notify/update the GUI,
     * whenever a new msg is received/consumed from the broker.
     */
    public void initGame(String map) {
        if(numOpenGames == 0){
            if(Objects.equals(map, "SmallVs")) {
                token = new TokenRing(2);
            } else if (Objects.equals(map, "FourCorners")) {
                token = new TokenRing(4);
            }

            ObserverGuiClient me=this;
            new Thread(() -> new Game(map, me)).start();

            numOpenGames++;
        }
    }

    public TokenRing getToken() {
        return token;
    }

    public int getPlayerId(){
        return playerId;
    }

    public void inputs(String i){

        Base ply = Game.player.get(Game.btl.currentplayer);

        if (Objects.equals(i, "up")) {
            ply.selecty--;
            if (ply.selecty<0) {
                ply.selecty++;
            }
        }
        else if (Objects.equals(i, "down")) {
            ply.selecty++;
            if (ply.selecty>=Game.map.height) {
                ply.selecty--;
            }
        }
        else if (Objects.equals(i, "left")) {
            ply.selectx--;
            if (ply.selectx<0) {
                ply.selectx++;
            }
        }
        else if (Objects.equals(i, "right")) {
            ply.selectx++;
            if (ply.selectx>=Game.map.width) {
                ply.selectx--;
            }
        }
        else if (Objects.equals(i, "select")) {
            Game.btl.Action();
        }
        else if (Objects.equals(i, "cancel")) {
            Game.player.get(Game.btl.currentplayer).Cancle();
        }
        else if (Objects.equals(i, "start")) {
            new Pause();
        }
        else if (Objects.equals(i, "endRound")) {
            if(this.playerId == token.getHolder()){
                sendMsg("passToken");
            }
            MenuHandler.CloseMenu();
            Game.btl.EndTurn();
        }
        else if (Objects.equals(i.substring(i.indexOf("-") + 1, i.indexOf("!")), "unit")) {
            System.out.println(i);
            int type = Integer.parseInt(i.substring(i.indexOf("!") + 1, i.indexOf("x")));
            int x = Integer.parseInt(i.substring(i.indexOf("x") + 1, i.indexOf("y")));
            int y = Integer.parseInt(i.substring(i.indexOf("y") + 1));
            Game.units.add(Game.list.CreateUnit(type, Game.btl.currentplayer, x, y, false));
        }

    }

    public void setPlayerId(int id) {
        this.playerId = id;
        System.out.println(playerId);
    }

    /**
     * Sends msg through the _05_observer to the exchange where all observers are binded
     *
     * @param msgToSend
     */
    public void sendMsg(String msgToSend) {
        try {
            this.observer.sendMessage(msgToSend);
        } catch (IOException ex) {
            Logger.getLogger(ObserverGuiClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //================================================ END TO CHANGE ================================================

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int expectedArgs = 4;
                if (args.length >= expectedArgs) {
                    new edu.ufp.inf.sd.rabbitmqservices.projeto.consumer.ObserverGuiClient(args);
                } else {
                    Logger.getLogger(edu.ufp.inf.sd.rabbitmqservices.projeto.consumer.ObserverGuiClient.class.getName()).log(Level.INFO, "check args.length < "+expectedArgs+"!!!" );
                }
            }
        });
    }
}
