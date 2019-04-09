package jexplorer.fileutilities;

import jexplorer.GUI;
import jexplorer.MainClass;

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

    public static final int REPLACE = 1;
    public static final int SKIP = 2;
    public static final int REPLACE_ALL = 3;
    public static final int SKIP_ALL = 4;

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
                resultSet.addErrText("Не удалось получить доступ к", root.getPath());
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
    }

    private DirectoryWalker directoryWalker;
    private FileProgressUpdater fileProgressUpdater;
    private TotalProgressUpdater totalProgressUpdater;
    private ResultSet resultSet;
    private List<Element> elements;

    public Copier() {
        elements = new LinkedList<>();
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
                resultSet.addErrText("Не удалось получить доступ к", file.getPath());
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
        String sDir = sourceDir.getAbsolutePath();
        String sFile;
        for (Element element : elements) {
            sFile = element.src.getAbsolutePath().substring(sDir.length());
            element.dest = new File(destDir, sFile);
        }

        //Поверяем список приемников на совпадение с реально имеющимися файлами

        //Инициализируем объект, который будет обновлять индикатор общего прогресса
        totalProgressUpdater.init();

        //Начинаем процесс копирования
        int readBytes;
        for (Element element : elements) {

            //Отображаем имя копируемого элемента
            gui.setCurrentCopyFileName("Копирую: " + element.src.getName());

            if (element.src.isFile()) {
                fileProgressUpdater.init(element.src);
                try (FileChannel channelSrc = new FileInputStream(element.src).getChannel();
                     FileChannel channelDest = new FileOutputStream(element.dest).getChannel()) {

                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    while ((readBytes = channelSrc.read(buffer))!=(-1)) {

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
                    resultSet.addErrText("Не удалось скопировать", element.src.getAbsolutePath() + " ошибка: " + ex);
                }
            }
            if (element.src.isDirectory()) {
                element.dest.mkdirs();
                //Обновляем индикатор общего прогресса
                totalProgressUpdater.update(1);
            }
        }

        //Если было активировано перемещение - удаляем источники
        if (isDeleteSourceList) {
            Remover remover = MainClass.getRemover();
            ResultSet removerResult = remover.remove(sourceList);
            for (File file : removerResult.getErrFile()) {
                resultSet.addErrText("Не удалось удалить источник", file.getAbsolutePath());
            }
        }

        //Закрываем диалог копирования
        gui.closeCopyDialog();
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
