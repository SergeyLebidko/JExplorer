package jexplorer;

import jexplorer.fileexplorerclasses.FileSorter;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileutilities.DirectoryCreator;
import jexplorer.fileutilities.PropertyReceiver;
import jexplorer.fileutilities.Remover;

import javax.swing.*;

public class MainClass {

    //Объект для перемещения по каталогам файловой системы. Он общий для всех классов проекта
    private final static FileSystemExplorer fileSystemExplorer = new FileSystemExplorer();

    //Объект для сортировки списков файлов и папок
    private final static FileSorter fileSorter = new FileSorter();

    //Объект, необходимый для создания каталогов
    private final static DirectoryCreator directoryCreator = new DirectoryCreator();

    //Объект, необходимый для получения свойств наборов файлов и каталогов
    private final static PropertyReceiver propertyReceiver = new PropertyReceiver();

    //Объект, необходимый для удаления файлов и каталогов
    private final static Remover remover = new Remover();

    //Объект GUI, создающий все компоненты окна приложения
    private final static GUI gui = new GUI();

    public static void main(String[] args) {
    }

    public static FileSystemExplorer getFileSystemExplorer() {
        return fileSystemExplorer;
    }

    public static FileSorter getFileSorter() {
        return fileSorter;
    }

    public static DirectoryCreator getDirectoryCreator() {
        return directoryCreator;
    }

    public static PropertyReceiver getPropertyReceiver(){
        return propertyReceiver;
    }

    public static Remover getRemover(){
        return remover;
    }

    public static GUI getGui() {
        return gui;
    }

}
