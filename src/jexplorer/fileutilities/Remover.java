package jexplorer.fileutilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class Remover {

    private class DirectoryWalker extends SimpleFileVisitor<Path> {

        private int dirDeleteCount;
        private int fileDeleteCount;
        private List<File> errorList;

        public void remove(File dir, List<File> errorList) throws IOException {
            dirDeleteCount = 0;
            fileDeleteCount = 0;
            this.errorList = errorList;
            Files.walkFileTree(dir.toPath(), this);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            File dirObj = dir.toFile();
            boolean successDelete = dirObj.delete();
            if (successDelete){
                dirDeleteCount++;
            }
            if (!successDelete){
                errorList.add(dirObj);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            File fileObj = file.toFile();
            boolean successDelete = fileObj.delete();
            if (successDelete){
                fileDeleteCount++;
            }
            if (!successDelete){
                errorList.add(fileObj);
            }
            return FileVisitResult.CONTINUE;
        }

        public int getDirDeleteCount() {
            return dirDeleteCount;
        }

        public int getFileDeleteCount() {
            return fileDeleteCount;
        }

    }

    public ResultSet remove(File[] fileList){
        ResultSet resultSet = new ResultSet();

        int dirDeleteCount = 0;
        int fileDeleteCount = 0;
        DirectoryWalker directoryWalker = new DirectoryWalker();
        for (File file: fileList){

            //Если удаляемый объект не существует
            if (!file.exists()){
                resultSet.addError(file);
                continue;
            }

            //Если удаляемый объект - файл
            if (file.isFile()){
                boolean success = file.delete();
                if (success){
                    fileDeleteCount++;
                    continue;
                }
                if (!success){
                    resultSet.addError(file);
                    continue;
                }
            }

            //Если удаляемый объект - каталог
            if (file.isDirectory()){
                try {
                    List<File> errorList = new LinkedList<>();
                    directoryWalker.remove(file, errorList);
                    resultSet.getError().addAll(errorList);
                } catch (IOException e) {
                    resultSet.addError(file);
                    continue;
                }
                dirDeleteCount+=directoryWalker.getDirDeleteCount();
                fileDeleteCount+=directoryWalker.getFileDeleteCount();
            }

        }

        if (dirDeleteCount!=0 || fileDeleteCount!=0){
            resultSet.addResult("Удалено файлов", fileDeleteCount+"");
            resultSet.addResult("Удалено папок", dirDeleteCount+"");
        }

        return resultSet;
    }

}
