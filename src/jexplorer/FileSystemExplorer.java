package jexplorer;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;

public class FileSystemExplorer {

    private File currentDirectory;

    public FileSystemExplorer() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        currentDirectory = getUserHomeDir();
    }

    //Метод возвращает список элементов (файлов и папок) текущего каталога
    public File[] getCurrentElementsList() {
        File[] result;
        result=currentDirectory.listFiles();
        return result;
    }

    public LinkedList<File> getDisks() {
        File[] roots = File.listRoots();
        LinkedList<File> result = new LinkedList<>();
        for (File f : roots) if (f.exists() & f.canRead()) {
            result.add(f);
        }
        return result;
    }

    public File getUserHomeDir() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        return new File(pathToUserHome);
    }

}
