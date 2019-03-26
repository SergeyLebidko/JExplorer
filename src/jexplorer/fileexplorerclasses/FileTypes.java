package jexplorer.fileexplorerclasses;

public enum FileTypes {
    EXECUTABLE("exe", "Программа",0),
    DOCUMENT("doc", "Документ",1),
    SPREADSHEET("table", "Таблица",2),
    PRESENTATION("presentation", "Презентация",3),
    ARCHIVE("arch", "Архив", 4),
    PDF("pdf", "Документ PDF", 5),
    WEB("web", "Файл HTML", 6),
    IMAGE("image", "Изображение",7),
    VIDEO("video", "Видео",8),
    MUSIC("music","Аудио", 9),
    DLL("dll", "Компонент приложения", 10),
    INI("ini", "Файл настроек",11),
    OTHER("other", "",12);

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
            {"dll"},
            {"ini"},
            {}
    };

    private String name;
    private String tooltipText;
    private int n;

    FileTypes(String name, String tooltipText, int n) {
        this.name = name;
        this.tooltipText = tooltipText;
        this.n = n;
    }

    public String getName() {
        return name;
    }

    public String getTooltipText(){
        return  tooltipText;
    }

    public String[] getExtensionsSet() {
        return extensions[n];
    }
}
