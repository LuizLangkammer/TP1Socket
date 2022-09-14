import udp.Client;
import udp.Server;

import javax.swing.*;

public class Main {


    public static void main(String args[]) throws InterruptedException {

        String[] options = {"Servidor", "Cliente"};
        int selectedType = JOptionPane.showOptionDialog(null, "Qual o serviço que deseja subir",
                "Tipo de serviço", 0, 1, null, options, 0);

        if(selectedType==0){
            new Server();
        }else{

            new Client("127.0.0.1");

        }


    }

}
