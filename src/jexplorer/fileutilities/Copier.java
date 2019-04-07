package jexplorer.fileutilities;

import jexplorer.MainClass;

import java.io.File;

public class Copier {

    public void copy(File destDir){
        Clipboard clipboard = MainClass.getClipboard();
        ClipboardContent clipboardContent = clipboard.get();

        File sourceDir = clipboardContent.root;
        File[] sourceList = clipboardContent.fileList;
        boolean isDeleteSourceList = clipboardContent.isDelete;

    }

}
