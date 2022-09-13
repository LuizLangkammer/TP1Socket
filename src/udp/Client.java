package udp;

import enums.Action;
import field.FieldInfo;
import windows.Window;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {


    FieldInfo[][] board;

    public Client(String ipAddress){


        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();

            //Connect to server
            byte [] connectionMessage = {Action.CONNECT.getValor(), 0, 0};
            InetAddress aHost = InetAddress.getByName(ipAddress);
            int serverPort = 3080;
            DatagramPacket request =
                    new DatagramPacket(connectionMessage,  connectionMessage.length, aHost, serverPort);
            aSocket.send(request);
            byte[] answer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(answer, answer.length);
            aSocket.receive(reply);

            board = buildBoard(answer);

            Window window = new Window(board);

            //Start Game

        }
        catch(Exception e){
            System.out.println("Erro na comunicação. Encerrando...");
        }
        finally {
            if ( aSocket != null) aSocket.close();
        }

    }


    private FieldInfo[][] buildBoard(byte[] input){

        FieldInfo[][] board = new FieldInfo[input[1]][input[2]];

        int count = 3;
        for(int i=0; i<input[1]; i++){
            for(int j=0; j<input[2]; j++){
                board[i][j] = new FieldInfo();
                if(input[count]==1){
                    board[i][j].setShip(true);
                }
                count++;
            }
        }

        return board;
    }

}
