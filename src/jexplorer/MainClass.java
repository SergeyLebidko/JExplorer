package jexplorer;

import javax.swing.*;

public class MainClass {

    //Объект для перемещения по каталогам файловой системы. Он общий для всех классов проекта
    private static final FileSystemExplorer fileSystemExplorer=new FileSystemExplorer();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI();
            }
        });
    }

    public static FileSystemExplorer getFileSystemExplorer() {
        return fileSystemExplorer;
    }

}
