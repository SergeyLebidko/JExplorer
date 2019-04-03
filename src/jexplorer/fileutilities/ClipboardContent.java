package jexplorer.fileutilities;

import java.io.File;

public class ClipboardContent {

    public File parent;
    public File[] fileList;
    public boolean isDelete;

    public ClipboardContent(File parent, File[] fileList, boolean isDelete) {
        this.parent = parent;
        this.fileList = fileList;
        this.isDelete = isDelete;
    }

}
