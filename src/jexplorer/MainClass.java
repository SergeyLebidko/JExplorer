package jexplorer;

import jexplorer.fileexplorerclasses.FileSystemExplorer;

import javax.swing.*;

public class MainClass {

    //Объект для перемещения по каталогам файловой системы. Он общий для всех классов проекта
    private static FileSystemExplorer fileSystemExplorer=new FileSystemExplorer();

    //Объект GUI, создающий все компоненты окна приложения
    private static GUI gui;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui = new GUI();
            }
        });
    }

    public static FileSystemExplorer getFileSystemExplorer() {
        return fileSystemExplorer;
    }

    public static GUI getGui(){
        return gui;
    }

}
