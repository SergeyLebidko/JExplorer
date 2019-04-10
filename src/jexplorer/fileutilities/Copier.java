package jexplorer.fileutilities;

import jexplorer.GUI;
import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

public class Copier implements Runnable {

    public static final int UNDEFINED_BEHAVIOR = 0;
    public static final int REPLACE = 1;
    public static final int SKIP = 2;
    public static final int SAVE_BOTH = 3;
    public static final int REPLACE_ALL = 4;
    public static final int SKIP_ALL = 5;
    public static final int SAVE_BOTH_ALL = 6;

    private File destDir;
    private GUI gui;

    private boolean isStoped;

    private class DirectoryWalker extends SimpleFileVisitor<Path> {

        private List<Element> tmpList;

        public void startWalk(File root) {
            tmpList = new LinkedList<>();
            try {
                Files.walkFileTree(root.toPath(), this);
            } catch (IOException e) {
                resultSet.addErrText("Не удалось получить доступ к:", root.getPath());
                return;
            }
            elements.addAll(tmpList);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Element element = new Element();
            element.src = dir.toFile();
            tmpList.add(element);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Element element = new Element();
            element.src = file.toFile();
            tmpList.add(element);
            return FileVisitResult.CONTINUE;
        }
    }

    private class TotalProgressUpdater {

        private long totalSize;
        private long currentSize;
        private int percent;

        public void init() {
            totalSize = 0;
            for (Element element : elements) {
                if (element.src.isFile()) {
                    totalSize += element.src.length();
                    continue;
                }
                totalSize++;
            }
            currentSize = 0;
            percent = 0;
            gui.setTotalProgress(0);
        }

        public void update(int countAdded) {
            int tmpPercent;
            currentSize += countAdded;
            tmpPercent = (int) (Math.round((double) currentSize / (double) totalSize * 100));
            if ((tmpPercent - percent) >= 1) {
                percent = tmpPercent;
                gui.setTotalProgress(percent);
            }
        }

    }

    private class FileProgressUpdater {

        private long totalSize;
        private long currentSize;
        private int percent;

        private void init(File file) {
            totalSize = file.length();
            currentSize = 0;
            percent = 0;
            gui.setFileProgress(0);
        }

        public void update(int countAdded) {
            int tmpPercent;
            currentSize += countAdded;
            tmpPercent = (int) (Math.round((double) currentSize / (double) totalSize * 100));
            if ((tmpPercent - percent) >= 1) {
                percent = tmpPercent;
                gui.setFileProgress(percent);
            }
        }

    }

    private class Element {
        File src;
        File dest;
        boolean isSuccessfulCopy;

        public Element() {
            isSuccessfulCopy = false;
        }

    }

    private FileSystemExplorer fileSystemExplorer;
    private DirectoryWalker directoryWalker;
    private FileProgressUpdater fileProgressUpdater;
    private TotalProgressUpdater totalProgressUpdater;
    private ResultSet resultSet;
    private List<Element> elements;

    public Copier() {
        elements = new LinkedList<>();
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        directoryWalker = new DirectoryWalker();
        fileProgressUpdater = new FileProgressUpdater();
        totalProgressUpdater = new TotalProgressUpdater();
    }

    //Метод инициализирует параметры для операции копирования. Исходные файлы для копирования класс получает из буфера обмена.
    public void initialCopier(File destDir) {
        this.destDir = destDir;
        isStoped = false;
        resultSet = new ResultSet();
        elements.clear();
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
        gui.setCopyDialogTitle(isDeleteSourceList ? "Перемещение" : "Копирование");

        //Формируем список источников
        for (File file : sourceList) {
            if (!file.exists()) {
                resultSet.addErrText("Не удалось получить доступ к:", file.getPath());
                continue;
            }
            if (file.isFile()) {
                Element element = new Element();
                element.src = file;
                elements.add(element);
                continue;
            }
            if (file.isDirectory()) {
                directoryWalker.startWalk(file);
            }
        }

        //Проверяем, не оказался ли список источников пустым
        if (elements.isEmpty()) {
            gui.closeCopyDialog();
            return;
        }

        //Формируем список приемников
        //Обычный случай: каталог-источник и каталог-приемник не совпадают
        if (!destDir.equals(sourceDir)) {
            String sDir = sourceDir.getAbsolutePath();
            String sFile;
            for (Element element : elements) {
                sFile = element.src.getAbsolutePath().substring(sDir.length());
                element.dest = new File(destDir, sFile);
            }
        }

        //Особый случай: каталог-источник и каталог-приемник совпадают
        if (destDir.equals(sourceDir)) {
            String str1, str2, str3;
            File fTmp;
            for (Element element : elements) {
                str2 = element.src.getAbsolutePath();
                for (File file : sourceList) {
                    str1 = file.getAbsolutePath();
                    if (str2.startsWith(str1)) {
                        fTmp = getNextName(file);
                        str3 = str2.substring(str1.length());
                        element.dest = new File(fTmp, str3);
                        break;
                    }
                }
            }
        }

        //Инициализируем объект, который будет обновлять индикатор общего прогресса
        totalProgressUpdater.init();

        //Начинаем процесс копирования
        int readBytes;
        int actionOnConflict = 0;
        for (Element element : elements) {

            //Проверяем конфликты
            if (element.dest.exists() & element.dest.isFile()) {
                if (actionOnConflict == UNDEFINED_BEHAVIOR) {
                    actionOnConflict = gui.showCopyConflictDialog("Файл " + element.dest + " уже существует в папке назначения");
                }
                switch (actionOnConflict) {
                    case SKIP: {
                        actionOnConflict = UNDEFINED_BEHAVIOR;
                        continue;
                    }
                    case SKIP_ALL: {
                        continue;
                    }
                    case REPLACE: {
                        actionOnConflict = UNDEFINED_BEHAVIOR;
                        break;
                    }
                    case REPLACE_ALL: {
                        break;
                    }
                    case SAVE_BOTH: {
                        element.dest = getNextName(element.dest);
                        actionOnConflict = UNDEFINED_BEHAVIOR;
                        break;
                    }
                    case SAVE_BOTH_ALL: {
                        element.dest = getNextName(element.dest);
                    }
                }
            }

            //Отображаем имя копируемого элемента
            gui.setCurrentCopyFileName("Копирую: " + element.src.getName());

            if (element.src.isFile()) {
                fileProgressUpdater.init(element.src);
                try (FileChannel channelSrc = new FileInputStream(element.src).getChannel();
                     FileChannel channelDest = new FileOutputStream(element.dest).getChannel()) {

                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    while ((readBytes = channelSrc.read(buffer)) != (-1)) {

                        //Записываем данные в файл-приемник
                        buffer.flip();
                        channelDest.write(buffer);
                        buffer.clear();

                        //Обновляем индикаторы прогресса
                        fileProgressUpdater.update(readBytes);
                        totalProgressUpdater.update(readBytes);

                        //Проверяем, не остановил ли пользователь процесс копирования
                        synchronized (this) {
                            if (isStoped) {
                                gui.closeCopyDialog();
                                return;
                            }
                        }

                    }

                } catch (Exception ex) {
                    resultSet.addErrText("Не удалось скопировать:", element.src.getAbsolutePath());
                }
                element.isSuccessfulCopy = true;
            }
            if (element.src.isDirectory()) {
                if (element.dest.mkdirs()) {
                    element.isSuccessfulCopy = true;
                }
                //Обновляем индикатор общего прогресса
                totalProgressUpdater.update(1);
            }
        }

        //Если было активировано перемещение - удаляем источники
        if (isDeleteSourceList) {
            boolean isRemove=true;
            while (isRemove){
                isRemove=false;

                //Вначале удаляем все файлы, коотрые были успешно скопированы
                for (Element element: elements){
                    if (element.src.isFile() & element.isSuccessfulCopy){
                        element.isSuccessfulCopy=false;
                        if (element.src.delete()){
                            isRemove=true;
                            continue;
                        }
                        resultSet.addErrText("Файл был скопирован, но удалить его в исходном местополежении не удалось:", element.src.getAbsolutePath());
                    }
                }

                //Затем удаляем все папки, коорые были успешно скопированы
                for (Element element: elements){
                    if (element.src.isDirectory() & element.isSuccessfulCopy){
                        element.isSuccessfulCopy = false;
                        if (element.src.delete()){
                            isRemove=true;
                            continue;
                        }
                        resultSet.addErrText("Папка была скопирована, но удалить её в исходном местополежении не удалось:", element.src.getAbsolutePath());
                    }
                }
            }
        }

        //Закрываем диалог копирования
        gui.closeCopyDialog();

        //test();
    }

    private File getNextName(File file) {
        String name;
        String ext;
        File result = new File(file.getAbsolutePath());
        do {
            name = fileSystemExplorer.getFileName(result);
            if (name == null) {
                name = result.getName();
            }
            ext = fileSystemExplorer.getFileExtension(result);
            if (ext == null) {
                ext = "";
            }
            if (!ext.equals("")) {
                ext = "." + ext;
            }
            result = new File(result.getParentFile(), name + " - копия" + ext);
        } while (result.exists());
        return result;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    synchronized public void stop() {
        isStoped = true;
    }

    private void test() {
        System.out.println("Источники - Приемники:");
        for (Element element : elements) {
            System.out.print("    Источник: ");
            System.out.println(element.src);
            System.out.print("    Приемник: ");
            System.out.println(element.dest);
            System.out.println();
        }
        System.out.println("Ошибки");
        for (String str : resultSet.getErrText()) {
            System.out.println("    " + str);
        }
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println();
    }

}
