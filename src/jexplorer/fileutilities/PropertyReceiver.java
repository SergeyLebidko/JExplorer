package jexplorer.fileutilities;

import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.fileexplorerclasses.FileTypes;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

public class PropertyReceiver {

    private class DirectoryWalker extends SimpleFileVisitor<Path> {

        private int dirCount;
        private int fileCount;
        private long totalFileSize;

        public void calculate(File dir) throws IOException {
            dirCount = 0;
            fileCount = 0;
            totalFileSize = 0;
            Files.walkFileTree(dir.toPath(), this);
            dirCount--;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            dirCount++;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            fileCount++;
             totalFileSize += attrs.size();
            return FileVisitResult.CONTINUE;
        }

        public int getDirCount() {
            return dirCount;
        }

        public int getFileCount() {
            return fileCount;
        }

        public long getTotalFileSize() {
            return totalFileSize;
        }
    }

    private DirectoryWalker directoryWalker;

    public PropertyReceiver() {
        directoryWalker = new DirectoryWalker();
    }

    public ResultSet getPropertySet(File[] files) throws Exception {
        ResultSet result = new ResultSet();

        //Вариант 1. Передан один объект
        if (files.length == 1) {
            File file = files[0];
            if (!file.exists()) throw new Exception("Невозможно получить свойства "+file.getName());

            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();

            //Если этот объект - файл
            if (file.isFile()) {
                result.addResult("Имя", file.getName());
                FileTypes fileType = fileSystemExplorer.getFileType(file);
                if (fileType != FileTypes.OTHER) {
                    result.addResult("Тип файла", fileType.getTooltipText());
                }
                result.addResult("Размер", sizeToString(file.length()));
                try {
                    Date dateCreated = fileSystemExplorer.getDateCreated(file);
                    result.addResult("Дата создания", dateToString(dateCreated));
                } catch (Exception ex) {
                }
                try {
                    Date dateModified = fileSystemExplorer.getDateModified(file);
                    result.addResult("Дата создания", dateToString(dateModified));
                } catch (Exception ex) {
                }
                return result;
            }

            //Если этот объект - каталог
            if (file.isDirectory()) {
                result.addResult("Имя", file.getName());
                result.addResult("Тип", "Папка");
                try {
                    directoryWalker.calculate(file);
                } catch (Exception ex) {
                    throw new Exception("Невозможно получить свойства " + file.getName());
                }
                result.addResult("Размер", sizeToString(directoryWalker.getTotalFileSize()));
                result.addResult("Количество файлов", countToString(directoryWalker.getFileCount()));
                result.addResult("Количество папок", countToString(directoryWalker.getDirCount()));
                return result;
            }

        }

        //Вариант 2. Передана группа объектов
        if (files.length > 1) {
            int fileCount = 0;           //Количество файлов в группе
            int dirCount = 0;            //Количество папок в группе
            long totalFileSize = 0;      //Общий размер файлов в группе
            for (File file : files) {
                if (!file.exists()) {
                    result.addErrFile(file);
                    continue;
                }
                if (file.isFile()){
                    fileCount++;
                    totalFileSize+=file.length();
                    continue;
                }
                if (file.isDirectory()){
                    dirCount++;
                    try {
                        directoryWalker.calculate(file);
                    }catch (Exception ex){
                        result.addErrFile(file);
                        continue;
                    }
                    fileCount+=directoryWalker.getFileCount();
                    totalFileSize+=directoryWalker.getTotalFileSize();
                    dirCount+=directoryWalker.getDirCount();
                }
            }
            if (fileCount!=0 & dirCount==0){
                result.addResult("Тип", "Файлы");
            }
            if (fileCount==0 & dirCount!=0){
                result.addResult("Тип", "Папки");
            }
            if (fileCount!=0 & dirCount!=0){
                result.addResult("Тип", "Файлы и папки");
            }
            result.addResult("Размер", sizeToString(totalFileSize));
            result.addResult("Количество файлов", countToString(fileCount));
            result.addResult("Количество папок", countToString(dirCount));
        }

        return result;
    }

    private String sizeToString(long size) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(size) + " байт";
    }

    private String dateToString(Date date) {
        DateFormat df = DateFormat.getInstance();
        return df.format(date);
    }

    private String countToString(int count) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(count);
    }

}
