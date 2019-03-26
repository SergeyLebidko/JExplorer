package jexplorer.guiclasses.tablepane;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.guiclasses.ExplorerPane;

import javax.swing.*;
import java.awt.*;

public class TableExplorerPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JTable contentTable;
    private TableExplorerModel tableModel;
    private FileSystemExplorer fileSystemExplorer;

    private boolean showHiddenElements;

    public TableExplorerPane() {
        fileSystemExplorer=MainClass.getFileSystemExplorer();

        tableModel=new TableExplorerModel();
        contentTable=new JTable(tableModel);
        scrollPane=new JScrollPane(contentTable);

        refreshContent();
    }

    @Override
    public Component getVisualComponent() {
        return scrollPane;
    }

    @Override
    public void refreshContent() {

//        File[] elements;
//        try {
//            elements = fileSystemExplorer.getCurrentDirectoryElementsList();
//        } catch (Exception e) {
//            GUI gui = MainClass.getGui();
//            gui.showErrorDialog(e.getMessage());
//            return;
//        }
    }

    @Override
    public void setShowHiddenElements(boolean show) {
        if (show == showHiddenElements) return;
        showHiddenElements = show;
        refreshContent();
    }

}
