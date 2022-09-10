import javax.swing.*;
import java.awt.*;


public class Field {

    Color blue= new Color(0,0,80);
    Color lightBlue= new Color(0,0,200);
    Color grey= new Color(50,50,50);
    Color red= new Color(150,0,0);
    private JButton button;
    private boolean open;
    private boolean ship;

    private boolean showShip;

    public Field(FieldInfo information){
        button = new JButton();
        this.open= information.open;
        this.ship = information.ship;
        if(ship){
            button.setBackground(grey);
        }else{
            button.setBackground(blue);
        }

    }

    public boolean isOpen() {
        return open;
    }

    public boolean isShip() {
        return ship;
    }

    public boolean isShowShip() {
        return showShip;
    }

    public JButton getButton() {
        return button;
    }

    public void setOpen(boolean open) {
        if(open && !this.open){
            if(!ship){
                button.setBackground(lightBlue);
            }else{
                button.setBackground(red);
            }
            this.open = true;
        }
    }

    public void setShip(boolean ship) {
        if(ship && !this.ship) {
            if (showShip) {
                button.setBackground(grey);
            }

            this.ship = true;
        }
    }

    public void setShowShip(boolean showShip) {
        this.showShip = showShip;
    }
}
