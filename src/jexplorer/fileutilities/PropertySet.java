package jexplorer.fileutilities;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class PropertySet {

    private List<String> propertiesList;    //Список свойств объектов
    private List<File> passList;            //Список объектов, свойства которых получить не удалось

    public PropertySet() {
        propertiesList = new LinkedList<>();
        passList = new LinkedList<>();
    }

    public void addProrerty(String name, String value){
        propertiesList.add(name+"*"+value);
    }

    public void addPass(File file){
        passList.add(file);
    }

    public List<String> getPropertiesList() {
        return propertiesList;
    }

    public List<File> getPassList() {
        return passList;
    }

    public boolean isPropertiesListEmpty(){
        return propertiesList.isEmpty();
    }

    public boolean isPassListEmpty(){
        return  passList.isEmpty();
    }

}
