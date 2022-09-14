package udp;

import Classes.Server;
import enums.Action;
import Classes.FieldInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class UDPServer extends Server {

    ArrayList<UDPPlayer> players;

    public UDPServer(){

        //Initialize Game board
        ArrayList<FieldInfo[][]> boards = initializeBoard();
        players= new ArrayList<UDPPlayer>();




        DatagramSocket aSocket = null ;
        try{
            aSocket = new DatagramSocket(3080);

            //wait two players to connect==============================================================================
            byte [] input = new byte [3];
            byte [] output;
            int countPlayers = 0;
            while(countPlayers<2){
                DatagramPacket request = new DatagramPacket(input, input.length);
                aSocket.receive(request);

                if(input[0] == Action.CONNECT.getValue()) {
                    UDPPlayer player = new UDPPlayer(request.getAddress(), request.getPort(), boards.get(countPlayers));
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
                    UDPPlayer player = players.get(i);
                    UDPPlayer enemyPlayer = null;
                    if(i==1){
                        enemyPlayer = players.get(0);
                    }else{
                        enemyPlayer = players.get(1);
                    }
                    //Wait play ====================================================================

                    receiver = new DatagramPacket(response, response.length);
                    do{
                        aSocket.receive(receiver);
                    }while(!receiver.getAddress().equals(player.ipAddress) || receiver.getPort() != player.port);

                    if(response[0] == Action.OPEN.getValue()){

                        enemyPlayer.board[response[1]][response[2]].setOpen(true);

                        //Play feedback ============================================
                        if(enemyPlayer.board[response[1]][response[2]].ship){
                            player.point++;
                            message[0] = Action.HIT.getValue();
                        }else{
                            message[0] = Action.NOTHIT.getValue();
                        }

                        if(player.point == 9){
                            message[0] = Action.WON.getValue();
                            messanger = new DatagramPacket(message, message.length, player.ipAddress, player.port);
                            aSocket.send(messanger);
                            message[0] = Action.LOST.getValue();
                            message[1] = response[1];
                            message[2] = response[2];
                            messanger = new DatagramPacket(message, message.length, enemyPlayer.ipAddress, enemyPlayer.port);
                            aSocket.send(messanger);
                            won = true;
                            break;
                        }

                        messanger = new DatagramPacket(message, message.length, player.ipAddress, player.port);
                        aSocket.send(messanger);

                        //Play notification
                        message[0] = Action.OPEN.getValue();
                        message[1] = response[1];
                        message[2] = response[2];
                        messanger = new DatagramPacket(message, message.length, enemyPlayer.ipAddress, enemyPlayer.port);

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

}
