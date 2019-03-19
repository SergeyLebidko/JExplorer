package jexplorer;

import java.io.File;

public class FileSystemExplorer {

    private File currentDirectory;

    public FileSystemExplorer() {
        String pathToUserHome;
        pathToUserHome=System.getProperty("user.home");
        currentDirectory =new File(pathToUserHome);
    }

    //Метод возвращает список элементов (файлов и папок) текущего каталога
    public FileSystemElement[] getCurrentElementsList(){
        FileSystemElement[] result;

        //Код-заглушка. Должен быть удален
        result=new FileSystemElement[100];
        for (int i=0;i<100;i++){
            result[i]=new FileSystemElement();
            result[i].name="Элемент №"+i;
        }

        return result;
    }

}
