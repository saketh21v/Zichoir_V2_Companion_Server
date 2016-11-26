/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zichoircentralserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Saketh
 */
public class ZichoirCentralServer {

    static HashMap<String, PeerNode> Peers;

    static final int ServerSocketPullPort = 4455;
    static final int ServerSocketPushPort = 5566;

    static ServerSocket CentralPullServerSocket;
    static ServerSocket CentralPushServerSocket;

    static Socket CurrentPullSocket;
    static Socket CurrentPushSocket;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Peers = new HashMap<>();
            CentralPullServerSocket = new ServerSocket(ServerSocketPullPort);
            CentralPushServerSocket = new ServerSocket(ServerSocketPushPort);

            // Push Server ---- Catalogue updates from peers
            Thread serverThread = new Thread(
                    new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            System.out.println("Waiting on Push Requests");
                            CurrentPushSocket = CentralPushServerSocket.accept();

                            System.out.println("Push server receiving from IP : " + CurrentPushSocket.getInetAddress().getHostAddress());
                            ObjectInputStream ois = new ObjectInputStream(CurrentPushSocket.getInputStream());
                           
                            //Receive IP
                            String IP = CurrentPushSocket.getInetAddress().getHostAddress();
                            if(IP.equals("127.0.0.1"))
                                IP = "10.6.4.246";
                            //Set ID = IP
                            String ID = IP;
                            
                            //Receive Strings[]
                            String[] songs = (String[]) ois.readObject();

                            for (String s : songs) {
                                System.out.println(ID + " : " + s);
                            }

                            PeerNode pn = new PeerNode(ID, IP, songs);

                            System.out.println("Details : IP(Given) : " + pn.IP);
                            Peers.put(ID, pn);
                            CurrentPushSocket.close();
                            System.out.println("Catalogue updating done.");
                        } catch (IOException ex) {
                            Logger.getLogger(ZichoirCentralServer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(ZichoirCentralServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            );

            serverThread.start();

            // Pull Catalogue Server
            while (true) {
                System.out.println("Waiting on Pull Requests");
                CurrentPullSocket = CentralPullServerSocket.accept();
                ObjectOutputStream oos = new ObjectOutputStream(CurrentPullSocket.getOutputStream());
                System.out.println("Pull request from IP : " + CurrentPullSocket.getInetAddress().getHostAddress());

                //Sending NoOfPeers
                oos.writeObject(Peers.size());

                ArrayList<PeerNode> al = new ArrayList(Peers.values());

//                Iterator ite = set.iterator();
//                while(ite.hasNext()){
                for (PeerNode pn : al) {
//                    PeerNode pn = (PeerNode) ite.next();
                    //Send ID
                    oos.writeObject(pn.ID);
                    //Send IP
                    oos.writeObject(pn.IP);
                    //Send Songs[]
                    oos.writeObject(pn.songNames);

                    System.out.println("Sent PeerNode : " + pn.ID);
                }
                CurrentPullSocket.close();
                System.out.println("Catalogue successfully sent");
            }
        } catch (IOException ex) {
            Logger.getLogger(ZichoirCentralServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
