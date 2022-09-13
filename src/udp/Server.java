package udp;

import enums.Action;
import field.FieldInfo;
import windows.Window;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {

    Map<String, FieldInfo[][]> players;

    public Server(){

        //Initialize Game board
        ArrayList<FieldInfo[][]> boards = initializeBoard();
        players= new HashMap<String, FieldInfo[][]>();




        DatagramSocket aSocket = null ;
        try{
            aSocket = new DatagramSocket(3080);

            //wait two players to connect
            byte [] input = new byte [3];
            byte [] output;
            int countPlayers = 0;
            while(countPlayers<2){
                DatagramPacket request = new DatagramPacket(input, input.length);
                System.out.println("Aguradando...");
                aSocket.receive(request);

                if(input[0] == Action.CONNECT.getValor()) {
                    players.put(request.getAddress().toString() + ":" + request.getPort(), boards.get(countPlayers));

                    output = getBytes(boards.get(countPlayers));

                    DatagramPacket reply = new DatagramPacket(output, output.length,
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                    countPlayers++;
                }
            }

            //Manage turns and plays
            boolean won=false;
            while(!won){

            }

        }catch(Exception e){
            System.out.println("Erro na comunicação. Encerrando...");

        }
        finally {
            if ( aSocket != null) aSocket.close();
        }


    }


    private ArrayList<FieldInfo[][]> initializeBoard (){

        FieldInfo[][] player1Fields = new FieldInfo[8][10];
        FieldInfo[][] player2Fields = new FieldInfo[8][10];

        //Initialize array
        loop1:for(int i=0; i<player1Fields.length; i++) {
            for (int j = 0; j < player1Fields[0].length; j++) {
                player1Fields[i][j] = new FieldInfo();
                player2Fields[i][j] = new FieldInfo();
            }
        }

        Random random = new Random();

        int column, line, direction;
        boolean invertDirection;
        boolean cantSet=false;
        for(int i=0; i<3; i++){


            do{
                column = random.nextInt(10);
                line = random.nextInt(8);
                direction = random.nextInt(2);
                invertDirection = !checkDirectionLength(player1Fields.length,player1Fields[0].length, line, column, direction, i+2);
                if(hasNearShip(i+2, invertDirection, direction, line, column, player1Fields)){
                    cantSet=true;
                }else{
                    cantSet=false;
                    setShip(i+2, invertDirection, direction, line, column, player1Fields);
                }
            }while(cantSet);

            do{
                column = random.nextInt(10);
                line = random.nextInt(1);
                direction = random.nextInt(2);
                invertDirection = !checkDirectionLength(player2Fields.length,player2Fields[0].length, line, column, direction, i+2);
                if(hasNearShip(i+2, invertDirection, direction, line, column, player2Fields)){
                    cantSet=true;
                }else{
                    cantSet=false;
                    setShip(i+2, invertDirection, direction, line, column, player2Fields);
                }
            }while(cantSet);

        }

        ArrayList<FieldInfo[][]> boards = new ArrayList<FieldInfo[][]>(2);
        boards.add(player1Fields);
        boards.add(player2Fields);

        return boards;
    }

    private void setShip(int length, boolean invertDirection, int direction, int line, int column, FieldInfo[][] playerFields){
        for(int c=0; c<length; c++){
            int sum = c;
            if(invertDirection) sum = -c;
            switch (direction) {
                case 0: {
                    playerFields[line][column + sum].setShip(true);
                    break;
                }
                case 1: {
                    playerFields[line+sum][column].setShip(true);
                    break;
                }
            }
        }
    }

    private boolean hasNearShip(int length, boolean invertDirection, int direction, int line, int column, FieldInfo[][] playerFields){
        for(int c=0; c<length+1; c++){
            int sum = c;
            if(invertDirection) sum = -c;
            switch (direction) {
                case 0: {
                    if((column+sum >= playerFields[0].length) ||  playerFields[line][column + sum].isShip()){
                        return true;
                    }
                    break;
                }
                case 1: {
                    if((line+sum >= playerFields.length) || playerFields[line+sum][column].isShip()){
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean checkDirectionLength(int maxI,int maxJ, int i, int j, int direction, int length){
        switch(direction){
            case 0: {
                if(j + length - 1 < maxJ) {
                    return true;
                }
                return false;
            }
            case 1: {
                if (i + length - 1 < maxI) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    private byte[] getBytes(FieldInfo[][] board){

        byte[] output = new byte[board.length*board[0].length + 3];

        output[0] = Action.CONFIRMCONNECTION.getValor();
        output[1] = (byte)board.length;
        output[2] = (byte)board[0].length;

        int count = 3;
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[0].length; j++){

                if(board[i][j].isShip()) {
                    output[count] = 1;
                }
                count++;
            }
        }
        return output;
    }

}
