package jexplorer.fileexplorerclasses;

import java.io.File;
import java.util.LinkedList;

public class FileSystemExplorer {

    private File currentDirectory;

    public FileSystemExplorer() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        currentDirectory = getUserHomeDir();
    }

    //Метод метод переходит в каталог directory. Возвращает false, если это не удалось
    public boolean openDirectory(File directory){
        if (directory.isFile())return false;
        currentDirectory=directory;
        return true;
    }

    //Метод возвращает список элементов (файлов и папок) текущего каталога
    public File[] getCurrentDirectoryElementsList() {
        File[] result;
        result = currentDirectory.listFiles();
        return result;
    }

    //Метод возвращает текущий каталог
    public File getCurrentDirectory(){
        return currentDirectory;
    }

    //Метод переходит в каталог родительский для текущего. Возвращает false, если это не удалось
    public boolean toUpDirectory(){
        File upDirectory;
        upDirectory=currentDirectory.getParentFile();
        if (upDirectory==null)return false;
        currentDirectory=upDirectory;
        return true;
    }

    //Метод возвращает список дисков
    public LinkedList<File> getDisks() {
        File[] roots = File.listRoots();
        LinkedList<File> result = new LinkedList<>();
        for (File f : roots)
            if (f.exists() & f.canRead()) {
                result.add(f);
            }
        return result;
    }

    //Метод возвращает домашний каталог пользователя
    public File getUserHomeDir() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        return new File(pathToUserHome);
    }

    //Метод возвращает расширение файла file. Если передана ссылка на каталог - возвращает null
    public String getFileExtension(File file) {
        if (file.isDirectory()) return null;
        String nameFile = file.getName();
        int dotPos = nameFile.lastIndexOf(".");
        if ((dotPos == (-1)) | (dotPos == 0) | (dotPos == (nameFile.length() - 1))) return "";
        return (nameFile.substring(dotPos + 1)).toLowerCase();
    }

    //Метод возвращает тип файла из перечисления типов FileTypes
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
