package jexplorer;

import jexplorer.fileexplorerclasses.FileSorter;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileexplorerclasses.SortTypes;
import jexplorer.fileexplorerclasses.SortOrders;
import jexplorer.fileutilities.*;
import jexplorer.guiclasses.ExplorerPane;
import jexplorer.guiclasses.adressPane.AdressPane;
import jexplorer.guiclasses.rootpane.RootPointExplorerPane;
import jexplorer.guiclasses.tablepane.TableExplorerPane;
import jexplorer.guiclasses.tilepane.TileExplorerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class GUI {

    private final int WIDTH_FRM = 1300;
    private final int HEIGHT_FRM = 850;

    private final JFrame frm;

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
    private JButton selectAllBtn;
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
    private JMenuItem selectAllItem;
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

    private JPopupMenu popupMenu;
    private JMenuItem selectAllPopupItem;
    private JMenuItem copyPopupItem;
    private JMenuItem cutPopupItem;
    private JMenuItem pastePopupItem;
    private JMenuItem renamePopupItem;
    private JMenuItem deletePopupItem;
    private JMenuItem propertiesPopupItem;

    private final int COPY_DIALOG_WIDTH = 500;
    private final int COPY_DIALOG_HEIGHT = 220;

    private JDialog copyDialog;
    private JLabel currentCopyFileLab;
    private JProgressBar fileCopyProgressBar;
    private JProgressBar totalCopyProgress;
    private JButton stopCopyBtn;

    public GUI() {

        //Заменяем текущий Look and Feel системным
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке переключить стиль интерфейса. Работа программы будет прекращена", "Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        //Локализация диалоговых окон
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.inputDialogTitle", "");

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
        rPane.setPreferredSize(new Dimension((int) (WIDTH_FRM * 0.15), (int) (HEIGHT_FRM * 0.9)));
        rootPointExplorerPane = new RootPointExplorerPane();
        rPane.add(rootPointExplorerPane.getVisualComponent(), BorderLayout.CENTER);

        //Создаем панель, которая будет содаржать панель инструментов, адресную строку, кнопки "Вверх" и "Обновить"
        JPanel upPane = new JPanel();

        //Создаем кнопки "Вверх" и "Обновить" и адресную строку
        upPane.setLayout(new BorderLayout());
        Box adressPane = Box.createHorizontalBox();
        adressPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
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
        toolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        toolBar.setFloatable(false);
        createFolderBtn = new JButton(new ImageIcon("res\\new_folder.png"));
        createFolderBtn.setToolTipText("Создать каталог");
        selectAllBtn = new JButton(new ImageIcon("res\\select_all.png"));
        selectAllBtn.setToolTipText("Выделить все");
        copyBtn = new JButton(new ImageIcon("res\\copy.png"));
        copyBtn.setToolTipText("Копировать");
        cutBtn = new JButton(new ImageIcon("res\\cut.png"));
        cutBtn.setToolTipText("Вырезать");
        pasteBtn = new JButton(new ImageIcon("res\\paste.png"));
        pasteBtn.setToolTipText("Вставить");
        pasteBtn.setEnabled(false);
        renameBtn = new JButton(new ImageIcon("res\\rename.png"));
        renameBtn.setToolTipText("Переименовать");
        deleteBtn = new JButton(new ImageIcon("res\\delete.png"));
        deleteBtn.setToolTipText("Удалить");
        propertiesBtn = new JButton(new ImageIcon("res\\properties.png"));
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
        toolBar.add(selectAllBtn);
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
        deleteItem = new JMenuItem("Удалить");
        propertiesItem = new JMenuItem("Свойства");
        exitItem = new JMenuItem("Выход");
        fileMenu.add(createFolderItem);
        fileMenu.add(deleteItem);
        fileMenu.addSeparator();
        fileMenu.add(propertiesItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        editMenu = new JMenu("Правка");
        selectAllItem = new JMenuItem("Выделить все");
        copyItem = new JMenuItem("Копировать");
        cutItem = new JMenuItem("Вырезать");
        pasteItem = new JMenuItem("Вставить");
        pasteItem.setEnabled(false);
        renameItem = new JMenuItem("Переименовать");
        editMenu.add(selectAllItem);
        editMenu.addSeparator();
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

        //Создаем всплывающе меню
        popupMenu = new JPopupMenu();
        selectAllPopupItem = new JMenuItem("Выделить все");
        copyPopupItem = new JMenuItem("Копировать");
        cutPopupItem = new JMenuItem("Вырезать");
        pastePopupItem = new JMenuItem("Вставить");
        pastePopupItem.setEnabled(false);
        renamePopupItem = new JMenuItem("Переименовать");
        deletePopupItem = new JMenuItem("Удалить");
        propertiesPopupItem = new JMenuItem("Свойства");
        popupMenu.add(selectAllPopupItem);
        popupMenu.addSeparator();
        popupMenu.add(copyPopupItem);
        popupMenu.add(cutPopupItem);
        popupMenu.add(pastePopupItem);
        popupMenu.addSeparator();
        popupMenu.add(renamePopupItem);
        popupMenu.add(deletePopupItem);
        popupMenu.addSeparator();
        popupMenu.add(propertiesPopupItem);

        //Добавяляем элементам всплывающего меню слушатели событий
        selectAllPopupItem.addActionListener(selectAllListener);
        copyPopupItem.addActionListener(copyListener);
        cutPopupItem.addActionListener(cutListener);
        pastePopupItem.addActionListener(pasteListener);
        renamePopupItem.addActionListener(renameListener);
        deletePopupItem.addActionListener(deleteListener);
        propertiesPopupItem.addActionListener(propertiesListener);

        //Передаем созданное всплывающее меню файловым панелям
        tableExplorer.setPopupMenu(popupMenu);
        tileExplorer.setPopupMenu(popupMenu);

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
        createFolderItem.addActionListener(createFolderListener);
        deleteItem.addActionListener(deleteListener);
        propertiesItem.addActionListener(propertiesListener);
        exitItem.addActionListener(exitListener);

        selectAllItem.addActionListener(selectAllListener);
        copyItem.addActionListener(copyListener);
        cutItem.addActionListener(cutListener);
        pasteItem.addActionListener(pasteListener);
        renameItem.addActionListener(renameListener);

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
        createFolderBtn.addActionListener(createFolderListener);
        selectAllBtn.addActionListener(selectAllListener);
        copyBtn.addActionListener(copyListener);
        cutBtn.addActionListener(cutListener);
        pasteBtn.addActionListener(pasteListener);
        renameBtn.addActionListener(renameListener);
        deleteBtn.addActionListener(deleteListener);
        propertiesBtn.addActionListener(propertiesListener);

        //Создаем диалог копирования файлов
        copyDialog = new JDialog(frm, true);
        copyDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        copyDialog.setSize(new Dimension(COPY_DIALOG_WIDTH, COPY_DIALOG_HEIGHT));
        copyDialog.setResizable(false);

        currentCopyFileLab = new JLabel();
        fileCopyProgressBar = new JProgressBar();
        fileCopyProgressBar.setMinimum(0);
        fileCopyProgressBar.setMaximum(100);
        totalCopyProgress = new JProgressBar();
        totalCopyProgress.setMinimum(0);
        totalCopyProgress.setMaximum(100);
        stopCopyBtn = new JButton("Отмена");

        JPanel dialogPane = new JPanel();
        dialogPane.setLayout(new GridLayout(0, 1,5,6));
        dialogPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Box box1 = Box.createHorizontalBox();
        Box box2 = Box.createHorizontalBox();
        Box box3 = Box.createHorizontalBox();

        box1.add(currentCopyFileLab);
        box1.add(Box.createHorizontalGlue());
        box2.add(new JLabel("Общий прогресс:"));
        box2.add(Box.createHorizontalGlue());
        box3.add(Box.createHorizontalGlue());
        box3.add(stopCopyBtn);

        dialogPane.add(box1);
        dialogPane.add(fileCopyProgressBar);
        dialogPane.add(box2);
        dialogPane.add(totalCopyProgress);
        dialogPane.add(box3);
        copyDialog.setContentPane(dialogPane);

        stopCopyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Copier copier = MainClass.getCopier();
                copier.stop();
                copyDialog.setVisible(false);
            }
        });

        //Задаем горячие клавиши для пунктов меню
        contentPane.setFocusable(false);
        createFolderItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK, true));
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.KEY_LOCATION_UNKNOWN));
        propertiesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        renameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,KeyEvent.KEY_LOCATION_UNKNOWN));

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
            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();
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

    private ActionListener createFolderListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(frm, "Введите имя папки", "");
            if (name == null) return;
            name = name.trim();
            DirectoryCreator directoryCreator = MainClass.getDirectoryCreator();
            try {
                directoryCreator.createDirectory(currentExplorerPane.getCurrentDirectory(), name);
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
                return;
            }
            currentExplorerPane.refreshContent();
        }
    };

    private ActionListener selectAllListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentExplorerPane.selectAllElements();
        }
    };

    private ActionListener copyListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentSelectedContentToClipboard(false);
            setPasteToolTip(currentExplorerPane.getSelectedElements());
            setEnabledPasteComponents();
        }
    };

    private ActionListener cutListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentSelectedContentToClipboard(true);
            setPasteToolTip(currentExplorerPane.getSelectedElements());
            setEnabledPasteComponents();
        }
    };

    private ActionListener pasteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Устанавливаем параметры копирования
            Copier copier = MainClass.getCopier();
            copier.initialCopier(currentExplorerPane.getCurrentDirectory());

            //Устанавливаем расположение диалога копирования по центру окна
            int xPos = frm.getLocation().x + (frm.getWidth() / 2) - (COPY_DIALOG_WIDTH / 2);
            int yPos = frm.getLocation().y + (frm.getHeight() / 2) - (COPY_DIALOG_HEIGHT / 2);
            copyDialog.setLocation(xPos, yPos);

            //Устанавливаем значения индикаторов прогресса
            fileCopyProgressBar.setValue(0);
            totalCopyProgress.setValue(0);

            //Запускаем поток копирования
            Thread trd = new Thread(copier);
            trd.start();

            //Выводим диалог копирования на экран (в модальном режиме)
            copyDialog.setVisible(true);

            //Выводим отчет об ошибках, возникших во время копирования
            ResultSet resultSet=copier.getResultSet();
            if (!resultSet.isErrTextListEmpty()){
                JPanel listPane = createResultPanel(resultSet.getErrText());
                JPanel pane = new JPanel();
                pane.setLayout(new GridLayout(0,1));
                pane.add(new JLabel("Во время копирования возникли следующие ошибки:"));
                pane.add(listPane);
                JOptionPane.showMessageDialog(frm, pane, "Внимание", JOptionPane.INFORMATION_MESSAGE);
            }

            //Обновляем элементы интерфейса после завершения копирования
            clearPasteToolTip();
            currentExplorerPane.refreshContent();
            setEnabledPasteComponents();
        }
    };

    private ActionListener renameListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File[] files = currentExplorerPane.getSelectedElements();
            if (files.length == 0) return;

            String name = "";
            if (files.length == 1) {
                name = files[0].getName();
            }

            name = JOptionPane.showInputDialog(frm, "Введите новое имя", name);
            if (name == null) return;
            name = name.trim();

            File root = currentExplorerPane.getCurrentDirectory();
            Renamer renamer = MainClass.getRenamer();
            ResultSet resultSet;
            try {
                resultSet = renamer.rename(root, files, name);
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
                return;
            }

            //Обработка ошибок переименования
            if (!resultSet.isErrFileListEmpty()) {
                JLabel lab = new JLabel();
                String text = "<html>Следующие объекты переименовать не удалось:";
                for (File file : resultSet.getErrFile()) {
                    text += "<br>" + file.getAbsolutePath();
                }
                lab.setText(text);
                JOptionPane.showMessageDialog(frm, lab, "", JOptionPane.INFORMATION_MESSAGE);
            }

            currentExplorerPane.refreshContent();
        }
    };

    private ActionListener deleteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File[] removeList = currentExplorerPane.getSelectedElements();
            if (removeList.length == 0) return;
            Remover remover = MainClass.getRemover();
            ResultSet resultSet = remover.remove(removeList);

            if (!resultSet.isErrFileListEmpty()) {
                JLabel lab = new JLabel();
                String text = "<html>Следующие объекты удалить не удалось:";
                for (File file : resultSet.getErrFile()) {
                    text += "<br>" + file.getAbsolutePath();
                }
                lab.setText(text);
                JOptionPane.showMessageDialog(frm, lab, "", JOptionPane.INFORMATION_MESSAGE);
            }

            //Отображаем количество удаленных объектов
            if (!resultSet.isResultListEmpty()){
                JOptionPane.showMessageDialog(frm, createResultPanel(resultSet.getResult()), "Удалено", JOptionPane.INFORMATION_MESSAGE);
            }

            currentExplorerPane.refreshContent();
        }
    };

    private ActionListener propertiesListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File[] selectedFiles = currentExplorerPane.getSelectedElements();
            if (selectedFiles.length == 0) return;
            PropertyReceiver propertyReceiver = MainClass.getPropertyReceiver();
            ResultSet resultSet;

            try {
                resultSet = propertyReceiver.getPropertySet(selectedFiles);
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
                return;
            }

            if (resultSet.isResultListEmpty()) {
                showErrorDialog("Не удалось получить свойства выделенных объектов");
            }

            //Выводим список объектов, свойства которых получить не удалось
            if (!resultSet.isErrFileListEmpty()) {
                JLabel lab = new JLabel();
                String text = "<html>Свойства следующих объектов получить не удалось:";
                for (File file : resultSet.getErrFile()) {
                    text += "<br>" + file.getAbsolutePath();
                }
                lab.setText(text);
                JOptionPane.showMessageDialog(frm, lab, "", JOptionPane.INFORMATION_MESSAGE);
            }

            //Отображаем список свойств
            JOptionPane.showMessageDialog(frm, createResultPanel(resultSet.getResult()), "Свойства", JOptionPane.INFORMATION_MESSAGE);

        }
    };

    private JPanel createResultPanel(List<String> resultList) {
        String name;
        String value;
        int pos;
        JPanel resultPane = new JPanel();
        resultPane.setLayout(new GridLayout(0, 1));
        for (String str : resultList) {
            Box line = Box.createHorizontalBox();
            JLabel nameLab = new JLabel();
            JLabel valueLab = new JLabel();
            pos = str.indexOf('*');
            name = str.substring(0, pos);
            value = str.substring(pos + 1, str.length());
            nameLab.setText(name);
            valueLab.setText(value);
            line.add(nameLab);
            line.add(Box.createHorizontalStrut(40));
            line.add(Box.createHorizontalGlue());
            line.add(valueLab);
            resultPane.add(line);
        }
        return resultPane;
    }

    //Метод помещает выделенные файлы и каталоги в буфер обмена
    //Если его параметр равен true, то при вставке в новое место они должны быть удалены в каталоге-источнике
    private void currentSelectedContentToClipboard(boolean deleteFromSource) {
        File[] fileList = currentExplorerPane.getSelectedElements();
        if (fileList.length == 0) return;
        File root = currentExplorerPane.getCurrentDirectory();
        ClipboardContent clipboardContent = new ClipboardContent(root, fileList, deleteFromSource);
        Clipboard clipboard = MainClass.getClipboard();
        clipboard.put(clipboardContent);
    }

    //Метод устанавливает подсказку для пунктов меню и кнопки "Вставить"
    private void setPasteToolTip(File[] files){
        if (files.length==0){
            clearPasteToolTip();
            return;
        }
        String toolTipText="<html>В буфере обмена:";
        for (File file: files){
            toolTipText+="<br>    "+file.getName();
        }
        pasteBtn.setToolTipText(toolTipText);
        pasteItem.setToolTipText(toolTipText);
        pastePopupItem.setToolTipText(toolTipText);
    }

    //Метод очищает подскахку для пунктов меню и кнопки "Вставить"
    private void clearPasteToolTip(){
        pasteBtn.setToolTipText("Вставить");
        pasteItem.setToolTipText(null);
        pastePopupItem.setToolTipText(null);
    }

    //Метод управляет доступностью элементов "вставить" в зависимости от того, есть в буфере обмена данные или нет
    private void setEnabledPasteComponents() {
        Clipboard clipboard = MainClass.getClipboard();
        pasteBtn.setEnabled(!clipboard.isEmpty());
        pasteItem.setEnabled(!clipboard.isEmpty());
        pastePopupItem.setEnabled(!clipboard.isEmpty());
    }

    //Ниже идет группа методов, необходимых для взаимодействия потока копирования файлов с потоком GUI
    public void setCopyDialogTitle(String title) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                copyDialog.setTitle(title);
            }
        });
    }

    public void setCurrentCopyFileName(String fileName){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                currentCopyFileLab.setText(fileName);
            }
        });
    }

    public void setFileProgress(int progress){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int value;
                value=progress;
                if (progress<0)value=0;
                if (progress>100)value=100;
                fileCopyProgressBar.setValue(value);
            }
        });
    }

    public void setTotalProgress(int progress){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int value;
                value=progress;
                if (progress<0)value=0;
                if (progress>100)value=100;
                totalCopyProgress.setValue(value);
            }
        });
    }

    public int showCopyConflictDialog(String conflictDescription) {
        class Answer implements Runnable {

            int result;
            String msg;

            public Answer(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {
                JPanel pane = new JPanel();
                pane.setLayout(new GridLayout(0, 1));
                JLabel lab = new JLabel(msg);
                Box box = Box.createHorizontalBox();

                JRadioButton replaceRadio = new JRadioButton("Заменить файл в папке назначения", true);
                JRadioButton skipRadio=new JRadioButton("Не копировать файл");
                JRadioButton saveBothRadio=new JRadioButton("Копировать и сохранить оба файла");
                ButtonGroup bg = new ButtonGroup();
                bg.add(replaceRadio);
                bg.add(skipRadio);
                bg.add(saveBothRadio);
                JCheckBox defaultActionCheckBox = new JCheckBox("Применить это действие в случае всех последующих конфликтов", false);

                box.add(lab);
                box.add(Box.createHorizontalGlue());

                pane.add(box);
                pane.add(replaceRadio);
                pane.add(skipRadio);
                pane.add(saveBothRadio);
                pane.add(defaultActionCheckBox);

                int answer;
                do {
                    answer = JOptionPane.showConfirmDialog(copyDialog, pane, "", (-1),JOptionPane.QUESTION_MESSAGE);
                } while (answer == JOptionPane.CLOSED_OPTION);

                if (replaceRadio.isSelected() & !defaultActionCheckBox.isSelected())result=Copier.REPLACE;
                if (skipRadio.isSelected() & !defaultActionCheckBox.isSelected())result=Copier.SKIP;
                if (saveBothRadio.isSelected() & !defaultActionCheckBox.isSelected())result=Copier.SAVE_BOTH;
                if (replaceRadio.isSelected() & defaultActionCheckBox.isSelected())result=Copier.REPLACE_ALL;
                if (skipRadio.isSelected() & defaultActionCheckBox.isSelected())result=Copier.SKIP_ALL;
                if (saveBothRadio.isSelected() & defaultActionCheckBox.isSelected())result=Copier.SAVE_BOTH_ALL;
            }

            int getResult() {
                return result;
            }

        }
        Answer answer = new Answer(conflictDescription);

        try {
            SwingUtilities.invokeAndWait(answer);
        } catch (Exception e) {
        }

        return answer.getResult();
    }

    public void closeCopyDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                copyDialog.setVisible(false);
            }
        });
    }

}
