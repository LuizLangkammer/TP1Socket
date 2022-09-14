package Classes;

import java.net.InetAddress;

public class Player {

    public FieldInfo[][] board;
    public InetAddress ipAddress;
    public int port;

    public int point;

    public Player(InetAddress ipAddress, int port, FieldInfo[][] board){
        this.board = board;
        this.ipAddress = ipAddress;
        this.port = port;
    }

}
