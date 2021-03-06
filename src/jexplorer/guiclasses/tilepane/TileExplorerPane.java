package jexplorer.guiclasses.tilepane;

import jexplorer.GUI;
import jexplorer.guiclasses.ExplorerPane;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.MainClass;
import jexplorer.guiclasses.adressPane.AdressPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class TileExplorerPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private JLabel[] content;
    private AdaptiveGridLayout currentLayout;
    private JPopupMenu popupMenu;
    private FileSystemExplorer fileSystemExplorer;
    private Selector selector;

    //Контейнер, сопоставляющий значки на панели и пути к соответствующим элементам в файловой системе
    private HashMap<JLabel, File> contentFileMap;

    private boolean showHiddenElements;

    private final Color backColor = Color.WHITE;
    private final Color textColorForNoHidden = Color.BLACK;
    private final Color textColorForHidden = new Color(130, 130, 130);

    class AdaptiveGridLayout implements LayoutManager {

        static final int BIG_CELLS = 1;
        static final int SMALL_CELLS = 2;

        private final int WIDTH_BIG_CELL = 120;
        private final int HEIGHT_BIG_CELL = 120;

        private final int WIDTH_SMALL_CELL = 80;
        private final int HEIGHT_SMALL_CELL = 80;

        private int currentSizeCell;
        private int currentWidthCell;
        private int currentHeightCell;

        public AdaptiveGridLayout(int sizeCells) {
            setSizeCells(sizeCells);
        }

        public void setSizeCells(int sizeCells) {
            currentSizeCell = sizeCells;
            if (sizeCells == BIG_CELLS) {
                currentWidthCell = WIDTH_BIG_CELL;
                currentHeightCell = HEIGHT_BIG_CELL;
                return;
            }
            if (sizeCells == SMALL_CELLS) {
                currentWidthCell = WIDTH_SMALL_CELL;
                currentHeightCell = HEIGHT_SMALL_CELL;
                return;
            }
        }

        public boolean isBigCells() {
            return currentSizeCell == BIG_CELLS;
        }

        public boolean isSmallCells() {
            return currentSizeCell == SMALL_CELLS;
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            int prefWidth, prefHeight;                //Желаемые ширина и высота в пикселях
            int prefHeightCells;                      //Желаемая высота в клетках сетки

            prefWidth = getXCellCount() * currentWidthCell;

            prefHeightCells = parent.getComponentCount() / getXCellCount();
            if (prefHeightCells == 0) prefHeightCells = 1;
            if ((prefHeightCells * getXCellCount()) < parent.getComponentCount()) prefHeightCells++;
            prefHeight = prefHeightCells * currentHeightCell;

            return new Dimension(prefWidth, prefHeight);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(currentWidthCell, currentHeightCell);
        }

        @Override
        public void layoutContainer(Container parent) {
            int x, y, i;
            x = 0;
            y = 0;
            i = 0;
            for (Component c : parent.getComponents()) {
                c.setBounds(x, y, currentWidthCell, currentHeightCell);
                i++;
                if (i == getXCellCount()) {
                    i = 0;
                    y += currentHeightCell;
                }
                x = currentWidthCell * i;
            }
        }

        //Метод возвращает количество компонентов, которе можно расположить в ряд при данном размере контейнера
        private int getXCellCount() {
            int result = scrollPane.getViewport().getWidth() / currentWidthCell;
            if (result == 0) result = 1;
            return result;
        }

    }

    public TileExplorerPane() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        UIManager.put("ScrollBar.width", 20);

        contentPane = new JPanel();
        currentLayout = new AdaptiveGridLayout(AdaptiveGridLayout.BIG_CELLS);
        contentPane.setLayout(currentLayout);
        contentPane.setBackground(backColor);

        scrollPane = new JScrollPane(contentPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPane.revalidate();
            }
        });

        selector = new Selector();
        contentFileMap = new HashMap<>();
        showHiddenElements = false;
        refreshContent();
    }

    //Метод, включающий режим "большие значки"
    public void setBigCells() {
        if (currentLayout.isSmallCells()) currentLayout.setSizeCells(AdaptiveGridLayout.BIG_CELLS);
        refreshContent();
    }

    //Метод, включающий режим "маленькие значки"
    public void setSmallCells() {
        if (currentLayout.isBigCells()) currentLayout.setSizeCells(AdaptiveGridLayout.SMALL_CELLS);
        refreshContent();
    }

    public void setShowHiddenElements(boolean show) {
        if (show == showHiddenElements) return;
        showHiddenElements = show;
        refreshContent();
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва удаляем все прежние элементы
        clearContentPane();
        contentFileMap.clear();

        //Добавляем на панель контента новые элементы
        //Для этого сперва получаем список элементов (файлов и каталогов) текущего каталога
        File[] elements;
        try {
            elements = fileSystemExplorer.getCurrentDirectoryElementsList();
        } catch (Exception e) {
            GUI gui = MainClass.getGui();
            gui.showErrorDialog(e.getMessage());
            return;
        }

        //Затем в зависимости от опции отображения скрытых элементов создаем новый массив меток для отображения элементов
        if (showHiddenElements) {
            content = new JLabel[elements.length];
        } else {
            int countNoHiddens = 0;
            for (File element : elements) {
                if (!element.isHidden()) countNoHiddens++;
            }
            content = new JLabel[countNoHiddens];
        }

        //Создаем метки соответствующие файлам и папкам
        int i = 0;
        for (File element : elements) {
            if (element.isHidden() & !showHiddenElements) continue;
            content[i] = new JLabel();
            contentFileMap.put(content[i], element);
            content[i].addMouseListener(ml);
            setParameters(element, content[i]);
            contentPane.add(content[i]);
            i++;
        }

        //Передаем элементы текущего каталога в селектор для поддержки выделения элементов
        selector.setContent(content, contentFileMap);

        //Перерисовываем панель контента
        contentPane.revalidate();
        contentPane.repaint();
    }

    public File getCurrentDirectory(){
        return fileSystemExplorer.getCurrentDirectory();
    }

    private void clearContentPane() {
        if (contentPane.getComponentCount() == 0) return;
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            contentPane.remove(component);
        }
    }

    public File[] getSelectedElements() {
        return selector.getSelectedFiles();
    }

    public void selectAllElements() {
        selector.selectAll();
    }

    //Ниже идет группа методов, необходимых для установки параметров значков
    private void setParameters(File element, JLabel lab) {
        setTexts(element, lab);
        setIcon(element, lab);
        setColors(element, lab);
        setBorders(lab);
        setAligments(lab);
    }

    private void setTexts(File element, JLabel lab) {
        lab.setText(element.getName());
        String toolTipText = "<html>";

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        if (element.isFile()) {
            toolTipText += "Файл: " + element.getName();

            String type = fileSystemExplorer.getFileType(element).getTooltipText();
            toolTipText += (type.equals("") ? "" : "<br>Тип: " + type);

            long size;
            String postFix = "";
            size = element.length();
            if (size < 1024) {
                postFix = nf.format(size) + " б.";
            }
            if (size >= 1024 & size < 1048576) {
                postFix = nf.format((double) size / 1024) + " Кб.";
            }
            if (size >= 1048576 & size < 1073741824) {
                postFix = nf.format((double) size / 1048576) + " Мб.";
            }
            if (size >= 1073741824) {
                postFix = nf.format((double) size / 1073741824) + " Гб.";
            }
            toolTipText += "<br>Размер: " + postFix;
        }

        if (element.isDirectory()) {
            toolTipText += "Каталог: " + element.getName();
        }

        DateFormat df = DateFormat.getInstance();
        try {
            toolTipText += "<br>Дата создания: " + df.format(fileSystemExplorer.getDateCreated(element));
        } catch (Exception e) {
            toolTipText += "Не удалось установить дату создания";
        }

        try {
            toolTipText += "<br>Дата последнего изменения: " + df.format(fileSystemExplorer.getDateModified(element));
        } catch (Exception e) {
            toolTipText += "Не удалось установить дату последнего изменения";
        }

        lab.setToolTipText(toolTipText);
    }

    private void setIcon(File element, JLabel lab) {
        String path = "res\\tileView\\";

        if (element.isDirectory()) path += "folder";
        if (element.isFile()) path += fileSystemExplorer.getFileType(element).getName();
        if (currentLayout.isBigCells()) path += "_big";
        if (currentLayout.isSmallCells()) path += "_small";
        if (element.isHidden()) path += "_hidden";
        path += ".png";

        lab.setIcon(new ImageIcon(path));
    }

    private void setColors(File element, JLabel lab) {
        lab.setOpaque(true);
        lab.setBackground(backColor);
        if (element.isHidden()) {
            lab.setForeground(textColorForHidden);
        }
        if (!element.isHidden()) {
            lab.setForeground(textColorForNoHidden);
        }
    }

    private void setBorders(JLabel lab) {
        lab.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    private void setAligments(JLabel lab) {
        lab.setHorizontalTextPosition(SwingConstants.CENTER);
        lab.setVerticalTextPosition(SwingConstants.BOTTOM);
        lab.setHorizontalAlignment(SwingConstants.CENTER);
    }

    //Обработчик кликов по элементам панели
    private MouseListener ml = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            //Обрабатываем один щелчек правой кнопкой мышки
            if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON3) {
                if (popupMenu == null) return;
                JLabel lab = (JLabel) e.getSource();
                if (!selector.isSelect(lab))selector.simpleSelect(lab);
                popupMenu.show(lab, e.getX(), e.getY());
                return;
            }

            //Обрабатываем один щелчек левой кнопкой мышки
            if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON1) {
                JLabel lab = (JLabel) e.getSource();
                if (e.getModifiersEx() == 0)
                    selector.simpleSelect(lab);                          //Если на клавиатуре не нажато никаких дополнительных клавиш
                if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)
                    selector.ctrlSelect(lab);      //Если на клавиатуре нажата клавиша Shift
                if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK)
                    selector.shiftSelect(lab);    //Если на клавиатуре нажата клавиша Ctrl
                return;
            }

            //Обрабатываем двойной щелчек левой кнопкой мышки
            if (e.getClickCount() == 2 & e.getButton() == MouseEvent.BUTTON1) {
                JLabel lab = (JLabel) e.getSource();
                File file = contentFileMap.get(lab);
                GUI gui = MainClass.getGui();
                if (!file.exists()) {
                    gui.showErrorDialog("Не получается открыть " + file.getName());
                    return;
                }
                if (file.isFile()) {
                    try {
                        fileSystemExplorer.openFile(file);
                    } catch (Exception ex) {
                        gui.showErrorDialog(ex.getMessage());
                    }
                    return;
                }
                if (file.isDirectory()) {
                    try {
                        fileSystemExplorer.openDirectory(file);
                    } catch (Exception ex) {
                        gui.showErrorDialog(ex.getMessage());
                        return;
                    }
                    AdressPane adressPane = gui.getAdressPane();
                    adressPane.refreshContent();
                    refreshContent();
                }
            }
        }
    };

}
