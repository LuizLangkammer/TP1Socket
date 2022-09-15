package tcp;

import Classes.Client;
import Classes.FieldInfo;
import enums.Action;
import windows.Window;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class TCPClient extends Client {

    DataInputStream in;

    DataOutputStream out;



    public TCPClient(String ipAddress){



        Socket socket = null;
        try {
            socket = new Socket(ipAddress, 3080);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            byte[] answer = new byte[500];
            in.read(answer);

            board = buildBoard(answer);
            window = new Window(board, this);

            waitForTurn();

        }
        catch(Exception e){
            e.printStackTrace();
            if(window!=null){
                window.setMessage("Falha de Comunicação");
            }
            System.out.println("Erro na comunicação. Encerrando...");
        }

    }

    public void waitForTurn(){

        window.setMessage("Aguardando turno");

        new Thread(){
            @Override
            public void run() {
                try{
                    byte[] response = new byte[3];
                    System.out.println("Waiting turn");
                    in.read(response);
                    System.out.println("Received turn");
                    if(response[0] == Action.YOURTURN.getValue()){
                        window.setMessage("Sua vez!");
                        myTurn = true;
                        Thread.sleep(800);
                        Random random = new Random();
                        play((byte)random.nextInt(8), (byte)random.nextInt(10));
                    }else{
                        if(response[0] == Action.OPEN.getValue()){
                            window.setField(response[1], response[2]);
                            window.setMessage("Sua vez!");
                            myTurn = true;
                            Thread.sleep(500);
                            Random random = new Random();
                            play((byte)random.nextInt(8), (byte)random.nextInt(10));
                        }
                        if(response[0] == Action.LOST.getValue()){
                            window.setField(response[1], response[2]);
                            window.setMessage("Perdemos");
                            myTurn = false;
                        }
                    }

                }catch (Exception e) {
                    if (window != null) {
                        window.setMessage("Falha de Comunicação");
                    }
                    System.out.println("Erro na comunicação");
                }
            }

        }.start();


    }


    public void play(byte i, byte j){
        if(myTurn) {

            byte[] message = new byte[3];
            byte[] response = new byte[3];
            try {
                //Send play to server
                message[0] = Action.OPEN.getValue();
                message[1] = i;
                message[2] = j;
               // Thread.sleep(200);
                System.out.println("Sending play");
                out.write(message);
                myTurn = false;
                //wait server to answer the result
                System.out.println("Waiting play feedback");
                in.read(response);
                System.out.println("Received feedback");

                if(response[0] == Action.HIT.getValue() || response[0] == Action.NOTHIT.getValue()){
                    window.setField(i,j,response[0] == Action.HIT.getValue());
                    waitForTurn();
                }else{
                    if(response[0] == Action.WON.getValue()){
                        window.setField(i,j,true);
                        window.setMessage("Você Venceu!!!!!!!");
                    }
                }

            } catch (Exception e) {
                if (window != null) {
                    window.setMessage("Falha de Comunicação");
                }
                System.out.println("Erro na comunicação");
            }
        }
    }


}
