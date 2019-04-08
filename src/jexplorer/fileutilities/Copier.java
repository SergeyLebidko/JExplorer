package jexplorer.fileutilities;

import jexplorer.GUI;
import jexplorer.MainClass;

import java.io.File;

public class Copier implements Runnable {

    public static final int REPLACE = 1;
    public static final int SKIP = 2;
    public static final int REPLACE_ALL = 3;
    public static final int SKIP_ALL = 4;

    private File destDir;
    private GUI gui;

    private boolean isStoped;

    //Метод необходим для задания папки назначения, в которую будут скопированы (перемещены) объекты из буфера обмена
    public void setDestinationDir(File destDir) {
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
        gui.setCopyDialogTitle(isDeleteSourceList?"Перемещение":"Копирование");

        int answer = gui.showConflictDialog("Файл ХХХ уже есть в папке назначения. Заменить его?");
        System.out.println("Ответ: "+answer);
        System.out.println("Процесс копирования закончил работу...");

    }

    synchronized public void stop() {
        isStoped = true;
        System.out.println("Остановка процесса копирования пользователем...");
    }

}
