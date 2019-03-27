package jexplorer.guiclasses.tablepane;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.SortTypes;
import jexplorer.fileexplorerclasses.SortOrders;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableExplorerHeaderRenderer extends DefaultTableCellRenderer {

    private final Color backColor = new Color(220, 220, 220);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lab = (JLabel) (super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));

        Dimension dim = lab.getPreferredSize();
        double labWidth = dim.getWidth();
        lab.setPreferredSize(new Dimension((int) labWidth, 28));
        lab.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        lab.setBackground(backColor);
        lab.setIcon(new ImageIcon(""));

        String columnName = value.toString();
        SortTypes sortType = MainClass.getFileSorter().getCurrentSortType();
        SortOrders sortOrder = MainClass.getFileSorter().getCurrentSortOrder();
        ImageIcon icon=null;

        if (sortOrder == SortOrders.TO_UP) icon = new ImageIcon("res\\to_up.png");
        if (sortOrder == SortOrders.TO_DOWN) icon = new ImageIcon("res\\to_down.png");

        if (sortType==SortTypes.BY_NAME & columnName.equals("Имя"))lab.setIcon(icon);
        if (sortType==SortTypes.BY_SIZE & columnName.equals("Размер"))lab.setIcon(icon);
        if (sortType==SortTypes.BY_TYPE & columnName.equals("Тип"))lab.setIcon(icon);
        if (sortType==SortTypes.BY_EXTENSION & columnName.equals("Расширение"))lab.setIcon(icon);
        if (sortType==SortTypes.BY_DATE_CREATED & columnName.equals("Дата создания"))lab.setIcon(icon);
        if (sortType==SortTypes.BY_DATE_MODIFIED & columnName.equals("Дата изменения"))lab.setIcon(icon);

        return lab;
    }

}
