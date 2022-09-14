package udp;

import Classes.Player;
import enums.Action;
import Classes.FieldInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Server {

    ArrayList<Player> players;

    public Server(){

        //Initialize Game board
        ArrayList<FieldInfo[][]> boards = initializeBoard();
        players= new ArrayList<Player>();




        DatagramSocket aSocket = null ;
        try{
            aSocket = new DatagramSocket(3080);

            //wait two players to connect==============================================================================
            byte [] input = new byte [3];
            byte [] output;
            int countPlayers = 0;
            while(countPlayers<2){
                DatagramPacket request = new DatagramPacket(input, input.length);
                System.out.println("Aguradando...");
                aSocket.receive(request);

                if(input[0] == Action.CONNECT.getValue()) {
                    Player player = new Player(request.getAddress(), request.getPort(), boards.get(countPlayers));
                    players.add(player);

                    output = getBytes(boards.get(countPlayers));

                    DatagramPacket reply = new DatagramPacket(output, output.length,
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                    countPlayers++;
                }
            }

            //Manage turns and plays===================================================================================
            boolean won=false;
            DatagramPacket messanger = null;
            DatagramPacket receiver = null;
            byte[] message = new byte[3];
            byte[] response = new byte[3];
            //Request first player to start==========================================================

            message[0] = Action.YOURTURN.getValue();
            messanger = new DatagramPacket(message, message.length, players.get(0).ipAddress, players.get(0).port);
            aSocket.send(messanger);

            //Loop turns=============================================================================
            while(!won){

                for (int i=0; i<2; i++){
                    Player player = players.get(i);
                    Player enemyPlayer = null;
                    if(i==1){
                        enemyPlayer = players.get(0);
                    }else{
                        enemyPlayer = players.get(1);
                    }
                    //Wait play ====================================================================

                    receiver = new DatagramPacket(response, response.length);
                    do{
                        System.out.println("Waiting player "+i);
                        aSocket.receive(receiver);
                    }while(!receiver.getAddress().equals(player.ipAddress) || receiver.getPort() != player.port);

                    if(response[0] == Action.OPEN.getValue()){

                        //Play feedback ============================================
                        if(enemyPlayer.board[response[1]][response[2]].ship){
                            message[0] = Action.HIT.getValue();
                        }else{
                            message[0] = Action.NOTHIT.getValue();
                        }
                        messanger = new DatagramPacket(message, message.length, player.ipAddress, player.port);
                        System.out.println("Player "+i+" feedback");
                        aSocket.send(messanger);

                        //Play notification
                        message[0] = Action.OPEN.getValue();
                        message[1] = response[1];
                        message[2] = response[2];
                        messanger = new DatagramPacket(message, message.length, enemyPlayer.ipAddress, enemyPlayer.port);
                        System.out.println("Player turn");
                        aSocket.send(messanger);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
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

        output[0] = Action.CONFIRMCONNECTION.getValue();
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
