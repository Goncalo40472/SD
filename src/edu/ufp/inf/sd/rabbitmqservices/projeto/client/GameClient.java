package edu.ufp.inf.sd.rabbitmqservices.projeto.client;

import com.rabbitmq.client.BuiltinExchangeType;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.engine.Game;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.menus.Pause;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.players.Base;
import edu.ufp.inf.sd.rabbitmqservices.projeto.server.GameServer;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.menus.MenuHandler;
import edu.ufp.inf.sd.rabbitmqservices.projeto.server.TokenRing;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameClient {

    private GameServer gameServer;
    private TokenRing token;
    private int playerId = -1;

    private int numOpenGames = 0;
    private boolean gameRunning = false;

    public GameClient(String args[]) {
        try {
            String host=args[0];
            int port=Integer.parseInt(args[1]);
            String exchangeName=args[2];
            String lobby = args[3];

            this.gameServer = new GameServer(this, host, port, "guest", "guest", lobby, exchangeName, BuiltinExchangeType.TOPIC, "UTF-8");

            sendMsg(lobby);

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    //================================================ BEGIN TO CHANGE ================================================

    public void initGame(String map) {
        if(numOpenGames == 0 && gameRunning){
            if(Objects.equals(map, "SmallVs")) {
                token = new TokenRing(2);
            } else if (Objects.equals(map, "FourCorners")) {
                token = new TokenRing(4);
            }

            GameClient me=this;
            new Thread(() -> new Game(map, me)).start();

            numOpenGames++;
        }
    }

    public TokenRing getToken() {
        return token;
    }

    public void setGameRunning(){gameRunning = true;}

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
            int type = Integer.parseInt(i.substring(i.indexOf("!") + 1, i.indexOf("x")));
            int x = Integer.parseInt(i.substring(i.indexOf("x") + 1, i.indexOf("y")));
            int y = Integer.parseInt(i.substring(i.indexOf("y") + 1));
            Game.units.add(Game.list.CreateUnit(type, Game.btl.currentplayer, x, y, false));
        }

    }

    public void setPlayerId(int id) {
        this.playerId = id;
    }

    public void sendMsg(String msgToSend) {
        try {
            this.gameServer.sendMessage(msgToSend);
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endProcess() {
        Runtime.getRuntime().exit(0);
    }

    //================================================ END TO CHANGE ================================================

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int expectedArgs = 4;
                if (args.length >= expectedArgs) {
                    new GameClient(args);
                } else {
                    Logger.getLogger(GameClient.class.getName()).log(Level.INFO, "check args.length < "+expectedArgs+"!!!" );
                }
            }
        });
    }
}
