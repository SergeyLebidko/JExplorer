package jexplorer.fileexplorerclasses;

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
        result = currentDirectory.listFiles();
        return result;
    }

    public File getCurrentDirectory(){
        return currentDirectory;
    }

    public LinkedList<File> getDisks() {
        File[] roots = File.listRoots();
        LinkedList<File> result = new LinkedList<>();
        for (File f : roots)
            if (f.exists() & f.canRead()) {
                result.add(f);
            }
        return result;
    }

    public File getUserHomeDir() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        return new File(pathToUserHome);
    }

    public String getFileExtension(File file) {
        if (file.isDirectory()) return null;
        String nameFile = file.getName();
        int dotPos = nameFile.lastIndexOf(".");
        if ((dotPos == (-1)) | (dotPos == 0) | (dotPos == (nameFile.length() - 1))) return "";
        return (nameFile.substring(dotPos + 1)).toLowerCase();
    }

    public FileTypes getFileType(File file) {
        if (file.isDirectory()) return null;
        String fileExtension = getFileExtension(file);

        for (FileTypes type: FileTypes.values()){             //Во внешнем цикле перебираем типы
            for (String ext: type.getExtensionsSet()){        //Во внутреннем - связанные с этими типа расширения
                if (fileExtension.equals(ext))return type;
            }
        }

        return FileTypes.OTHER;
    }

}
