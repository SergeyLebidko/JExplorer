package jexplorer.guiclasses.tilepane;

import jexplorer.ExplorerPane;
import jexplorer.FileSystemExplorer;
import jexplorer.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class TileExplorerPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private JLabel[] content;
    private AdaptiveGridLayout currentLayout;
    private FileSystemExplorer fileSystemExplorer;

    private final Color backColor = Color.WHITE;
    private final Color textColorForNoHidden = Color.BLACK;
    private final Color textColorForHidden = new Color(130,130,130);


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

        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва очищаем панель контента от прежних элементов
        clearContentPane();

        //Добавляем на панель контента новые элементы
        File[] elements = fileSystemExplorer.getCurrentElementsList();
        content = new JLabel[elements.length];
        int i = 0;

        //Вначале добавляем каталоги
        for (File element : elements) {
            if (!element.isDirectory()) continue;
            content[i] = new JLabel();
            setParameters(element, content[i]);
            contentPane.add(content[i]);
            i++;
        }

        //Затем добавляем файлы
        for (File element : elements) {
            if (!element.isFile()) continue;
            content[i] = new JLabel();
            setParameters(element, content[i]);
            contentPane.add(content[i]);
            i++;
        }

        scrollPane.repaint();
    }

    private void clearContentPane() {
        if (contentPane.getComponentCount() == 0) return;
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            contentPane.remove(component);
        }
    }

    private void setParameters(File element, JLabel lab){
        setText(element, lab);
        setIcon(element, lab);
        setTextColors(element, lab);
        setBorders(lab);
        setAligments(lab);
    }

    private void setText(File element, JLabel lab){
        lab.setText(element.getName());
        lab.setToolTipText(element.getName());
    }

    private void setIcon(File element, JLabel lab) {
        if (element.isDirectory()) {
            if (currentLayout.isBigCells()) {
                if (element.isHidden()) lab.setIcon(new ImageIcon("res\\tileView\\folder_big_hidden.png"));
                if (!element.isHidden()) lab.setIcon(new ImageIcon("res\\tileView\\folder_big.png"));
            }
            if (currentLayout.isSmallCells()) {
                if (element.isHidden()) lab.setIcon(new ImageIcon("res\\tileView\\folder_small_hidden.png"));
                if (!element.isHidden()) lab.setIcon(new ImageIcon("res\\tileView\\folder_small.png"));
            }
        }
        if (element.isFile()){
            if (currentLayout.isBigCells()) {
                //Вставить код выбора подходящего значка
            }
            if (currentLayout.isSmallCells()) {
                //вставить код выбора подходящего значка
            }
        }
    }

    private void setTextColors(File element, JLabel lab){
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

}
