import javax.swing.*;
import java.awt.*;

public class Window extends JFrame{

    JLabel titleLB = new JLabel("Batalha Naval");
    JLabel ipTitleLB = new JLabel("IP");
    JLabel player1LB = new JLabel("Você");
    JLabel player2LB = new JLabel("Jogador 2");


    JRadioButton tcpRB= new JRadioButton("TCP");
    JRadioButton udpRB= new JRadioButton("UDP");
    ButtonGroup protocol= new ButtonGroup();

    JTextField ipTF= new JTextField(30);

    Font font= new Font("Times new Roman",Font.BOLD,30);
    Font font2= new Font("Times new Roman",Font.BOLD,20);

    Color black= new Color(0,0,0);

    JPanel jp1 = new JPanel();
    JPanel divider = new JPanel();
    JPanel jp2 = new JPanel();

    int lines, columns;

    Field[][] player1Fields;
    Field[][] player2Fields;

    public Window(FieldInfo[][] p1Informations, FieldInfo[][] p2Informations){

        lines = p1Informations.length;
        columns = p1Informations[0].length;

        player1Fields = new Field[lines][columns];
        player2Fields = new Field[lines][columns];

        for(int i=0; i<lines; i++){
            for(int j=0; j<columns; j++){
                player1Fields[i][j] = new Field(p1Informations[i][j]);
                player2Fields[i][j] = new Field(p2Informations[i][j]);
            }
        }


        setLayout(null);

        add(titleLB);
        titleLB.setFont(font);
        titleLB.setBounds(410,20,450,30);

        add(tcpRB);
        tcpRB.setBounds(100,100,60,20);
        add(udpRB);
        udpRB.setBounds(160,100,60,20);
        protocol.add(tcpRB);
        protocol.add(udpRB);


        add(ipTitleLB);
        ipTitleLB.setBounds(300,80,50,20);
        add(ipTF);
        ipTF.setBounds(290,100,150,30);



        add(player1LB);
        player1LB.setFont(font2);
        player1LB.setBounds(220,145,80,30);

        add(divider);
        divider.setBackground(black);
        divider.setBounds(498,165,4,335);

        add(player2LB);
        player2LB.setFont(font2);
        player2LB.setBounds(700,145,150,30);

        for(int i=0; i<lines; i++){
            for(int j=0; j<columns; j++){

                JButton button = player1Fields[i][j].getButton();

                add(button);
                button.setBounds(50+(j*40),175+(i*40),40,40);


                button = player2Fields[i][j].getButton();
                add(button);
                button.setBounds(550+(j*40),175+(i*40),40,40);

            }
        }


        setTitle("Client");
        setSize(1000,550);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void setBoard(FieldInfo[][] newPlayer1Fields, FieldInfo[][] newPlayer2Fields){

        for(int i=0; i<lines;i++){
            for(int j=0; j<columns; j++){
                Field field = this.player1Fields[i][j];


                field.setShowShip(true);
                field.setShip(newPlayer1Fields[i][j].ship);
                field.setOpen(newPlayer1Fields[i][j].open);

                field = this.player2Fields[i][j];


                field.setShowShip(false);
                field.setShip(newPlayer2Fields[i][j].ship);
                field.setOpen(newPlayer2Fields[i][j].open);
            }
        }
    }

}
