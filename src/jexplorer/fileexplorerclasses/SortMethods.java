package jexplorer.fileexplorerclasses;

public enum SortMethods {

    BY_NAME("BY_NAME"),
    BY_SIZE("BY_SIZE"),
    BY_TYPE("BY_TYPE"),
    BY_EXTENSION("BY_EXTENSION"),
    BY_DATE_CREATED ("BY_DATE_CREATED"),
    BY_DATE_MODIFIED ("BY_DATE_MODIFIED");

    private String name;

    SortMethods(String name) {
        this.name=name;
    }

    public String getName(){
        return name;
    }

}
