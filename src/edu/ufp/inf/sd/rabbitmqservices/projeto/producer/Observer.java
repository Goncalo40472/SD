package edu.ufp.inf.sd.rabbitmqservices.projeto.producer;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import edu.ufp.inf.sd.rabbitmqservices.projeto.consumer.ObserverGuiClient;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.engine.Game;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.menus.Pause;
import edu.ufp.inf.sd.rabbitmqservices.projeto.game.players.Base;
import edu.ufp.inf.sd.rabbitmqservices.util.RabbitUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rui
 */
public class Observer {

    //Reference for gui
    private final ObserverGuiClient gui;
    private int currentPlayers = 0;
    private int id = -1;
    private String lobby;

    //Preferences for exchange...
    private final Channel channelToRabbitMq;
    private final String exchangeName;
    private final BuiltinExchangeType exchangeType;
    //private final String[] exchangeBindingKeys;
    private final String messageFormat;

    //Store received message to be get by gui
    private String receivedMessage;

    /**
     * @param gui
     */
    public Observer(ObserverGuiClient gui, String host, int port, String user, String pass, String lobby, String exchangeName, BuiltinExchangeType exchangeType, String messageFormat) throws IOException, TimeoutException {
        this.gui=gui;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " going to attach observer to host: " + host + "...");

        Connection connection=RabbitUtils.newConnection2Server(host, port, user, pass);
        this.channelToRabbitMq=RabbitUtils.createChannel2Server(connection);

        this.lobby = lobby;
        this.exchangeName=exchangeName;
        this.exchangeType=exchangeType;
        this.messageFormat=messageFormat;

        bindExchangeToChannelRabbitMQ();
        attachConsumerToChannelExchangeWithKey();
    }

    /**
     * Binds the channel to given exchange name and type.
     */
    private void bindExchangeToChannelRabbitMQ() throws IOException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Declaring Exchange '" + this.exchangeName + "' with type " + this.exchangeType);
        channelToRabbitMq.exchangeDeclare(exchangeName, exchangeType);
    }

    /**
     * Creates a Consumer associated with an unnamed queue.
     */
    public void attachConsumerToChannelExchangeWithKey() {
        try {
            String queueName=channelToRabbitMq.queueDeclare().getQueue();

            String routingKey = lobby;
            channelToRabbitMq.queueBind(queueName, exchangeName, routingKey);

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " Created consumerChannel bound to Exchange " + this.exchangeName + "...");

            /* Use a DeliverCallback lambda function instead of DefaultConsumer to receive messages from queue;
               DeliverCallback is an interface which provides a single method:
                void handle(String tag, Delivery delivery) throws IOException; */
            DeliverCallback deliverCallback=(consumerTag, delivery) -> {
                String message=new String(delivery.getBody(), messageFormat);

                //Store the received message
                setReceivedMessage(message);
                System.out.println(" [x] Consumer Tag [" + consumerTag + "] - Received '" + message + "'");

                if(message.equals("passToken")) {
                    gui.getToken().passToken();
                }

                else if (message.substring(0, message.indexOf("-")).equals("currentPlayers")) {

                    int currentPlayers = Integer.parseInt(message.substring(message.indexOf("-") + 1, message.indexOf(",")));

                    if(this.currentPlayers <= currentPlayers) {
                        this.currentPlayers = currentPlayers;
                        if(this.id == -1) {
                            this.id = currentPlayers - 1;
                            gui.setPlayerId(currentPlayers - 1);
                        }
                    }
                }

                else if(message.substring(0, message.indexOf("-")).equals("Input")) {
                    gui.inputs(message.substring(message.indexOf("-") + 1, message.indexOf(",")));
                }

                else if(message.substring(0, message.indexOf("-")).equals("SmallVs")) {

                    if(currentPlayers == 1) {
                        currentPlayers++;
                        gui.sendMsg("currentPlayers-" + currentPlayers + ",");
                    }
                    else if(currentPlayers == 0) {
                        currentPlayers = 1;
                        gui.sendMsg("currentPlayers-" + currentPlayers + ",");
                    }

                    if(currentPlayers == 2) {
                        gui.sendMsg("Start-SmallVs,");
                    }

                }
                else if(message.substring(0, message.indexOf("-")).equals("FourCorners")) {

                    if(currentPlayers > 0 && currentPlayers < 4) {
                        currentPlayers++;
                        gui.sendMsg("currentPlayers-" + currentPlayers + ",");
                    }
                    else if(currentPlayers == 0) {
                        currentPlayers = 1;
                        gui.sendMsg("currentPlayers-" + currentPlayers + ",");
                    }

                    if(currentPlayers == 4) {
                        gui.sendMsg("Start-FourCorners,");
                    }
                }

                else if(message.substring(0, message.indexOf("-")).equals("Start")) {
                    gui.initGame(message.substring(message.indexOf("-") + 1, message.indexOf(",")));
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

    /**
     * Publish messages to existing exchange instead of the nameless one.
     * - The routingKey is empty ("") since the fanout exchange ignores it.
     * - Messages will be lost if no queue is bound to the exchange yet.
     * - Basic properties can be: MessageProperties.PERSISTENT_TEXT_PLAIN, etc.
     */
    public void sendMessage(String msgToSend) throws IOException {
        String routingKey = this.lobby;
        BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN;

        channelToRabbitMq.basicPublish(exchangeName, routingKey, null,
                msgToSend.getBytes("UTF-8"));
    }

    /**
     * @return the most recent message received from the broker
     */
    public String getReceivedMessage() {
        return receivedMessage;
    }

    /**
     * @param receivedMessage the received message to set
     */
    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage=receivedMessage;
    }
}
