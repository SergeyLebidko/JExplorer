package jexplorer;

import java.io.File;

public class FileSystemExplorer {

    private File currentPath;

    public FileSystemExplorer() {
        String pathToUserHome;
        pathToUserHome=System.getProperty("user.home");
        currentPath=new File(pathToUserHome);

        System.out.println(currentPath);
    }

}
