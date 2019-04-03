package jexplorer.fileutilities;

public class Clipboard {

    private ClipboardContent clipboardContent;

    public Clipboard() {
        clipboardContent=null;
    }

    public void put(ClipboardContent clipboardContent){
        this.clipboardContent=clipboardContent;
    }

    public ClipboardContent get(){
        ClipboardContent result=clipboardContent;
        clipboardContent=null;
        return result;
    }

    public boolean isEmpty(){
        return clipboardContent==null;
    }

}
