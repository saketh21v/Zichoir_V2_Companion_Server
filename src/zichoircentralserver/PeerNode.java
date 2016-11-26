package zichoircentralserver;


import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Saketh
 */
public class PeerNode implements Serializable{
    
    public final String ID;
    public final String IP;
    public final String[] songNames;

    public PeerNode(String id, String ip, String[] songNames) {
        ID = id;
        IP = ip;
        this.songNames = songNames;
    }
}
