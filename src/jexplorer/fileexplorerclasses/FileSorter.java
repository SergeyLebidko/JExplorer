package jexplorer.fileexplorerclasses;

import java.io.File;

public class FileSorter {

    //Методы сортировки
    public final String BY_NAME = "BY_NAME";
    public final String BY_SIZE = "BY_SIZE";
    public final String BY_TYPE = "BY_TYPE";
    public final String BY_EXTENSION = "BY_EXTENSION";
    public final String BY_DATE_CREATED = "BY_DATE_CREATED";
    public final String BY_DATE_MODIFIED = "BY_DATE_MODIFIED";

    //Направления сортировки
    public final String TO_UP = "TO_UP";
    public final String TO_DOWN = "TO_DOWN";

    private String currentSortType;    //Текущий тип сортировки
    private int currentSortOrder;      //Текущий порядок сортировки (по возрастанию/убыванию)

    public FileSorter() {
        currentSortType = BY_NAME;
        currentSortOrder = 1;
    }

    public void setSortType(String type) {
        currentSortType = type;
    }

    public void setSortOrder(String order) {
        if (order.equals(TO_UP)) currentSortOrder = 1;
        if (order.equals(TO_DOWN)) currentSortOrder = -1;
    }

    public String getCurrentSortType() {
        return currentSortType;
    }

    public int getCurrentSortOrder() {
        return currentSortOrder;
    }

    public File[] sort(File[] files){
        return files;
    }

}
