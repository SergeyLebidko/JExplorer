package jexplorer.guiclasses;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public interface ExplorerPane {

    Component getVisualComponent();
    void refreshContent();
    void setShowHiddenElements(boolean show);
    File[] getSelectedElements();
    void selectAllElements();
    void setPopupMenu(JPopupMenu popupMenu);

}
