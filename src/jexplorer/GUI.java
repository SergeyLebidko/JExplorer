package jexplorer;

import javax.swing.*;
import java.awt.*;

public class GUI {

    private final int WIDTH_FRM = 1400;
    private final int HEIGHT_FRM = 850;

    private final JFrame frm;

    private TileExplorerPane tileExplorerPane;

    public GUI() {

        //Заменяем текущий LaF системным
        String laf=UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex){
            JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке переключить стиль интерфейса. Работа программы будет прекращена", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Создаем главное окно
        frm=new JFrame("JExplorer");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(WIDTH_FRM, HEIGHT_FRM);
        frm.setIconImage(new ImageIcon("res\\logo.png").getImage());
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH_FRM / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT_FRM / 2;
        frm.setLocation(xPos, yPos);

        createTileExplorerPane();

        frm.setVisible(true);
    }

    private void createTileExplorerPane(){
        tileExplorerPane=new TileExplorerPane();
        frm.add(tileExplorerPane,BorderLayout.CENTER);
    }

}
