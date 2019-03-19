package jexplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TileExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private JLabel[] content;
    private AdaptiveGridLayout currentLayout;
    private FileSystemExplorer fileSystemExplorer;

    private final Color backColor=Color.WHITE;

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

        public boolean isBigCells(){
            return currentSizeCell==BIG_CELLS;
        }

        public boolean isSmallCells(){
            return currentSizeCell==SMALL_CELLS;
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
        fileSystemExplorer=MainClass.getFileSystemExplorer();

        contentPane = new JPanel();
        currentLayout = new AdaptiveGridLayout(AdaptiveGridLayout.BIG_CELLS);
        contentPane.setLayout(currentLayout);
        contentPane.setBackground(backColor);

        scrollPane=new JScrollPane(contentPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPane.revalidate();
            }
        });

        setContent(fileSystemExplorer.getCurrentElementsList());
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void setContent(FileSystemElement[] elements) {
        //Сперва очищаем панель контента от прежних элементов
        clearContentPane();

        //Добавляем на панель контента новые элементы
        content = new JLabel[elements.length];
        int i = 0;
        for (FileSystemElement element : elements) {
            content[i] = new JLabel();
            content[i].setText(element.name);
            content[i].setToolTipText(element.toolTipText);
            if (currentLayout.isBigCells()){
                content[i].setIcon(new ImageIcon("res\\tileView\\folder_big.png"));
            }
            if (currentLayout.isSmallCells()){
                content[i].setIcon(new ImageIcon("res\\tileView\\folder_small.png"));
            }
            content[i].setHorizontalTextPosition(SwingConstants.CENTER);
            content[i].setVerticalTextPosition(SwingConstants.BOTTOM);
            content[i].setHorizontalAlignment(SwingConstants.CENTER);
            content[i].setBackground(backColor);
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

}
