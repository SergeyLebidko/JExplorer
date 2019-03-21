package jexplorer;

import jexplorer.guiclasses.ExplorerPane;
import jexplorer.guiclasses.adressPane.AdressPane;
import jexplorer.guiclasses.rootpane.RootPointExplorerPane;
import jexplorer.guiclasses.tilepane.TileExplorerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {

    private final int WIDTH_FRM = 1300;
    private final int HEIGHT_FRM = 850;

    private final JFrame frm;

    private ExplorerPane currentExplorerPane;

    private ExplorerPane rootPointExplorerPane;
    private AdressPane adressPane;

    private JButton refreshPanesBtn;

    private JButton bigTileViewBtn;
    private JButton smallTileViewBtn;
    private JButton tableViewBtn;
    private JToggleButton hiddenBtn;

    private JButton upBtn;

    public GUI() {

        //Заменяем текущий LaF системным
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке переключить стиль интерфейса. Работа программы будет прекращена", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Создаем главное окно
        frm = new JFrame("JExplorer");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(WIDTH_FRM, HEIGHT_FRM);
        frm.setIconImage(new ImageIcon("res\\logo.png").getImage());
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH_FRM / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT_FRM / 2;
        frm.setLocation(xPos, yPos);

        //Создаем панель контента для главного окна
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        frm.setContentPane(contentPane);

        //Создаем вспомогательную панель для отображения содержимого текущей папки
        JPanel fPane = new JPanel();
        fPane.setLayout(new BorderLayout());
        currentExplorerPane = new TileExplorerPane();
        fPane.add(currentExplorerPane.getVisualComponent(), BorderLayout.CENTER);

        Box fUpPane = Box.createHorizontalBox();
        fUpPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        hiddenBtn = new JToggleButton(new ImageIcon("res\\do_not_show_hidden.png"));
        hiddenBtn.setSelectedIcon(new ImageIcon("res\\show_hidden.png"));
        hiddenBtn.setToolTipText("Показать скрытые элементы");
        bigTileViewBtn = new JButton(new ImageIcon("res\\big_tiles.png"));
        bigTileViewBtn.setToolTipText("Крупные значки");
        smallTileViewBtn = new JButton(new ImageIcon("res\\small_tiles.png"));
        smallTileViewBtn.setToolTipText("Мелкие значки");
        tableViewBtn = new JButton(new ImageIcon("res\\table.png"));
        tableViewBtn.setToolTipText("Таблица");
        fUpPane.add(hiddenBtn);
        fUpPane.add(Box.createHorizontalGlue());
        fUpPane.add(tableViewBtn);
        fUpPane.add(Box.createHorizontalStrut(5));
        fUpPane.add(smallTileViewBtn);
        fUpPane.add(Box.createHorizontalStrut(5));
        fUpPane.add(bigTileViewBtn);
        fPane.add(fUpPane, BorderLayout.NORTH);

        //Создаем вспомогательную панель для отображения списка корневых точек
        JPanel rPane = new JPanel();
        rPane.setLayout(new BorderLayout());
        rPane.setPreferredSize(new Dimension(WIDTH_FRM / 6, (int) (HEIGHT_FRM * 0.9)));
        rootPointExplorerPane = new RootPointExplorerPane();
        rPane.add(rootPointExplorerPane.getVisualComponent(), BorderLayout.CENTER);

        Box rUpPane = Box.createHorizontalBox();
        rUpPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        refreshPanesBtn = new JButton(new ImageIcon("res\\refresh.png"));
        refreshPanesBtn.setToolTipText("обновить");
        rUpPane.add(refreshPanesBtn);
        rUpPane.add(Box.createHorizontalGlue());
        rPane.add(rUpPane, BorderLayout.NORTH);

        //Создаем панель, которая будет содаржать адресную строку и кнопку "вверх"
        Box upPane = Box.createHorizontalBox();
        upBtn = new JButton(new ImageIcon("res\\up.png"));
        upBtn.setToolTipText("Вверх");
        adressPane = new AdressPane();
        upPane.add(upBtn);
        upPane.add(Box.createHorizontalStrut(5));
        upPane.add(adressPane.getVisualComponent());

        //Добавляем компонентам слушатели событий
        refreshPanesBtn.addActionListener(refreshPanes);
        bigTileViewBtn.addActionListener(setBigTiles);
        smallTileViewBtn.addActionListener(setSmallTiles);
        hiddenBtn.addActionListener(hiddenRevert);

        //Добавляем вспомогательные панели в корневую панель
        contentPane.add(rPane, BorderLayout.WEST);
        contentPane.add(fPane, BorderLayout.CENTER);
        contentPane.add(upPane, BorderLayout.NORTH);

        frm.setVisible(true);
    }

    public ExplorerPane getCurrentExplorerPane() {
        return currentExplorerPane;
    }

    public AdressPane getAdressPane() {
        return adressPane;
    }

    private ActionListener refreshPanes = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            rootPointExplorerPane.refreshContent();
            currentExplorerPane.refreshContent();
            adressPane.refreshContent();
        }
    };

    private ActionListener setBigTiles = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentExplorerPane instanceof TileExplorerPane) {
                TileExplorerPane tileExplorerPane = (TileExplorerPane) currentExplorerPane;
                tileExplorerPane.setBigCells();
            }
        }
    };

    private ActionListener setSmallTiles = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentExplorerPane instanceof TileExplorerPane) {
                TileExplorerPane tileExplorerPane = (TileExplorerPane) currentExplorerPane;
                tileExplorerPane.setSmallCells();
            }
        }
    };

    private ActionListener hiddenRevert = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentExplorerPane instanceof TileExplorerPane) {
                if (hiddenBtn.isSelected()) hiddenBtn.setToolTipText("Не показывать скрытые элементы");
                if (!hiddenBtn.isSelected()) hiddenBtn.setToolTipText("Показать скрытые элементы");
                TileExplorerPane tileExplorerPane = (TileExplorerPane) currentExplorerPane;
                tileExplorerPane.revertShowHiddenElements();
            }
        }
    };

}
