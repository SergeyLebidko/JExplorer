package jexplorer.fileutilities;

import jexplorer.GUI;
import jexplorer.MainClass;

import java.io.File;

public class Copier implements Runnable {

    private File destDir;
    private GUI gui;

    private boolean isStoped;

    //Метод необходим для задания параметров работы процесса копирования.
    //Список копируемых элементов объект получает из буфера обмена самостоятельно.
    public void setStartParameters(File destDir) {
        this.destDir = destDir;
        isStoped = false;
        if (gui == null) gui = MainClass.getGui();
    }

    @Override
    public void run() {
        //Получаем из буфера обмена список копируемых (или перемещаемых) элементов
        Clipboard clipboard = MainClass.getClipboard();
        ClipboardContent clipboardContent = clipboard.get();
        File sourceDir = clipboardContent.root;
        File[] sourceList = clipboardContent.fileList;
        boolean isDeleteSourceList = clipboardContent.isDelete;

        //Устанавливаем заголовок диалога копирования соотвественно задаче, которую будем выполнять
        gui.setCopyDialoTitle(isDeleteSourceList?"Перемещение":"Копирование");

        System.out.println("Процесс копирования закончил работу...");
    }


    synchronized public void stop() {
        isStoped = true;
        System.out.println("Остановка процесса копирования пользователем...");
    }

}
