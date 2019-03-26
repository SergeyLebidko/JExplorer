package jexplorer.guiclasses.tablepane;

import javax.swing.table.AbstractTableModel;
import java.io.File;

public class TableExplorerModel extends AbstractTableModel {

    private String[] columnNames={"Имя", "Тип", "Расширение", "Размер", "Дата создания", "Дата изменения"};
    private File[] content;

    public TableExplorerModel() {
        content=new File[0];
    }

    public void refreshContent(File[] content){
        this.content=content;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return content.length;
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
    public Class<?> getColumnClass(int columnIndex) {
        return File.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return content[rowIndex];
    }

}
