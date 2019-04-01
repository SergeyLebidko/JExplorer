package jexplorer;

import jexplorer.fileexplorerclasses.FileSorter;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileexplorerclasses.SortTypes;
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
    private JButton upBtn;
    private JButton bigTileViewBtn;
    private JButton smallTileViewBtn;
    private JButton tableViewBtn;
    private JToggleButton showHiddenBtn;
    private String commandToShowHiddenBtn = "btn_element";
    private JButton createFolderBtn;
    private JButton copyBtn;
    private JButton cutBtn;
    private JButton pasteBtn;
    private JButton renameBtn;
    private JButton deleteBtn;
    private JButton propertiesBtn;

    private JMenu fileMenu;
    private JMenuItem createFolderItem;
    private JMenuItem deleteItem;
    private JMenuItem propertiesItem;
    private JMenuItem exitItem;

    private JMenu editMenu;
    private JMenuItem renameItem;
    private JMenuItem copyItem;
    private JMenuItem cutItem;
    private JMenuItem pasteItem;

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
        frm.setMinimumSize(new Dimension(WIDTH_FRM / 2, HEIGHT_FRM / 2));
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

        //Создаем вспомогательную панель для отображения списка корневых точек
        JPanel rPane = new JPanel();
        rPane.setLayout(new BorderLayout());
        rPane.setPreferredSize(new Dimension(WIDTH_FRM / 6, (int) (HEIGHT_FRM * 0.9)));
        rootPointExplorerPane = new RootPointExplorerPane();
        rPane.add(rootPointExplorerPane.getVisualComponent(), BorderLayout.CENTER);

        //Создаем панель, которая будет содаржать панель инструментов, адресную строку, кнопки "Вверх" и "Обновить"
        JPanel upPane = new JPanel();

        //Создаем кнопки "Вверх" и "Обновить" и адресную строку
        upPane.setLayout(new BorderLayout());
        Box adressPane = Box.createHorizontalBox();
        upBtn = new JButton(new ImageIcon("res\\up.png"));
        upBtn.setToolTipText("Вверх");
        refreshPanesBtn = new JButton(new ImageIcon("res\\refresh.png"));
        refreshPanesBtn.setToolTipText("Обновить");
        this.adressPane = new AdressPane();
        adressPane.add(upBtn);
        adressPane.add(Box.createHorizontalStrut(5));
        adressPane.add(refreshPanesBtn);
        adressPane.add(Box.createHorizontalStrut(5));
        adressPane.add(this.adressPane.getVisualComponent());
        upPane.add(adressPane, BorderLayout.SOUTH);

        //Создаем панель инструментов
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        createFolderBtn = new JButton(new ImageIcon("res\\new_folder.png"));
        createFolderBtn.setToolTipText("Создать каталог");
        copyBtn=new JButton(new ImageIcon("res\\copy.png"));
        copyBtn.setToolTipText("Копировать");
        cutBtn=new JButton(new ImageIcon("res\\cut.png"));
        cutBtn.setToolTipText("Вырезать");
        pasteBtn=new JButton(new ImageIcon("res\\paste.png"));
        pasteBtn.setToolTipText("Вставить");
        renameBtn=new JButton(new ImageIcon("res\\rename.png"));
        renameBtn.setToolTipText("Переименовать");
        deleteBtn=new JButton(new ImageIcon("res\\delete.png"));
        deleteBtn.setToolTipText("Удалить");
        propertiesBtn=new JButton(new ImageIcon("res\\properties.png"));
        propertiesBtn.setToolTipText("Свойства");
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
        toolBar.add(createFolderBtn);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(copyBtn);
        toolBar.add(cutBtn);
        toolBar.add(pasteBtn);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(renameBtn);
        toolBar.add(deleteBtn);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(propertiesBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(showHiddenBtn);
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.addSeparator();
        toolBar.add(Box.createHorizontalStrut(10));
        toolBar.add(tableViewBtn);
        toolBar.add(smallTileViewBtn);
        toolBar.add(bigTileViewBtn);
        upPane.add(toolBar, BorderLayout.NORTH);

        //Создаем главное меню и его элементы
        JMenuBar mainMenu = new JMenuBar();

        fileMenu = new JMenu("Файл");
        createFolderItem = new JMenuItem("Создать папку");
        deleteItem=new JMenuItem("Удалить");
        propertiesItem=new JMenuItem("Свойства");
        exitItem = new JMenuItem("Выход");
        fileMenu.add(createFolderItem);
        fileMenu.add(deleteItem);
        fileMenu.addSeparator();
        fileMenu.add(propertiesItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        editMenu=new JMenu("Правка");
        copyItem=new JMenuItem("Копировать");
        cutItem=new JMenuItem("Вырезать");
        pasteItem=new JMenuItem("Вставить");
        renameItem=new JMenuItem("Переименовать");
        editMenu.add(copyItem);
        editMenu.add(cutItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(renameItem);

        sortedMenu = new JMenu("Сортировка");
        sortedByNameItem = new JRadioButtonMenuItem("По имени", true);
        sortedByNameItem.setActionCommand(SortTypes.BY_NAME.getName());
        sortedBySizeItem = new JRadioButtonMenuItem("По размеру");
        sortedBySizeItem.setActionCommand(SortTypes.BY_SIZE.getName());
        sortedByTypeItem = new JRadioButtonMenuItem("По типу");
        sortedByTypeItem.setActionCommand(SortTypes.BY_TYPE.getName());
        sortedByExtensionItem = new JRadioButtonMenuItem("По расширению");
        sortedByExtensionItem.setActionCommand(SortTypes.BY_EXTENSION.getName());
        sortedByDateCreatedItem = new JRadioButtonMenuItem("По дате создания");
        sortedByDateCreatedItem.setActionCommand(SortTypes.BY_DATE_CREATED.getName());
        sortedByDateModifiedItem = new JRadioButtonMenuItem("По дате изменения");
        sortedByDateModifiedItem.setActionCommand(SortTypes.BY_DATE_MODIFIED.getName());

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
        mainMenu.add(editMenu);
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
                bigTilesItem.setSelected(true);
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
                smallTilesItem.setSelected(true);
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
                    tableItem.setSelected(true);
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

        //Добавляем главное меню в окно
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

    //Метод необходим для отображения сообщений об ошибках
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

    //Метод ниже нужен для обновления состояния пунктов меню "Сортировка".
    //Он необходим для корректного отображения выбранных параметров сортировки при изменении их не через меню
    public void refreshSortMenu() {
        SortTypes sortType = MainClass.getFileSorter().getCurrentSortType();
        SortOrders sortOrder = MainClass.getFileSorter().getCurrentSortOrder();

        switch (sortType) {
            case BY_NAME: {
                sortedByNameItem.setSelected(true);
                break;
            }
            case BY_SIZE: {
                sortedBySizeItem.setSelected(true);
                break;
            }
            case BY_TYPE: {
                sortedByTypeItem.setSelected(true);
                break;
            }
            case BY_EXTENSION: {
                sortedByExtensionItem.setSelected(true);
                break;
            }
            case BY_DATE_CREATED: {
                sortedByDateCreatedItem.setSelected(true);
                break;
            }
            case BY_DATE_MODIFIED: {
                sortedByDateModifiedItem.setSelected(true);
            }
        }

        switch (sortOrder) {
            case TO_UP: {
                orderToUpItem.setSelected(true);
                break;
            }
            case TO_DOWN: {
                orderToDownItem.setSelected(true);
            }
        }
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
            String msg = "<html>JExplorer. Простой файловый менеджер на Java<br>Автор: Сергей Лебидко. 2019 г.<br><br>Набор иконок для приложения взят с сайта icon-icons.com";
            JOptionPane.showMessageDialog(frm, msg, "О программе", JOptionPane.PLAIN_MESSAGE);
        }
    };

    private ActionListener setSortedType = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            FileSorter fileSorter = MainClass.getFileSorter();
            SortTypes currentSortedType = fileSorter.getCurrentSortType();
            SortTypes choiceSortedType = SortTypes.valueOf(e.getActionCommand());
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
