package jexplorer;

import jexplorer.guiclasses.ExplorerPane;
import jexplorer.guiclasses.rootpane.RootPointExplorerPane;
import jexplorer.guiclasses.tilepane.TileExplorerPane;

import javax.swing.*;
import java.awt.*;

public class GUI {

    private final int WIDTH_FRM = 1300;
    private final int HEIGHT_FRM = 850;

    private final JFrame frm;

    private ExplorerPane currentExplorerPane;

    private ExplorerPane rootPointExplorerPane;

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

        //Создаем панель контента для главного окна
        JPanel contentPane=new JPanel();
        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        frm.setContentPane(contentPane);

        //Создаем вспомогательную панель для отображения содержимого текущей папки
        JPanel fPane=new JPanel();
        fPane.setLayout(new BorderLayout());
        currentExplorerPane=new TileExplorerPane();
        fPane.add(currentExplorerPane.getVisualComponent(),BorderLayout.CENTER);

        //Создаем вспомогательную панель для отображения списка корневых точек
        JPanel rPane=new JPanel();
        rPane.setLayout(new BorderLayout());
        rPane.setPreferredSize(new Dimension(WIDTH_FRM/6,(int) (HEIGHT_FRM*0.9)));
        rootPointExplorerPane=new RootPointExplorerPane();
        rPane.add(rootPointExplorerPane.getVisualComponent(),BorderLayout.CENTER);

        //Добавляем вспомогательные панели в корневую панель
        contentPane.add(rPane,BorderLayout.WEST);
        contentPane.add(fPane,BorderLayout.CENTER);

        frm.setVisible(true);
    }

}
