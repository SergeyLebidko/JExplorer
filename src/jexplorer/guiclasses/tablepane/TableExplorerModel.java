package jexplorer.guiclasses.tablepane;

import javax.swing.table.AbstractTableModel;

public class TableExplorerModel extends AbstractTableModel {

    private String[] columnNames={"Имя", "Тип", "Расширение", "Размер", "Дата создания", "Дата изменения"};

    @Override
    public int getRowCount() {
        return 100;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return "Строка "+rowIndex+" Столбец "+columnIndex;
    }

}
