package jexplorer.guiclasses.adressPane;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.guiclasses.ExplorerPane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;

public class AdressPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private Box contentPane;
    private FileSystemExplorer fileSystemExplorer;

    public AdressPane() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();

        contentPane = Box.createHorizontalBox();
        scrollPane = new JScrollPane(contentPane);

        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва очищаем панель контента от прежних элементов
        clearContentPane();

        File currentPath = fileSystemExplorer.getCurrentDirectory();
        LinkedList<JButton> btnList=new LinkedList<>();
        JButton btn;
        String btnName;
        do {
            btnName = currentPath.getName();
            if (btnName.equals("")){
                btnName=currentPath.toString();
            }
            btn=new JButton(btnName);
            btnList.add(btn);
            currentPath = currentPath.getParentFile();
        } while (currentPath != null);

        while ((btn=btnList.poll())!=null){
            contentPane.add(btn);
            contentPane.add(Box.createHorizontalStrut(5));
        }

        contentPane.add(Box.createHorizontalGlue());
    }

    private void clearContentPane() {
        if (contentPane.getComponentCount() == 0) return;
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            contentPane.remove(component);
        }
    }

}
