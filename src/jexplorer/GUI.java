package jexplorer;

import jexplorer.fileexplorerclasses.FileSorter;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileexplorerclasses.SortMethods;
import jexplorer.fileexplorerclasses.SortOrders;
import jexplorer.guiclasses.ExplorerPane;
import jexplorer.guiclasses.adressPane.AdressPane;
import jexplorer.guiclasses.rootpane.RootPointExplorerPane;
import jexplorer.guiclasses.tablepane.TableExplorerPane;
import jexplorer.guiclasses.tilepane.TileExplorerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI {

    private final int WIDTH_FRM = 1300;
    private final int HEIGHT_FRM = 850;

    private final JFrame frm;

    private FileSystemExplorer fileSystemExplorer;

    private ExplorerPane currentExplorerPane;

    private ExplorerPane tileExplorer;
    private ExplorerPane tableExplorer;

    private RootPointExplorerPane rootPointExplorerPane;
    private AdressPane adressPane;

    private JButton refreshPanesBtn;
    private JButton bigTileViewBtn;
    private JButton smallTileViewBtn;
    private JButton tableViewBtn;
    private JToggleButton showHiddenBtn;
    private String commandToShowHiddenBtn = "btn_element";

    private JButton upBtn;

    private JMenu fileMenu;
    private JMenuItem exitItem;

    private JMenu sortedMenu;
    private JRadioButtonMenuItem sortedByNameItem;
    private JRadioButtonMenuItem sortedBySizeItem;
    private JRadioButtonMenuItem sortedByTypeItem;
    private JRadioButtonMenuItem sortedByExtensionItem;
    private JRadioButtonMenuItem sortedByDateCreatedItem;
    private JRadioButtonMenuItem sortedByDateModifiedItem;
    private JRadioButtonMenuItem orderToUpItem;
    private JRadioButtonMenuItem orderToDownItem;

    private ButtonGroup sortedGroup;
    private ButtonGroup orderGroup;

    private JMenu viewMunu;
    private JRadioButtonMenuItem bigTilesItem;
    private JRadioButtonMenuItem smallTilesItem;
    private JRadioButtonMenuItem tableItem;

    private JCheckBoxMenuItem showHiddenItem;
    private String commandToShowHiddenItem = "menu_element";

    private ButtonGroup viewGroup;

    private JMenu helpMenu;
    private JMenuItem aboutItem;

    public GUI() {

        //Заменяем текущий Look and Feel системным
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке переключить стиль интерфейса. Работа программы будет прекращена", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Получаем от MainClass объект для работы с файловой системой
        fileSystemExplorer = MainClass.getFileSystemExplorer();

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

        //Создаем панели - табличную и плиточную. По-умолчанию программа стартует с плиточной панелью
        tileExplorer = new TileExplorerPane();
        tableExplorer = new TableExplorerPane();
        currentExplorerPane = tileExplorer;

        //Создаем вспомогательную панель для отображения содержимого текущей папки
        JPanel fPane = new JPanel();
        fPane.setLayout(new BorderLayout());
        fPane.add(currentExplorerPane.getVisualComponent(), BorderLayout.CENTER);

        //Создаем панель с кнопками для изменения режима отображения скрытых файлов и переключения режима отображения файлов
        Box fUpPane = Box.createHorizontalBox();
        fUpPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        showHiddenBtn = new JToggleButton(new ImageIcon("res\\do_not_show_hidden.png"));
        showHiddenBtn.setSelectedIcon(new ImageIcon("res\\show_hidden.png"));
        showHiddenBtn.setToolTipText("Сейчас скрытые элементы не отображаются");
        showHiddenBtn.setActionCommand(commandToShowHiddenBtn);
        bigTileViewBtn = new JButton(new ImageIcon("res\\big_tiles.png"));
        bigTileViewBtn.setToolTipText("Крупные значки");
        smallTileViewBtn = new JButton(new ImageIcon("res\\small_tiles.png"));
        smallTileViewBtn.setToolTipText("Мелкие значки");
        tableViewBtn = new JButton(new ImageIcon("res\\table.png"));
        tableViewBtn.setToolTipText("Таблица");
        fUpPane.add(showHiddenBtn);
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

        //Создаем панель с кнопкой "Обновить"
        Box rUpPane = Box.createHorizontalBox();
        rUpPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        refreshPanesBtn = new JButton(new ImageIcon("res\\refresh.png"));
        refreshPanesBtn.setToolTipText("Обновить");
        rUpPane.add(refreshPanesBtn);
        rUpPane.add(Box.createHorizontalGlue());
        rPane.add(rUpPane, BorderLayout.NORTH);

        //Создаем панель, которая будет содаржать адресную строку и кнопку "Вверх"
        Box upPane = Box.createHorizontalBox();
        upBtn = new JButton(new ImageIcon("res\\up.png"));
        upBtn.setToolTipText("Вверх");
        adressPane = new AdressPane();
        upPane.add(upBtn);
        upPane.add(Box.createHorizontalStrut(5));
        upPane.add(adressPane.getVisualComponent());

        //Создаем главное меню и его элементы
        JMenuBar mainMenu = new JMenuBar();
        mainMenu.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        fileMenu = new JMenu("Файл");
        exitItem = new JMenuItem("Выход");
        fileMenu.add(exitItem);

        sortedMenu = new JMenu("Сортировка");
        sortedByNameItem = new JRadioButtonMenuItem("По имени", true);
        sortedByNameItem.setActionCommand(SortMethods.BY_NAME.getName());
        sortedBySizeItem = new JRadioButtonMenuItem("По размеру");
        sortedBySizeItem.setActionCommand(SortMethods.BY_SIZE.getName());
        sortedByTypeItem = new JRadioButtonMenuItem("По типу");
        sortedByTypeItem.setActionCommand(SortMethods.BY_TYPE.getName());
        sortedByExtensionItem = new JRadioButtonMenuItem("По расширению");
        sortedByExtensionItem.setActionCommand(SortMethods.BY_EXTENSION.getName());
        sortedByDateCreatedItem = new JRadioButtonMenuItem("По дате создания");
        sortedByDateCreatedItem.setActionCommand(SortMethods.BY_DATE_CREATED.getName());
        sortedByDateModifiedItem = new JRadioButtonMenuItem("По дате изменения");
        sortedByDateModifiedItem.setActionCommand(SortMethods.BY_DATE_MODIFIED.getName());

        sortedGroup = new ButtonGroup();
        sortedGroup.add(sortedByNameItem);
        sortedGroup.add(sortedBySizeItem);
        sortedGroup.add(sortedByTypeItem);
        sortedGroup.add(sortedByExtensionItem);
        sortedGroup.add(sortedByDateCreatedItem);
        sortedGroup.add(sortedByDateModifiedItem);

        orderToUpItem = new JRadioButtonMenuItem("По возрастанию", true);
        orderToUpItem.setActionCommand(SortOrders.TO_UP.getName());
        orderToDownItem = new JRadioButtonMenuItem("По убыванию");
        orderToDownItem.setActionCommand(SortOrders.TO_DOWN.getName());

        orderGroup = new ButtonGroup();
        orderGroup.add(orderToUpItem);
        orderGroup.add(orderToDownItem);

        sortedMenu.add(sortedByNameItem);
        sortedMenu.add(sortedBySizeItem);
        sortedMenu.add(sortedByTypeItem);
        sortedMenu.add(sortedByExtensionItem);
        sortedMenu.add(sortedByDateCreatedItem);
        sortedMenu.add(sortedByDateModifiedItem);
        sortedMenu.addSeparator();
        sortedMenu.add(orderToUpItem);
        sortedMenu.add(orderToDownItem);

        viewMunu = new JMenu("Вид");
        bigTilesItem = new JRadioButtonMenuItem("Крупные значки", true);
        smallTilesItem = new JRadioButtonMenuItem("Мелкие значки");
        tableItem = new JRadioButtonMenuItem("Таблица");

        viewGroup = new ButtonGroup();
        viewGroup.add(bigTilesItem);
        viewGroup.add(smallTilesItem);
        viewGroup.add(tableItem);

        showHiddenItem = new JCheckBoxMenuItem("Отображать скрытые элементы", false);
        showHiddenItem.setActionCommand(commandToShowHiddenItem);

        viewMunu.add(bigTilesItem);
        viewMunu.add(smallTilesItem);
        viewMunu.add(tableItem);
        viewMunu.addSeparator();
        viewMunu.add(showHiddenItem);

        helpMenu = new JMenu("Помощь");
        aboutItem = new JMenuItem("О программе");

        helpMenu.add(aboutItem);

        //Добавляем созданные элементы в главное меню
        mainMenu.add(fileMenu);
        mainMenu.add(sortedMenu);
        mainMenu.add(viewMunu);
        mainMenu.add(helpMenu);

        //Создаем слушателей, необходимых для переключения видов: табличного, плиточного с маленькими плитками и плиточного с большими плитками
        ActionListener setBigTileView = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentExplorerPane instanceof TableExplorerPane) {
                    fPane.remove(currentExplorerPane.getVisualComponent());
                    currentExplorerPane = tileExplorer;
                    fPane.add(currentExplorerPane.getVisualComponent(), BorderLayout.CENTER);
                    fPane.revalidate();
                    fPane.repaint();
                }
                TileExplorerPane tileExplorerPane = (TileExplorerPane) currentExplorerPane;
                tileExplorerPane.setBigCells();
            }
        };
        ActionListener setSmallTileView = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentExplorerPane instanceof TableExplorerPane) {
                    fPane.remove(currentExplorerPane.getVisualComponent());
                    currentExplorerPane = tileExplorer;
                    fPane.add(currentExplorerPane.getVisualComponent(), BorderLayout.CENTER);
                    fPane.revalidate();
                    fPane.repaint();
                }
                TileExplorerPane tileExplorerPane = (TileExplorerPane) currentExplorerPane;
                tileExplorerPane.setSmallCells();
            }
        };
        ActionListener setTableView = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentExplorerPane instanceof TileExplorerPane) {
                    fPane.remove(currentExplorerPane.getVisualComponent());
                    currentExplorerPane = tableExplorer;
                    fPane.add(currentExplorerPane.getVisualComponent(), BorderLayout.CENTER);
                    fPane.revalidate();
                    fPane.repaint();
                    tableExplorer.refreshContent();
                }
            }
        };

        //Добавляем элементам меню слушатели событий
        exitItem.addActionListener(exitListener);

        bigTilesItem.addActionListener(setBigTileView);
        smallTilesItem.addActionListener(setSmallTileView);
        tableItem.addActionListener(setTableView);
        showHiddenItem.addActionListener(hiddenListener);

        sortedByNameItem.addActionListener(setSortedType);
        sortedBySizeItem.addActionListener(setSortedType);
        sortedByTypeItem.addActionListener(setSortedType);
        sortedByExtensionItem.addActionListener(setSortedType);
        sortedByDateCreatedItem.addActionListener(setSortedType);
        sortedByDateModifiedItem.addActionListener(setSortedType);

        orderToUpItem.addActionListener(setSortedOrder);
        orderToDownItem.addActionListener(setSortedOrder);

        aboutItem.addActionListener(aboutListener);

        //Добавляем меню в главное окно
        frm.setJMenuBar(mainMenu);

        //Добавляем кнопкам слушатели событий
        refreshPanesBtn.addActionListener(refreshPanesListener);
        bigTileViewBtn.addActionListener(setBigTileView);
        smallTileViewBtn.addActionListener(setSmallTileView);
        tableViewBtn.addActionListener(setTableView);

        showHiddenBtn.addActionListener(hiddenListener);
        upBtn.addActionListener(upListener);

        //Добавляем вспомогательные панели в корневую панель
        contentPane.add(rPane, BorderLayout.WEST);
        contentPane.add(fPane, BorderLayout.CENTER);
        contentPane.add(upPane, BorderLayout.NORTH);

        //Выводим окно программы на экран
        frm.setVisible(true);
    }

    public void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(frm, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    //Ниже идут геттеры, используемые элементами интерфейса для управления друг другом
    public ExplorerPane getCurrentExplorerPane() {
        return currentExplorerPane;
    }

    public AdressPane getAdressPane() {
        return adressPane;
    }

    //Ниже идет группа анонимных классов - обработчиков событий
    private ActionListener refreshPanesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            rootPointExplorerPane.refreshContent();
            currentExplorerPane.refreshContent();
            adressPane.refreshContent();
        }
    };

    private ActionListener hiddenListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean show = false;
            String actionCommand = e.getActionCommand();

            if (actionCommand.equals(commandToShowHiddenItem)) {
                show = showHiddenItem.isSelected();
            }
            if (actionCommand.equals(commandToShowHiddenBtn)) {
                show = showHiddenBtn.isSelected();
            }

            showHiddenItem.setSelected(show);
            showHiddenBtn.setSelected(show);

            if (show) {
                showHiddenBtn.setToolTipText("Сейчас срытые элементы отображаются");
            } else {
                showHiddenBtn.setToolTipText("Сейчас скрытые элементы не отображаются");
            }

            tileExplorer.setShowHiddenElements(show);
            tableExplorer.setShowHiddenElements(show);
        }
    };

    private ActionListener upListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                fileSystemExplorer.toUpDirectory();
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
                return;
            }
            currentExplorerPane.refreshContent();
            adressPane.refreshContent();
        }
    };

    private ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    private ActionListener aboutListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String msg = "<html>Простой файловый менеджер на Java<br>Автор: Сергей Лебидко. 2019 г.<br><br>Набор иконок для приложения взят с сайта icon-icons.com";
            JOptionPane.showMessageDialog(frm, msg, "О программе", JOptionPane.PLAIN_MESSAGE);
        }
    };

    private ActionListener setSortedType = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileSorter fileSorter = MainClass.getFileSorter();
            SortMethods currentSortedType = fileSorter.getCurrentSortType();
            SortMethods choiceSortedType = SortMethods.valueOf(e.getActionCommand());
            if (choiceSortedType != currentSortedType) {
                fileSorter.setSortType(choiceSortedType);
                currentExplorerPane.refreshContent();
            }
        }
    };

    private ActionListener setSortedOrder = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileSorter fileSorter = MainClass.getFileSorter();
            SortOrders currentSortedOrder = fileSorter.getCurrentSortOrder();
            SortOrders choiceSortedOrder = SortOrders.valueOf(e.getActionCommand());
            if (choiceSortedOrder != currentSortedOrder) {
                fileSorter.setSortOrder(choiceSortedOrder);
                currentExplorerPane.refreshContent();
            }
        }
    };

}
