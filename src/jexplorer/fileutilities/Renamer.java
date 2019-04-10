package jexplorer.fileutilities;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Renamer {

    private FileSystemExplorer fileSystemExplorer;
    private String forbiddenSymbols = "\\/:*?\"<>|";
    private Map<String, Integer> numMap;

    public Renamer() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        numMap = new HashMap<>();
    }

    public ResultSet rename(File root, File[] files, String name) throws Exception {

        //Сперва проверяем новое имя на допустимость
        if (name.equals("")) throw new Exception("Имя не может быть пустым");
        for (char c : name.toCharArray()) {
            for (char f : forbiddenSymbols.toCharArray()) {
                if (c == f) throw new Exception("Имя не может содержать символы " + forbiddenSymbols);
            }
        }

        ResultSet result = new ResultSet();
        boolean success;
        File dest;

        //Если переименовываем один объект
        if (files.length == 1) {
            File file = files[0];
            if (!file.exists()) throw new Exception("Не удалось переименовать " + file.getName());

            dest = new File(root, name);
            if (dest.exists()) throw new Exception("Объект с таким именем уже существует");
            success = file.renameTo(dest);
            if (!success) throw new Exception("Не удалось переименовать " + file.getName());
            return result;
        }

        //Если переименовываем много объектов
        int num;
        String ext;
        numMap.clear();
        if (files.length > 1) {
            for (File file : files) {
                if (!file.exists()) {
                    result.addErrFile(file);
                    continue;
                }
                if (file.isFile()) {
                    ext = fileSystemExplorer.getFileExtension(file);
                    if (!ext.equals("")) ext = "." + ext;
                } else {
                    ext = "";
                }
                dest = new File(root, name + "(" + getNextNum(file) + ")" + ext);
                success = file.renameTo(dest);
                if (!success) {
                    result.addErrFile(file);
                }
            }
        }

        return result;
    }

    private int getNextNum(File file) {
        Integer nextNum;
        String ext;

        if (file.isDirectory()) {
            ext = "*folder";
        } else {
            ext = fileSystemExplorer.getFileExtension(file);
        }

        nextNum = numMap.get(ext);
        if (nextNum == null) {
            nextNum = 1;
         }
        numMap.put(ext, nextNum + 1);

        return nextNum;
    }

}
