package jexplorer.fileexplorerclasses;

public enum FileTypes {
    EXECUTABLE("exe", 0),
    DOCUMENT("doc", 1),
    SPREADSHEET("table", 2),
    PRESENTATION("presentation", 3),
    ARCHIVE("arch", 4),
    PDF("pdf", 5),
    WEB("web", 6),
    IMAGE("image", 7),
    VIDEO("video", 8),
    MUSIC("music", 9),
    OTHER("other", 10);

    private String[][] extensions = {
            {"exe"},
            {"txt", "doc", "docx", "rtf", "odt"},
            {"xls", "xlsx", "ods"},
            {"ppt", "pptx", "odp"},
            {"rar", "zip", "7z"},
            {"pdf"},
            {"htm", "html"},
            {"jpg", "jpeg", "bmp", "png", "gif", "ico"},
            {"avi", "mkv", "mp4", "wmv", "mpeg", "mpg", "h264", "3gp"},
            {"mp3", "aac", "aac", "flac", "wav", "wave", "wma"},
            {}
    };

    private String name;
    private int n;

    FileTypes(String name, int n) {
        this.name = name;
        this.n = n;
    }

    public String getName() {
        return name;
    }

    public String[] getExtensionsSet() {
        return extensions[n];
    }
}
