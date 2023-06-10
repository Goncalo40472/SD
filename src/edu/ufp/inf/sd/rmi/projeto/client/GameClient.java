package edu.ufp.inf.sd.rmi.projeto.client;

import edu.ufp.inf.sd.rmi.projeto.client.game.engine.Game;
import edu.ufp.inf.sd.rmi.projeto.server.GameFactoyRI;
import edu.ufp.inf.sd.rmi.projeto.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class GameClient {

    /**
     * Context for connecting a RMI client MAIL_TO_ADDR a RMI Servant
     */
    private SetupContextRMI contextRMI;
    /**
     * Remote interface that will hold the Servant proxy
     */
    private GameFactoyRI gameFactoyRI;
    private GameSessionRI gameSessionRI;
    private ObserverImpl observer;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.PongClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            //1. ============ Setup client RMI context ============
            GameClient hwc=new GameClient(args);
            //2. ============ Lookup service ============
            hwc.lookupService();
            //3. ============ Play with service ============
            hwc.playService();
        }
    }

    public GameClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);
                
                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                gameFactoyRI = (GameFactoyRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return gameFactoyRI;
    }
    
    private void playService() {
        try {
            String username, pwd;


            Scanner scan = new Scanner(System.in);
            System.out.println("\n\n1- Registar\n2- Login\n\nEscolha um opção: ");
            int option = scan.nextInt();

            if(option == 1) {

                System.out.println("Insira o seu nome de utilizador: ");
                username = scan.next();
                System.out.println("Insira a sua palavra-passe: ");
                pwd = scan.next();

                if(this.gameFactoyRI.register(username, pwd)){

                    System.out.println("Registo efetuado com sucesso!");
                    this.gameSessionRI = this.gameFactoyRI.login(username, pwd);
                    System.out.println("Logado!");
                    this.observer = new ObserverImpl(username);

                    new Game(this.gameSessionRI, this.observer);

                }else{

                    System.out.println("Utilizador já existe!");

                }

            }else if(option == 2) {

                System.out.println("Insira o seu nome de utilizador: ");
                username = scan.next();
                System.out.println("Insira a sua palavra-passe: ");
                pwd = scan.next();

                GameSessionRI session = this.gameFactoyRI.login(username, pwd);

                if(session != null){

                    this.gameSessionRI = session;
                    System.out.println("Login efetuado com sucesso!");
                    this.observer = new ObserverImpl(username);

                    new Game(this.gameSessionRI, this.observer);

                }else {

                    System.out.println("Utilizador ou password errados!");

                }

            }

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR finish, bye. ;)");
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
}
