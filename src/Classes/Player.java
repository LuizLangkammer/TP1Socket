package Classes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Player {


    public FieldInfo[][] board;

    public int point;

    public Player(FieldInfo[][] board){
        this.board = board;
    }

}
