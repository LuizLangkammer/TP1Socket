package udp;

import Classes.FieldInfo;
import Classes.Player;

import java.net.InetAddress;

public class UDPPlayer extends Player {

    public InetAddress ipAddress;
    public int port;


    public UDPPlayer(InetAddress ipAddress, int port, FieldInfo[][] board){
        super(board);
        this.ipAddress = ipAddress;
        this.port = port;
    }

}
