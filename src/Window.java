import javax.swing.*;

public class Window extends JFrame{


    JRadioButton tcpRB= new JRadioButton("TCP");
    JRadioButton udpRB= new JRadioButton("UDP");


    public Window(){





        setTitle("Janela");
        setSize(800,500);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }


}
