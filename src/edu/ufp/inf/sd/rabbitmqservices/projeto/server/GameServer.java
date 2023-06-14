package edu.ufp.inf.sd.rabbitmqservices.projeto.server;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.projeto.client.GameClient;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {

    private final GameClient client;
    private int currentPlayers = 0;
    private int count = 0;
    private final String lobby;
    private boolean gameFull = false;

    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    private final String messageFormat;

    public GameServer(GameClient client, String host, int port, String user, String pass, String lobby, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {
        this.client=client;

        Connection connection=RabbitUtils.newConnection2Server(host, port, user, pass);
        this.channelToRabbitMq=RabbitUtils.createChannel2Server(connection);

        this.lobby = lobby;
        this.exchangeName=exchangeName;
        this.exchangeType=exchangeType;
        this.messageFormat=messageFormat;

        bindExchangeToChannelRabbitMQ();
        attachConsumerToChannelExchangeWithKey();
    }

    private void bindExchangeToChannelRabbitMQ() throws IOException {
        channelToRabbitMq.exchangeDeclare(exchangeName, exchangeType);
    }

    public void attachConsumerToChannelExchangeWithKey() {
        try {
            String queueName=channelToRabbitMq.queueDeclare().getQueue();

            channelToRabbitMq.queueBind(queueName, exchangeName, lobby);

            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message=new String(delivery.getBody(), messageFormat);

                if(message.equals("passToken")) {
                    client.getToken().passToken();
                }

                else if (message.equals("GameFull")) {

                    if(!gameFull){
                        String queue=channelToRabbitMq.queueDeclare().getQueue();
                        channelToRabbitMq.queueUnbind(queue, exchangeName, lobby);
                        try {
                            channelToRabbitMq.close();
                        } catch (TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("GAME ALREADY FULL TRY OTHER LOBBY!");
                        client.endProcess();

                    }
                }

                else if (message.substring(0, message.indexOf("-")).equals("SetGameFull")) {

                    gameFull = true;
                    client.sendMsg("Start-" + message.substring(message.indexOf("-") + 1));

                }

                else if (message.substring(0, message.indexOf("-")).equals("currentPlayers")) {
                    count++;
                }

                else if(message.substring(0, message.indexOf("-")).equals("Input")) {
                    client.inputs(message.substring(message.indexOf("-") + 1));
                }

                else if(message.substring(0, message.indexOf("-")).equals("SmallVs")) {

                    if(currentPlayers == 1) {
                        currentPlayers++;
                        client.sendMsg("currentPlayers-" + currentPlayers);
                    }
                    else if(currentPlayers == 0) {
                        currentPlayers = 1;
                        client.sendMsg("currentPlayers-" + currentPlayers);
                    }

                    if(currentPlayers == 2 && !gameFull) {
                        client.sendMsg("SetGameFull-SmallVs");
                    } else if (currentPlayers == 2 && gameFull) {
                        client.sendMsg("GameFull");
                    }

                }
                else if(message.substring(0, message.indexOf("-")).equals("FourCorners")) {

                    if(currentPlayers > 0 && currentPlayers < 4) {
                        currentPlayers++;
                        client.sendMsg("currentPlayers-" + currentPlayers);
                    }
                    else if(currentPlayers == 0) {
                        currentPlayers = 1;
                        client.sendMsg("currentPlayers-" + currentPlayers);
                    }

                    if(currentPlayers == 4 && !gameFull) {
                        client.sendMsg("SetGameFull-FourCorners");
                    } else if (currentPlayers == 4 && gameFull) {
                        client.sendMsg("GameFull");
                    }
                }

                else if(message.substring(0, message.indexOf("-")).equals("Start") && gameFull) {

                    String map = message.substring(message.indexOf("-") + 1);

                    if(map.equals("SmallVs")) {
                        client.setPlayerId(count - 2);
                    }
                    else if(map.equals("FourCorners")) {
                        if(count == 4) {
                            client.setPlayerId(0);
                        }
                        else if(count == 7) {
                            client.setPlayerId(1);
                        }
                        else if(count == 9) {
                            client.setPlayerId(2);
                        }
                        else if(count == 10) {
                            client.setPlayerId(3);
                        }
                    }

                    client.setGameRunning();
                    client.initGame(map);
                }

            };
            CancelCallback cancelCallback=consumerTag -> {
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Cancel Callback invoked!");
            };

            channelToRabbitMq.basicConsume(queueName, true, deliverCallback, cancelCallback);

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString());
        }
    }

    public void sendMessage(String msgToSend) throws IOException {
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;

        channelToRabbitMq.basicPublish(exchangeName, this.lobby, prop,
                msgToSend.getBytes("UTF-8"));
    }

}
