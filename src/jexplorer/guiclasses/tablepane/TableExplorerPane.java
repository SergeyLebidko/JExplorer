package jexplorer.guiclasses.tablepane;

import jexplorer.GUI;
import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSorter;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileexplorerclasses.SortOrders;
import jexplorer.fileexplorerclasses.SortTypes;
import jexplorer.guiclasses.ExplorerPane;
import jexplorer.guiclasses.adressPane.AdressPane;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;

public class TableExplorerPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JTable contentTable;
    private TableExplorerModel tableModel;
    private FileSystemExplorer fileSystemExplorer;

    private final Color backColor = Color.WHITE;

    private boolean showHiddenElements;

    public TableExplorerPane() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        UIManager.put("ScrollBar.width", 20);

        tableModel = new TableExplorerModel();

        contentTable = new JTable(tableModel);
        contentTable.setDefaultRenderer(File.class, new TableExplorerCellRenderer());
        contentTable.getTableHeader().setDefaultRenderer(new TableExplorerHeaderRenderer());
        contentTable.setRowHeight(34);
        contentTable.setShowVerticalLines(false);
        contentTable.setShowHorizontalLines(false);
        contentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumnModel columnModel = contentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(400);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(120);

        scrollPane = new JScrollPane(contentTable);
        scrollPane.getViewport().setBackground(backColor);

        contentTable.addMouseListener(tableClickListener);
        contentTable.getTableHeader().addMouseListener(columnHeaderClickListener);

        refreshContent();
    }

    public void setShowHiddenElements(boolean show) {
        if (show == showHiddenElements) return;
        showHiddenElements = show;
        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Получаем список объектов в текущем каталоге
        File[] elements;
        try {
            elements = fileSystemExplorer.getCurrentDirectoryElementsList();
        } catch (Exception e) {
            GUI gui = MainClass.getGui();
            gui.showErrorDialog(e.getMessage());
            return;
        }

        //Формируем список объектов, которые будут выводится на экран в зависимости от значения флага отображения скрытых элементов
        LinkedList<File> content = new LinkedList<>();
        for (File f : elements) {
            if (f.isHidden() & !showHiddenElements) continue;
            content.add(f);
        }

        //Отправляем данные модели
        tableModel.refreshContent(content.toArray(new File[content.size()]));
    }

    public File[] getSelectedElements() {
        File[] result;
        int[] selectedRows = contentTable.getSelectedRows();
        result = new File[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            result[i] = (File) tableModel.getValueAt(selectedRows[i], 0);
        }
        return result;
    }

    public void selectAllElements() {
        int rowCount = contentTable.getRowCount();
        contentTable.getSelectionModel().setSelectionInterval(0, rowCount - 1);
    }

    //Обраточик кликов по строкам таблицы
    private MouseListener tableClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 & e.getButton() == MouseEvent.BUTTON1) {
                File file = (File) tableModel.getValueAt(contentTable.getSelectedRow(), 0);
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

    //Обработчик кликов по заголовкам столбцов
    private MouseListener columnHeaderClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1 & e.getButton() == MouseEvent.BUTTON1) {
                int columnNumber = contentTable.getTableHeader().columnAtPoint(e.getPoint());
                String columnName = contentTable.getColumnModel().getColumn(columnNumber).getHeaderValue().toString();

                FileSorter fileSorter = MainClass.getFileSorter();
                SortTypes selectedSortType = null;
                SortOrders selectedSortOrder = fileSorter.getCurrentSortOrder();

                switch (columnName) {
                    case "Имя": {
                        selectedSortType = SortTypes.BY_NAME;
                        break;
                    }
                    case "Размер": {
                        selectedSortType = SortTypes.BY_SIZE;
                        break;
                    }
                    case "Тип": {
                        selectedSortType = SortTypes.BY_TYPE;
                        break;
                    }
                    case "Расширение": {
                        selectedSortType = SortTypes.BY_EXTENSION;
                        break;
                    }
                    case "Дата создания": {
                        selectedSortType = SortTypes.BY_DATE_CREATED;
                        break;
                    }
                    case "Дата изменения": {
                        selectedSortType = SortTypes.BY_DATE_MODIFIED;
                    }
                }

                if (fileSorter.getCurrentSortType() == selectedSortType) {
                    if (fileSorter.getCurrentSortOrder() == SortOrders.TO_UP) selectedSortOrder = SortOrders.TO_DOWN;
                    if (fileSorter.getCurrentSortOrder() == SortOrders.TO_DOWN) selectedSortOrder = SortOrders.TO_UP;
                }

                fileSorter.setSortOrder(selectedSortOrder);
                fileSorter.setSortType(selectedSortType);

                GUI gui = MainClass.getGui();
                gui.refreshSortMenu();

                contentTable.getTableHeader().repaint();
                refreshContent();
            }
        }
    };

}
