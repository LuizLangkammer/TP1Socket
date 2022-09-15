package tcp;

import Classes.FieldInfo;
import Classes.Server;
import enums.Action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer extends Server {

    protected ArrayList<TCPPlayer> players;
    public TCPServer(){

        //Initialize Game board
        ArrayList<FieldInfo[][]> boards = initializeBoard();
        players = new ArrayList<TCPPlayer>();



        try{
            ServerSocket listenSocket = new ServerSocket(3080);

            int countPlayers = 0;
            while(countPlayers<2){

                Socket newSocket = listenSocket.accept();

                TCPPlayer player = new TCPPlayer(
                        new DataInputStream(newSocket.getInputStream()),
                        new DataOutputStream(newSocket.getOutputStream()),
                        boards.get(countPlayers)
                );

                players.add(player);
                player.out.write(getBytes(boards.get(countPlayers)));
                countPlayers++;
            }

            //Manage turns and plays===================================================================================
            boolean won=false;
            byte[] message = new byte[3];
            byte[] response = new byte[3];

            //Request first player to start==========================================================

            message[0] = Action.YOURTURN.getValue();
            players.get(0).out.write(message);

            //Loop turns=============================================================================
            while(!won){
                for (int i=0; i<2; i++){
                    TCPPlayer player = players.get(i);
                    TCPPlayer enemyPlayer = null;
                    if(i==1){
                        enemyPlayer = players.get(0);
                    }else{
                        enemyPlayer = players.get(1);
                    }

                    //Wait play ====================================================================
                    System.out.println("Waiting play");
                    player.in.read(response);
                    System.out.println("Play received");
                    if(response[0] == Action.OPEN.getValue()){
                        enemyPlayer.board[response[1]][response[2]].setOpen(true);

                        //Play feedback ============================================
                        if(enemyPlayer.board[response[1]][response[2]].ship){
                            player.point++;
                            message[0] = Action.HIT.getValue();
                        }else{
                            message[0] = Action.NOTHIT.getValue();
                        }
                        //Thread.sleep(200);
                        if(player.point == 9){
                            message[0] = Action.WON.getValue();
                            player.out.write(message);
                            message[0] = Action.LOST.getValue();
                            message[1] = response[1];
                            message[2] = response[2];
                            enemyPlayer.out.write(message);
                            won = true;
                            break;
                        }

                        System.out.println("Send play feedback");
                        player.out.write(message);

                        //Play notification
                        message[0] = Action.OPEN.getValue();
                        message[1] = response[1];
                        message[2] = response[2];
                        System.out.println("Send play feedback");
                        enemyPlayer.out.write(message);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Erro na comunicação. Encerrando...");
        }
    }


}
