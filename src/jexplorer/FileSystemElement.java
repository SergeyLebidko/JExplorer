package jexplorer;

import java.io.File;
import java.util.Date;

public class FileSystemElement {

    public File path;                //Путь к элементу в файловой системе
    public String name;              //Имя элемента
    public String toolTipText;       //Подсказка для элемента

    public boolean isDir;            //Равен true, если данный элемент - каталог
    public boolean isHidden;         //Равен true, если данный элемент - скрытый файл или папка

    public long size;                //Размер файла в байтах, для папок равен -1

    public String fileExtension;     //Для файлов - расширение. Пустая строка, если расширение отсутсвует. null - если данный элемент - каталог

    public Date dateCreated;         //Дата создания файла/каталога
    public Date dateModified;        //Дата последней модификации файла/каталога

}
