package jexplorer.guiclasses.rootpane;

import jexplorer.GUI;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.LinkedList;

public class RootPointExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private JButton[] content;
    private StackLayout currentLayout;
    private FileSystemExplorer fileSystemExplorer;

    private final Color backColor = Color.WHITE;

    class StackLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Dimension dim;
            int preferredWidth, preferredHeight;
            preferredWidth = scrollPane.getViewport().getWidth();
            preferredHeight = 0;
            for (Component c : parent.getComponents()) {
                preferredHeight += c.getPreferredSize().getHeight();
            }
            dim = new Dimension(preferredWidth, preferredHeight);
            return dim;
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(0, 0);
        }

        @Override
        public void layoutContainer(Container parent) {
            int y;
            y = 0;
            for (Component c : parent.getComponents()) {
                c.setBounds(0, y, scrollPane.getViewport().getWidth(), (int) c.getPreferredSize().getHeight());
                y += c.getPreferredSize().getHeight();
            }
        }
    }

    public RootPointExplorerPane() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        UIManager.put("ScrollBar.width", 20);

        contentPane = new JPanel();
        currentLayout = new StackLayout();
        contentPane.setLayout(currentLayout);
        contentPane.setBackground(backColor);

        scrollPane = new JScrollPane(contentPane);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPane.revalidate();
            }
        });

        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва очищаем панель контента от прежних элементов
        clearContentPane();

        //Добавляем на панель контента новые элементы
        LinkedList<File> disks = fileSystemExplorer.getDisks();
        content = new JButton[disks.size() + 1];

        content[0] = new JButton();
        content[0].setText("Домашняя папка");
        content[0].setToolTipText("Домашний каталог пользователя");
        content[0].setActionCommand(fileSystemExplorer.getUserHomeDir().toString());
        content[0].addActionListener(al);
        content[0].setIcon(new ImageIcon("res\\rootPointView\\home_folder.png"));
        content[0].setBorder(BorderFactory.createEmptyBorder(8, 1, 8, 1));
        content[0].setBackground(backColor);
        contentPane.add(content[0]);

        String txt;
        int i = 1;
        for (File element : fileSystemExplorer.getDisks()) {
            txt = "  " + element.getAbsolutePath();
            content[i] = new JButton();
            content[i].setText(txt);
            txt = element.getAbsolutePath();
            txt = "Диск " + txt.substring(0, txt.indexOf(':'));
            content[i].setToolTipText(txt);
            content[i].setActionCommand(element.toString());
            content[i].addActionListener(al);
            content[i].setIcon(new ImageIcon("res\\rootPointView\\disk.png"));
            content[i].setBorder(BorderFactory.createEmptyBorder(8, 1, 8, 1));
            content[i].setBackground(backColor);
            contentPane.add(content[i]);
            i++;
        }

        contentPane.revalidate();
        contentPane.repaint();
    }

    private void clearContentPane() {
        if (contentPane.getComponentCount() == 0) return;
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            contentPane.remove(component);
        }
    }

    private ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File directory = new File(e.getActionCommand());
            GUI gui = MainClass.getGui();
            try {
                fileSystemExplorer.openDirectory(directory);
            } catch (Exception ex) {
                gui.showErrorDialog(ex.getMessage());
                refreshContent();
                try {
                    fileSystemExplorer.openDirectory(fileSystemExplorer.getUserHomeDir());
                } catch (Exception e1) {}
            }
            gui.getCurrentExplorerPane().refreshContent();
            gui.getAdressPane().refreshContent();
        }
    };

}
