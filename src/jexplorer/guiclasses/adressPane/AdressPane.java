package jexplorer.guiclasses.adressPane;

import jexplorer.GUI;
import jexplorer.MainClass;
import jexplorer.fileexplorerclasses.FileSystemExplorer;
import jexplorer.guiclasses.ExplorerPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

public class AdressPane implements ExplorerPane {

    private JScrollPane scrollPane;
    private JPanel contentPane;
    private FileSystemExplorer fileSystemExplorer;

    private final Color backColor = Color.WHITE;

    public AdressPane() {
        fileSystemExplorer = MainClass.getFileSystemExplorer();
        UIManager.put("ScrollBar.width", 5);

        contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        contentPane.setBackground(backColor);
        scrollPane = new JScrollPane(contentPane);

        refreshContent();
    }

    public Component getVisualComponent() {
        return scrollPane;
    }

    public void refreshContent() {
        //Сперва очищаем панель контента от прежних элементов
        clearContentPane();

        File currentPath = fileSystemExplorer.getCurrentDirectory();
        LinkedList<JButton> btnList=new LinkedList<>();
        JButton btn;
        String btnName;
        do {
            btnName = currentPath.getName();
            if (btnName.equals("")){
                btnName=currentPath.toString();
            }
            btn=new JButton(btnName);
            btn.setActionCommand(currentPath.toString());
            btn.addActionListener(al);
            btn.setBackground(backColor);
            btn.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
            btnList.add(btn);
            currentPath = currentPath.getParentFile();
        } while (currentPath != null);

        while ((btn=btnList.pollLast())!=null){
            contentPane.add(btn);
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

    private ActionListener al=new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File directory=new File(e.getActionCommand());
            if (!fileSystemExplorer.openDirectory(directory))return;

            GUI gui=MainClass.getGui();
            gui.getCurrentExplorerPane().refreshContent();
            refreshContent();
        }
    };

}
