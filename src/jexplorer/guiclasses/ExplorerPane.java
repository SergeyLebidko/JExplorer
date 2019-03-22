package jexplorer.guiclasses;

import java.awt.*;

public interface ExplorerPane {

    Component getVisualComponent();
    void refreshContent();
    void setShowHiddenElements(boolean show);

}
