package jexplorer.fileutilities;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

//Класс необходим для возврата результатов файловых операций и списков файлов и каталогов,
//над которыми эти операции выполнить не удалось
public class ResultSet {

    private List<String> result;
    private List<File> errFile;
    private List<String> errText;

    public ResultSet() {
        result = new LinkedList<>();
        errFile = new LinkedList<>();
        errText = new LinkedList<>();
    }

    public void addResult(String name, String value){
        result.add(name+"*"+value);
    }

    public void addErrFile(File file){
        errFile.add(file);
    }

    public void addErrText(String name, String value){
        errText.add(name+"*"+value);
    }

    public List<String> getResult() {
        return result;
    }

    public List<File> getErrFile() {
        return errFile;
    }

    public List<String> getErrText(){return  errText;}

    public boolean isResultListEmpty(){
        return result.isEmpty();
    }

    public boolean isErrFileListEmpty(){
        return  errFile.isEmpty();
    }

    public boolean isErrTextListEmpty(){return errText.isEmpty();}

}
