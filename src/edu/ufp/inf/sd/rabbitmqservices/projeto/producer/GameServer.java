package edu.ufp.inf.sd.rabbitmqservices.projeto.producer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameServer {
    private transient Connection connection;
    private transient Channel channel;

    /*+ name of the queue */
    //public final static String QUEUE_NAME="hello_queue";
    public GameServer() throws IOException, TimeoutException {
        this.gameExchange();

    }

    public void gameExchange() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.connection = factory.newConnection();
        this.channel = this.connection.createChannel();
        channel.queueDeclare("serverQueues", false, false, false, null);

        DeliverCallback deliverCallbackTopic = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "MESSAGE RECEIVED:" + message);
            this.gamePublish(message);
        };
        this.channel.basicConsume("serverQueues", true, deliverCallbackTopic, consumerTag -> {
        });
    }

    public void gamePublish(String message) throws IOException {

        String routeKey = "routeKey";
        this.channel.basicPublish("gameExchanger", routeKey, null, message.getBytes(StandardCharsets.UTF_8));

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new GameServer();
    }
}
