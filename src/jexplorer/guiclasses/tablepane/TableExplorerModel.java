package jexplorer.guiclasses.tablepane;

import javax.swing.table.AbstractTableModel;

public class TableExplorerModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
        return 100;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return "Строка "+rowIndex+" Столбец "+columnIndex;
    }

}
