import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {




    public Client(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte [] m = "BORA BILLLLLL!".getBytes();
            InetAddress aHost = InetAddress.getByName("127.0.0.1");
            int serverPort = 3080;
            DatagramPacket request =
                    new DatagramPacket(m,  m.length, aHost, serverPort);
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));
        }
        catch(Exception e){
            System.out.println("Erro na comunicação. Encerrando...");
        }
        finally {
            if ( aSocket != null) aSocket.close();
        }
    }


}
