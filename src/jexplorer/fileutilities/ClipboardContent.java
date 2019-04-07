package jexplorer.fileutilities;

import java.io.File;

public class ClipboardContent {

    public File root;
    public File[] fileList;
    public boolean isDelete;

    public ClipboardContent(File root, File[] fileList, boolean isDelete) {
        this.root = root;
        this.fileList = fileList;
        this.isDelete = isDelete;
    }

}
