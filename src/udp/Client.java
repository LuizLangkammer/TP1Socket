package udp;

import enums.Action;
import Classes.FieldInfo;
import windows.Window;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {


    FieldInfo[][] board;
    boolean myTurn = false;

    String ipAddress;

    DatagramSocket aSocket;

    Window window;

    public Client(String ipAddress){

        this.ipAddress = ipAddress;

        try {
            aSocket = new DatagramSocket();

            //Connect to server
            byte [] connectionMessage = {Action.CONNECT.getValue(), 0, 0};
            InetAddress aHost = InetAddress.getByName(ipAddress);
            int serverPort = 3080;
            DatagramPacket request =
                    new DatagramPacket(connectionMessage,  connectionMessage.length, aHost, serverPort);
            aSocket.send(request);
            byte[] answer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(answer, answer.length);
            aSocket.receive(reply);

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
                    DatagramPacket receiver = null;
                    byte[] response = new byte[3];
                    receiver = new DatagramPacket(response, response.length);
                    System.out.println("Waiting server");
                    aSocket.receive(receiver);

                    if(response[0] == Action.YOURTURN.getValue()){
                        window.setMessage("Sua vez!");
                        myTurn = true;
                        System.out.println("Playing");
                    }else{
                        if(response[0] == Action.OPEN.getValue()){
                            window.setField(response[1], response[2]);
                            window.setMessage("Sua vez!");
                            myTurn = true;
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
            System.out.println("Playing");
            DatagramPacket messanger = null;
            DatagramPacket receiver = null;
            byte[] message = new byte[3];
            byte[] response = new byte[3];
            try {
                //Send play to server
                message[0] = Action.OPEN.getValue();
                message[1] = i;
                message[2] = j;
                messanger = new DatagramPacket(message, message.length, InetAddress.getByName(ipAddress), 3080);
                System.out.println("Sending Play");
                aSocket.send(messanger);
                myTurn = false;
                //wait server to answer the result
                receiver = new DatagramPacket(response, response.length);
                System.out.println("Waiting response");
                aSocket.receive(receiver);

                if(response[0] == Action.HIT.getValue() || response[0] == Action.NOTHIT.getValue()){
                    window.setField(i,j,response[0] == Action.HIT.getValue());
                    gambi();
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

    public void gambi(){
        waitForTurn();
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
