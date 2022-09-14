import udp.*;
import tcp.*;

import javax.swing.*;

public class Main {


    public static void main(String args[]) throws InterruptedException {

        String[] options = {"UDP", "TCP"};
        int selectedComunication = JOptionPane.showOptionDialog(null, "Qual protocolo deseja usar",
                "Protocolo", 0,1,null, options, 0);

        String[] options2 = {"Servidor", "Cliente"};
        int selectedType = JOptionPane.showOptionDialog(null, "Qual o serviço que deseja subir",
                "Tipo de serviço", 0, 1, null, options2, 0);

        switch(selectedComunication){
            case 0: {
                if(selectedType==0){
                    new UDPServer();
                }else{
                    String ipAddress = JOptionPane.showInputDialog("Qual o ip do servidor desejado?");
                    new UDPClient(ipAddress);
                }
                break;
            }
            case 1: {
                if(selectedType==0){
                    new TCPServer();
                }else{
                    String ipAddress = JOptionPane.showInputDialog("Qual o ip do servidor desejado?");
                    new TCPClient(ipAddress);
                }
                break;
            }
        }





    }

}
