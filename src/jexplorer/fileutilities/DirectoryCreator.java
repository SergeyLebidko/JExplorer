package jexplorer.fileutilities;

import java.io.File;

public class DirectoryCreator {

    private String forbiddenSymbols = "\\/:*?\"<>|";

    public void createDirectory(File parent, String name) throws Exception {
        if (name.equals("")) throw new Exception("Имя папки не может быть пустым");
        for (char c : name.toCharArray()) {
            for (char f : forbiddenSymbols.toCharArray()) {
                if (c == f) throw new Exception("Имя папки не может содержать символы "+forbiddenSymbols);
            }
        }
        if (!parent.exists()) throw new Exception("Каталог " + parent + " не доступен");
        File directory = new File(parent, name);
        if (directory.exists())throw new Exception("Папка "+name+" уже существует");
        if (!directory.mkdir())throw new Exception("Не удалось создать папку "+name);
    }

}
