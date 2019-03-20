package jexplorer.fileexplorerclasses;

public enum FileTypes {
    EXECUTABLE("exe"),
    DOCUMENT("doc"),
    SPREADSHEET("table"),
    PRESENTATION("presentation"),
    ARCHIVE("arch"),
    PDF("pdf"),
    WEB("web"),
    IMAGE("image"),
    VIDEO("video"),
    MUSIC("music"),
    OTHER("other");

    private String name;

    FileTypes(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
