package tcp;

import Classes.FieldInfo;
import Classes.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

public class TCPPlayer extends Player {


    public DataInputStream in;

    public DataOutputStream out;



    public TCPPlayer(DataInputStream in, DataOutputStream out, FieldInfo[][] board){
        super(board);
        this.in = in;
        this.out = out;
    }

}
