package edu.ufp.inf.sd.rmi.projeto.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.rabbitmq.client.Channel;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoyRI {

    private final ArrayList<User> users;
    private HashMap<String, GameSessionImpl> sessions;

    private HashMap<String, LobbyImpl> lobbies;

    private ArrayList<LobbyImpl> lobbiesArray;

    private HashMap<User, String> usersTokens;

    public GameFactoryImpl() throws RemoteException {
        super();
        users = new ArrayList();
        users.add(new User("guest", "ufp"));
        this.lobbies = new HashMap<>();
        this.lobbiesArray = new ArrayList<>();
        this.usersTokens = new HashMap<>();
    }

    @Override
    public boolean register(String username, String pwd) throws RemoteException {

        if(exists(username, pwd)) return false;

        users.add(new User(username, pwd));
        return exists(username, pwd);
    }

    @Override
    public GameSessionRI login(String username, String pwd) throws RemoteException {

        if(sessions == null) sessions = new HashMap<>();

        if(exists(username, pwd)){

            User user = getUser(username, pwd);

            if(!usersTokens.containsKey(user)){

                String token = generateToken(username, pwd, new Date(System.currentTimeMillis() + 1800000));
                usersTokens.put(user, token);

                GameSessionImpl session = new GameSessionImpl(this, username, this.lobbies, this.lobbiesArray);
                sessions.put(username, session);
                return session;

            }

            else{

                String token = usersTokens.get(user);

                try {
                    DecodedJWT decodedJWT = JWT.decode(token);
                    Algorithm algorithm = Algorithm.HMAC256("secret");
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    verifier.verify(decodedJWT);
                } catch (JWTVerificationException e) {
                    Date expirationDate = new Date(System.currentTimeMillis() + 1800000);
                    String newToken = generateToken(username, pwd, expirationDate);
                    usersTokens.replace(user, token, newToken);
                }

                GameSessionImpl session = new GameSessionImpl(this, username, this.lobbies, this.lobbiesArray);
                sessions.put(username, session);
                return session;

            }

        } else if (exists(username, pwd) && sessions.containsKey(username)) {

            return sessions.get(username);

        }

        return null;

    }

    @Override
    public String generateToken(String username, String pwd, Date expirationDate) throws RemoteException {
        String token = JWT.create()
                .withClaim("username", username)
                .withClaim("password", pwd)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256("secret"));
        return token;
    }

    @Override
    public void removeSessions(String username) throws RemoteException {
        sessions.remove(username);
    }

    public boolean exists(String u, String p) {
        for (User usr : users) {
            if (Objects.equals(usr.getUname(), u) && Objects.equals(usr.getPword(), p)) {
                return true;
            }
        }
        return false;
    }

    public User getUser(String username, String pwd) {

        for (User user : users) {

            if(Objects.equals(user.getUname(), username) && Objects.equals(user.getPword(), pwd)) {

                return user;

            }
        }

        return null;

    }
}
