package jexplorer.guiclasses.tilepane;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class Selector {

    private class Element {

        JLabel lab;
        boolean isSelected;

        public Element(JLabel lab, boolean isSelected) {
            this.lab = lab;
            this.isSelected = isSelected;
        }

    }

    private Element[] elements;

    Map<JLabel, File> fileMap;

    private int header;

    private final Color backColorForNoSelect = Color.WHITE;
    private final Color backColorForSelect = new Color(168, 201, 255);

    public Selector() {
        header = -1;
        elements = new Element[0];
    }

    public void setContent(JLabel[] labs, Map<JLabel, File> fileMap) {
        elements = new Element[labs.length];
        this.fileMap = fileMap;
        for (int i = 0; i < labs.length; i++) {
            elements[i] = new Element(labs[i], false);
        }
        header = -1;
    }

    public void simpleSelect(JLabel lab) {
        header = getLabelPosition(lab);
        if (!elements[header].isSelected) {
            lab.setBackground(backColorForSelect);
            elements[header].isSelected = true;
        }
        for (int i = 0; i < elements.length; i++) {
            if (i == header || !elements[i].isSelected) continue;
            elements[i].lab.setBackground(backColorForNoSelect);
            elements[i].isSelected = false;
        }
    }

    public void ctrlSelect(JLabel lab) {
        header = getLabelPosition(lab);
        if (!elements[header].isSelected) {
            lab.setBackground(backColorForSelect);
            elements[header].isSelected = true;
            return;
        }
        if (elements[header].isSelected) {
            lab.setBackground(backColorForNoSelect);
            elements[header].isSelected = false;
        }
    }

    public void shiftSelect(JLabel lab) {
        int pos = getLabelPosition(lab);
        if (header == (-1)) header = 0;
        int start, stop;
        start = Math.min(header, pos);
        stop = Math.max(header, pos);
        for (int i = 0; i < elements.length; i++) {
            if (i >= start & i <= stop) {
                elements[i].lab.setBackground(backColorForSelect);
                elements[i].isSelected = true;
                continue;
            }
            elements[i].lab.setBackground(backColorForNoSelect);
            elements[i].isSelected = false;
        }
    }

    public void selectAll(){
        header=0;
        for (Element element: elements){
            element.lab.setBackground(backColorForSelect);
            element.isSelected=true;
        }
    }

    public File[] getSelectedFiles() {
        File[] result;
        int countSelectedElements = getCountSelectedElements();
        result = new File[countSelectedElements];
        for (int i = 0; i < countSelectedElements; i++) {
            if (elements[i].isSelected) result[i] = fileMap.get(elements[i].lab);
        }
        return result;
    }

    private int getLabelPosition(JLabel lab) {
        int pos = -1;
        for (int i = 0; i < elements.length; i++) {
            if (lab == elements[i].lab) {
                pos = i;
                break;
            }
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
