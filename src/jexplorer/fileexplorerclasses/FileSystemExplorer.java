package jexplorer.fileexplorerclasses;

import jexplorer.MainClass;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedList;

public class FileSystemExplorer {

    private File currentDirectory;

    public FileSystemExplorer() {
        String pathToUserHome;
        pathToUserHome = System.getProperty("user.home");
        currentDirectory = getUserHomeDir();
    }

    //Метод метод переходит в каталог directory. Возвращает false, если это не удалось
    public void openDirectory(File directory) throws Exception {
        if (directory.isFile()) return;
        if (!directory.exists() || !directory.canRead() || directory.listFiles() == null) {
            String name = directory.getName();
            if (name.equals("")) name = directory.getAbsolutePath();
            throw new Exception("Не получается открыть " + name);
        }
        currentDirectory = directory;
    }

    //Метод открывает файл, используя средства операционной системы
    public void openFile(File file) throws Exception {
        if (file.isDirectory()) return;
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            throw new Exception("Не получается открыть: " + file.getName());
        }
    }

    //Метод возвращает список элементов (файлов и папок) текущего каталога
    public File[] getCurrentDirectoryElementsList() throws Exception {
        File[] result;
//        result = currentDirectory.listFiles();
//        if (result == null) {
//            throw new Exception("Не могу прочитать содержимое папки " + currentDirectory.getName());
//        }

        LinkedList<File> directroryContent = new LinkedList<>();

        try (DirectoryStream<Path> directoryStream=Files.newDirectoryStream(currentDirectory.toPath())) {
            for (Path element: directoryStream){
                directroryContent.add(element.toFile());
            }
        }catch (Exception ex){
            throw new Exception("Не могу прочитать содержимое папки " + currentDirectory.getName());
        }

        result=new File[directroryContent.size()];
        directroryContent.toArray(result);

        FileSorter fileSorter = MainClass.getFileSorter();
        return fileSorter.sort(result);
    }

    //Метод возвращает текущий каталог
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    //Метод переходит в каталог родительский для текущего. Возвращает false, если это не удалось
    public void toUpDirectory() throws Exception {
        File upDirectory;
        upDirectory = currentDirectory.getParentFile();
        if (upDirectory == null) return;
        openDirectory(upDirectory);
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

    //Метод возвращает имя файла (без расширения). Если передана ссылка на каталог - возвращает null
    public String getFileName(File file) {
        if (file.isDirectory()) return null;
        String nameFile = file.getName();
        int dotPos = nameFile.lastIndexOf(".");
        if ((dotPos == (-1)) | (dotPos == 0) | (dotPos == (nameFile.length() - 1))) return nameFile;
        return nameFile.substring(0, dotPos);
    }

    //Метод возвращает расширение файла file. Если передана ссылка на каталог - возвращает null
    public String getFileExtension(File file) {
        if (file.isDirectory()) return null;
        String nameFile = file.getName();
        int dotPos = nameFile.lastIndexOf(".");
        if ((dotPos == (-1)) | (dotPos == 0) | (dotPos == (nameFile.length() - 1))) return "";
        return (nameFile.substring(dotPos + 1)).toLowerCase();
    }

    //Метод возвращает тип файла file. Перечень типов файлов определен в перечеслении FileTypes. Если передана ссылка на каталог - возвращает null
    public FileTypes getFileType(File file) {
        if (file.isDirectory()) return null;
        String fileExtension = getFileExtension(file);

        for (FileTypes type : FileTypes.values()) {             //Во внешнем цикле перебираем типы
            for (String ext : type.getExtensionsSet()) {        //Во внутреннем - связанные с этими типа расширения
                if (fileExtension.equals(ext)) return type;
            }
        }

        return FileTypes.OTHER;
    }

    //Метод возвращает дату создания объекта
    public Date getDateCreated(File file) throws Exception {
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            throw new Exception("Не удалось получить дату создания");
        }
        return new Date(attr.creationTime().toMillis());
    }

    //Метод возвращает дату последней модификации объекта
    public Date getDateModified(File file) throws Exception {
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        } catch (IOException e) {
            throw new Exception("Не удалось получить дату последнего изменения");
        }
        return new Date(attr.lastModifiedTime().toMillis());
    }

}
