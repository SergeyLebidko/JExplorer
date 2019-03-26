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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.HashMap;

public class TileExplorerPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private JLabel[] content;
    private AdaptiveGridLayout currentLayout;
    private FileSystemExplorer fileSystemExplorer;

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

    //Метод, управляющий отображением скрытых элементов
    public void setShowHiddenElements(boolean show) {
        if (show == showHiddenElements) return;
        showHiddenElements = show;
        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва удаляем все прежние элементы
        clearContentPane();
        contentFileMap.clear();

        //Добавляем на панель контента новые элементы
        File[] elements;
        try {
            elements = fileSystemExplorer.getCurrentDirectoryElementsList();
        } catch (Exception e) {
            GUI gui = MainClass.getGui();
            gui.showErrorDialog(e.getMessage());
            return;
        }
        content = new JLabel[elements.length];
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

        contentPane.revalidate();
        contentPane.repaint();
    }

    private void clearContentPane() {
        if (contentPane.getComponentCount() == 0) return;
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            contentPane.remove(component);
        }
    }

    //Ниже идет группа методов, необходимых для установки параметров значков
    private void setParameters(File element, JLabel lab) {
        setTexts(element, lab);
        setIcon(element, lab);
        setTextColors(element, lab);
        setBorders(lab);
        setAligments(lab);
    }

    private void setTexts(File element, JLabel lab) {
        lab.setText(element.getName());
        String toolTipText="<html>";

        NumberFormat nf=NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        if (element.isFile()){
            toolTipText+="Файл: "+element.getName();

            String type=fileSystemExplorer.getFileType(element).getTooltipText();
            toolTipText+=(type.equals("")?"":"<br>Тип: "+type);

            long size;
            String postFix="";
            size=element.length();
            if (size<1024){
                postFix=nf.format(size)+" б.";
            }
            if (size>=1024 & size<1048576){
                postFix=nf.format((double) size/1024)+" Кб.";
            }
            if (size>=1048576 & size<1073741824){
                postFix=nf.format((double)size/1048576)+" Мб.";
            }
            if (size>=1073741824){
                postFix=nf.format((double)size/1073741824)+" Гб.";
            }
            toolTipText+="<br>Размер: "+postFix;
        }

        if (element.isDirectory()){
            toolTipText+="Каталог: "+element.getName();
        }

        DateFormat df=DateFormat.getInstance();
        try {
            toolTipText+="<br>Дата создания: "+df.format(fileSystemExplorer.getDateCreated(element));
        } catch (Exception e) {
            toolTipText+="Не удалось установить дату создания";
        }

        try {
            toolTipText+="<br>Дата последнего изменения: "+df.format(fileSystemExplorer.getDateModified(element));
        } catch (Exception e) {
            toolTipText+="Не удалось установить дату последнего изменения";
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

    private void setTextColors(File element, JLabel lab) {
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
            if (e.getClickCount() == 2 & e.getButton() == MouseEvent.BUTTON1) {
                JLabel lab = (JLabel) e.getSource();
                File file = contentFileMap.get(lab);
                GUI gui = MainClass.getGui();
                if (!file.exists()){
                    gui.showErrorDialog("Не получается открыть "+file.getName());
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
