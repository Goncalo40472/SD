package edu.ufp.inf.sd.rabbitmqservices.projeto.server;

public class TokenRing {

    private int numPlayers;
    private int holder;

    public TokenRing(int numberPlayers) {
        this.numPlayers = numberPlayers;
        this.holder = 0;
    }

    public void passToken() {
        this.holder++;
        if (this.holder >= this.numPlayers) {
            this.holder = 0;
        }

    }

    public int getHolder() {
        return this.holder;
    }

}
