package jexplorer.fileexplorerclasses;

import jexplorer.MainClass;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FileSorter {

    private SortMethods currentSortType;    //Текущий тип сортировки
    private SortOrders currentSortOrder;      //Текущий порядок сортировки (по возрастанию/убыванию)

    //Компаратор для сортировки файлов по имени
    private class ByName implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            String name1 = o1.getName().toLowerCase();
            String name2 = o2.getName().toLowerCase();
            return currentSortOrder.getOrder() * name1.compareTo(name2);
        }

    }

    //Компаратор для сортировки объектов по размеру (применим только к файлам)
    private class BySize implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            long size1 = o1.length();
            long size2 = o2.length();
            int result = 0;
            if (size1 < size2) result = -1;
            if (size1 > size2) result = 1;
            return currentSortOrder.getOrder() * result;
        }

    }

    //Компаратор для сортировки объектов по типу (применим только к файлам)
    private class ByType implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();
            FileTypes type1 = fileSystemExplorer.getFileType(o1);
            FileTypes type2 = fileSystemExplorer.getFileType(o2);
            FileTypes[] types = FileTypes.values();
            int num1, num2;
            num1 = num2 = 0;
            for (int i = 0; i < types.length; i++) {
                if (types[i] == type1) num1 = i;
                if (types[i] == type2) num2 = i;
            }
            int result = 0;
            if (num1 < num2) result = -1;
            if (num1 > num2) result = 1;
            return currentSortOrder.getOrder() * result;
        }
    }

    //Компаратор для сортировки объектов по расширению (применим только к файлам)
    private class ByExtension implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();
            String ext1 = fileSystemExplorer.getFileExtension(o1);
            String ext2 = fileSystemExplorer.getFileExtension(o2);
            return currentSortOrder.getOrder() * ext1.compareTo(ext2);
        }

    }

    //Компаратор для сортировки объектов по дате создания
    private class ByDateCreated implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();
            Date date1, date2;
            try {
                date1 = fileSystemExplorer.getDateCreated(o1);
            } catch (Exception e) {
                date1 = new Date(0);
            }
            try {
                date2 = fileSystemExplorer.getDateCreated(o2);
            } catch (Exception e) {
                date2 = new Date(0);
            }
            return currentSortOrder.getOrder() * date1.compareTo(date2);
        }

    }

    //Компаратор для сортировки объектов по дате последней модификации
    private class ByDateModified implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            FileSystemExplorer fileSystemExplorer = MainClass.getFileSystemExplorer();
            Date date1, date2;
            try {
                date1 = fileSystemExplorer.getDateModified(o1);
            } catch (Exception e) {
                date1 = new Date(0);
            }
            try {
                date2 = fileSystemExplorer.getDateModified(o2);
            } catch (Exception e) {
                date2 = new Date(0);
            }
            return currentSortOrder.getOrder() * date1.compareTo(date2);
        }

    }

    private ByName nameComparator = new ByName();
    private BySize sizeComparator = new BySize();
    private ByType typeComparator = new ByType();
    private ByExtension extensionComparator = new ByExtension();
    private ByDateCreated dateCreatedComparator = new ByDateCreated();
    private ByDateModified byDateModifiedComparator = new ByDateModified();

    public FileSorter() {
        currentSortType = SortMethods.BY_NAME;
        currentSortOrder = SortOrders.TO_UP;
    }

    public void setSortType(SortMethods type) {
        currentSortType = type;
    }

    public void setSortOrder(SortOrders order) {

    }

    public SortMethods getCurrentSortType() {
        return currentSortType;
    }

    public SortOrders getCurrentSortOrder() {
        return currentSortOrder;
    }

    public File[] sort(File[] list) {
        File[] files;
        File[] dirs;

        //Делим исходный список на два списка: список файлов и список каталогов
        files = getFils(list);
        dirs = getDirs(list);

        return list;
    }

    private File[] getFils(File[] files) {
        List<File> filesList = new LinkedList<>();
        for (File file : files) {
            if (file.isFile()) filesList.add(file);
        }
        return filesList.toArray(new File[filesList.size()]);
    }

    private File[] getDirs(File[] files) {
        List<File> filesList = new LinkedList<>();
        for (File file : files) {
            if (file.isDirectory()) filesList.add(file);
        }
        return filesList.toArray(new File[filesList.size()]);
    }

}
