package jexplorer.guiclasses.tilepane;

import javax.swing.*;
import java.io.File;

public class Selector {

    private class Element {

        JLabel lab;
        File file;
        boolean isSelected;

        public Element(JLabel lab, File file, boolean isSelected) {
            this.lab = lab;
            this.file = file;
            this.isSelected = isSelected;
        }

    }

    private Element[] elements;

    private int header;

    public Selector() {
        header = -1;
    }

    public void setContent(File[] files, JLabel[] labs) {
        elements = new Element[files.length];
        for (int i = 0; i < files.length; i++) {
            elements[i] = new Element(labs[i], files[i], false);
        }
        header = -1;
    }

    public void simpleSelect(JLabel lab) {

    }

    public void ctrlSelect(JLabel lab) {

    }

    public void shiftSelect(JLabel lab) {

    }

    public File[] getSelectedFiles() {
        File[] result;
        int countSelectedElements = getCountSelectedElements();
        result = new File[countSelectedElements];
        for (int i = 0; i < countSelectedElements; i++) {
            if (elements[i].isSelected) result[i] = elements[i].file;
        }
        return result;
    }

    private int getLabelPosition(JLabel lab) {
        int pos = 0;
        for (Element element : elements) {
            if (lab==element.lab)break;
            pos++;
        }
        return pos;
    }

    private int getCountSelectedElements() {
        int count = 0;
        for (Element element : elements) {
            if (element.isSelected) count++;
        }
        return count;
    }

}
