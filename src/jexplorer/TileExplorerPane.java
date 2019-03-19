package jexplorer;

import javax.swing.*;
import java.awt.*;

public class TileExplorerPane extends JScrollPane {

    private JPanel contentPane;
    private JLabel[] content;

    class AdaptiveGridLayout implements LayoutManager {

        static final int BIG_CELLS=1;
        static final int SMALL_CELLS=2;

        private final int WIDTH_BIG_CELL = 120;
        private final int HEIGHT_BIG_CELL = 120;

        private final int WIDTH_SMALL_CELL = 80;
        private final int HEIGHT_SMALL_CELL = 80;

        private int currentWidthCell;
        private int currentHeightCell;

        public AdaptiveGridLayout(int sizeCells) {
            setSizeCells(sizeCells);
        }

        public void setSizeCells(int sizeCells){
            if (sizeCells==BIG_CELLS){
                currentWidthCell=WIDTH_BIG_CELL;
                currentHeightCell=HEIGHT_BIG_CELL;
                return;
            }
            if (sizeCells==SMALL_CELLS){
                currentWidthCell=WIDTH_SMALL_CELL;
                currentHeightCell=HEIGHT_SMALL_CELL;
                return;
            }
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

            prefHeightCells = parent.getComponentCount()/getXCellCount();
            if (prefHeightCells==0)prefHeightCells=1;
            if ((prefHeightCells*getXCellCount())<parent.getComponentCount())prefHeightCells++;

            prefHeight=prefHeightCells* currentHeightCell;

            return new Dimension(prefWidth,prefHeight);
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
            int result = TileExplorerPane.this.getViewport().getWidth() / currentWidthCell;
            if (result == 0) result = 1;
            return result;
        }

    }

    public TileExplorerPane() {
        contentPane=new JPanel();
        contentPane.setLayout(new AdaptiveGridLayout(AdaptiveGridLayout.BIG_CELLS));
        add(contentPane);
    }

    //Метод запоняет панель контента элементами
    public void setContent(FileSystemElement[] elements){


    }
}
