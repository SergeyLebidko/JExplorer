package jexplorer.guiclasses.tablepane;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Enumeration;

public class TableExplorerCellRenderer extends DefaultTableCellRenderer {

    private FileSystemExplorer fileSystemExplorer;

    private final Color backColorForNoSelect = Color.WHITE;
    private final Color backColorForSelect = new Color(168, 201, 255);
    private final Color textColorForNoHidden = Color.BLACK;
    private final Color textColorForHidden = new Color(130, 130, 130);

    public TableExplorerCellRenderer() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lab = (JLabel) (super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column));
        File element = (File) value;

        //Сбрасываем иконку
        lab.setIcon(new ImageIcon(""));

        //Сбрасываем выравнивание
        lab.setHorizontalAlignment(SwingConstants.CENTER);

        //Устанавливаем параметры цвета фона и текста (они общие для всех столбцов)
        if (isSelected) {
            lab.setBackground(backColorForSelect);
        } else lab.setBackground(backColorForNoSelect);
        if (element.isHidden()) {
            lab.setForeground(textColorForHidden);
        } else lab.setForeground(textColorForNoHidden);

        //Выставляем параметры отображения ячеек в зависимости от номера столбца
        String labText = "";

        String columnName=table.getColumnModel().getColumn(column).getHeaderValue().toString();
        //Выводим имя объекта
        if (columnName.equals("Имя")) {
            if (element.isFile()){
                labText=fileSystemExplorer.getFileName(element);
            }else labText = element.getName();
            String path = "res\\tableView\\";
            if (element.isDirectory()) path += "folder";
            if (element.isFile()) path += fileSystemExplorer.getFileType(element).getName();
            if (element.isHidden()) path += "_hidden";
            path += ".png";
            lab.setIcon(new ImageIcon(path));
            lab.setHorizontalAlignment(SwingConstants.LEFT);
        }

        //Выводим наименование типа
        if (columnName.equals("Тип")) {
            if (element.isFile()) {
                labText = fileSystemExplorer.getFileType(element).getTooltipText();
            }
        }

        //Выводим расширение
        if (columnName.equals("Расширение")) {
            if (element.isFile()) {
                labText = fileSystemExplorer.getFileExtension(element);
                if (labText == null) labText = "";
            }
        }

        //Выводим размер
        if (columnName.equals("Размер")) {
            if (element.isFile()) {
                NumberFormat nf = NumberFormat.getInstance();
                long size = element.length();
                labText = nf.format(size);
                lab.setHorizontalAlignment(SwingConstants.RIGHT);
            }
        }

        //Выводим дату создания
        if (columnName.equals("Дата создания")) {
            DateFormat df=DateFormat.getInstance();
            try {
                Date dateCreated=fileSystemExplorer.getDateCreated(element);
                labText=df.format(dateCreated);
            }catch (Exception e){}
        }

        //Выводим дату последнего изменения
        if (columnName.equals("Дата изменения")){
            DateFormat df=DateFormat.getInstance();
            try {
                Date dateModified=fileSystemExplorer.getDateModified(element);
                labText=df.format(dateModified);
            }catch (Exception e){}
        }

        lab.setText(labText);

        return lab;
    }

}
