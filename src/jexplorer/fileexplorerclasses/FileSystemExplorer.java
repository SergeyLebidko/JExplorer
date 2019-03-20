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

        String[][] fileExtensions = {
                {"exe"},
                {"txt", "doc", "docx", "rtf", "odt"},
                {"xls", "xlsx", "ods"},
                {"ppt", "pptx", "odp"},
                {"rar", "zip", "7z"},
                {"pdf"},
                {"htm", "html"},
                {"jpg", "jpeg", "bmp", "png", "gif", "ico"},
                {"avi", "mkv", "mp4", "wmv", "mpeg", "mpg", "h264", "3gp"},
                {"mp3", "aac", "aac", "flac", "wav", "wave", "wma"}
        };

        FileTypes[] fileTypes = FileTypes.values();
        int findPosition = -1;
        for (int i=0;i<fileExtensions.length;i++){
            for (String s: fileExtensions[i]){
                if (fileExtension.equals(s)){
                    findPosition=i;
                    break;
                }
            }
            if (findPosition!=(-1))break;
        }

        if (findPosition==(-1))return FileTypes.OTHER;
        return fileTypes[findPosition];
    }

}
