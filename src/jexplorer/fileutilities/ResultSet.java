package jexplorer.fileutilities;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

//Класс необходим для возврата результатов файловых операций и списков файлов и каталогов,
//над которыми эти операции выполнить не удалось
public class ResultSet {

    private List<String> result;
    private List<File> error;

    public ResultSet() {
        result = new LinkedList<>();
        error = new LinkedList<>();
    }

    public void addResult(String name, String value){
        result.add(name+"*"+value);
    }

    public void addError(File file){
        error.add(file);
    }

    public List<String> getResult() {
        return result;
    }

    public List<File> getError() {
        return error;
    }

    public boolean isResultListEmpty(){
        return result.isEmpty();
    }

    public boolean isErrorListEmpty(){
        return  error.isEmpty();
    }

}
