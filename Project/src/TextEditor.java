package main;

import jdk.internal.org.objectweb.asm.tree.InnerClassNode;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.awt.Component;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class TextEditor extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static JTextArea area;
    private static JFrame frame, frame_edits, frame_delete_group;
    private static JScrollPane j1, j3;
    private static Button b1, b2, b3;
    private static int returnValue = 0, j = 0;
    private static JTable table1, table2,tableTemp;
    private static Vector<String> editsColumnNames = new Vector();
    private static Vector<Vector> editsValues = new Vector();
    private static Vector<Vector> editsValues2 = new Vector();
    private static Vector<String> groupColumnNames = new Vector<>();
    private static Vector<Vector> groupValues = new Vector<>();
    private static List<EditsProps> undoPropsList = new ArrayList<>();
    private static List<EditsProps> undoPropsList2 = new ArrayList<>();
    private static int PK = 0;


    // center column text and change row color
    public static void setCellsAlignment(JTable table, int alignment) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);
        TableModel tableModel = table.getModel();
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }


    public TextEditor() {
        run();
    }

    public void run() {

        frame = new JFrame("Text Editor");
        frame.setLocationRelativeTo(null);
        // Set the look-and-feel (LNF) of the application
        // Try to default to whatever the host system prefers
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        JMenuBar menu_main = new JMenuBar();
        menu_main.setVisible(true);
        frame.setJMenuBar(menu_main);
        // Set attributes of the app window
        area = new JTextArea();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(area);
        frame.setSize(740, 580);
        frame.setVisible(true);


        // Build the menu
        JMenu menu_file = new JMenu("File");
        JMenuItem menuitem_new = new JMenuItem("New");
        JMenuItem menuitem_open = new JMenuItem("Open");
        JMenuItem menuitem_save = new JMenuItem("Save");
        JMenuItem menuitem_quit = new JMenuItem("Quit");
        menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_quit.addActionListener(this);
        menu_main.add(menu_file);
        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_quit);


        //>>>> Build the smart undo menu
        JMenu undo_file = new JMenu("Smart Undo");
        JMenuItem undo_last = new JMenuItem("Undo Prev. (ctrl+z)");
        JMenuItem view_edit = new JMenuItem("View/Undo Edit(s)");
        JMenuItem create_group = new JMenuItem("Start a New Group");
        JMenuItem delete_edit = new JMenuItem("Delete Edit(s)");
        JMenuItem delete_group = new JMenuItem("Delete Group(s)");
        JMenuItem reset_undo = new JMenuItem("Clear All");
        undo_last.addActionListener(this);
        view_edit.addActionListener(this);
        create_group.addActionListener(this);
        delete_edit.addActionListener(this);
        delete_group.addActionListener(this);
        reset_undo.addActionListener(this);
        menu_main.add(undo_file);
        undo_file.add(undo_last);
        undo_file.add(view_edit);
        undo_file.add(create_group);
        undo_file.add(delete_edit);
        undo_file.add(delete_group);
        undo_file.add(reset_undo);

        EditsTBlColumnNames.setName(
                editsColumnNames
                , "group"
                , "line"
                , "columns"
                , "action"
                , "character"
                ,"charIndex"
                , "pk");

        EditsTBlColumnNames.setName(
                groupColumnNames
                , "group"
                , "startLine"
                , "endLine"
                , "numberOfEdits");


//        UserInputTables.setValues(area, editsValues, groupValues, undoPropsList,table2);

        DefaultTableModel defaultTableModel = new DefaultTableModel(editsValues, editsColumnNames);
        DefaultTableModel defaultTableModel2 = new DefaultTableModel(editsValues2, editsColumnNames);
        table1 = new JTable(defaultTableModel);
        tableTemp = new JTable(defaultTableModel2);
        int rowCount = table1.getRowCount();
        UserInputTables.setPkAndTable(rowCount, table1);

        setCellsAlignment(table1, SwingConstants.CENTER);
        table1.setPreferredScrollableViewportSize(new Dimension(450, 300));
        table1.setFillsViewportHeight(true);
        table1.getTableHeader().setReorderingAllowed(false); // not allow re-ordering of columns
        table1.getTableHeader().setResizingAllowed(false);

        // add edits list
        j1 = new JScrollPane(table1);

        //>>> TABLES
        table2 = new JTable(groupValues, groupColumnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        setCellsAlignment(table2, SwingConstants.CENTER);
        table2.setPreferredScrollableViewportSize(new Dimension(250, 300));
        table2.setFillsViewportHeight(true);
        table2.getTableHeader().setReorderingAllowed(false); // not allow re-ordering of columns
        table2.getTableHeader().setResizingAllowed(false);
        UserInputTables.setValues(area, editsValues, groupValues, undoPropsList, table2, table1,undoPropsList2,editsValues2);


        //>>>> Build the VIEW EDITS frame
        frame_edits = new JFrame("View Edits");
        // add edits list

        j1 = new JScrollPane(table1);
        j1.setVisible(true);
        frame_edits.add(j1);

        // add Undo Edits button
        b1 = new Button("UNDO Selected Edit(s)");
        b1.addActionListener(this);
        b1.setSize(150, 25);

        // add Delete Edits button
//        b2 = new Button("DELETE Selected Edit(s)");
        b2 = new Button("DELETE Selected Edit(s)");
        b2.addActionListener(this);
        b2.setSize(150, 25);

        frame_edits.setSize(450, 300);
        frame_edits.setVisible(false);
        frame_edits.setLocationRelativeTo(null);


        //>>>> Build the VIEW EDITS frame
        frame_delete_group = new JFrame("Delete Group");
        // add edits list
        j3 = new JScrollPane(table2);
        j3.setVisible(true);
        frame_delete_group.add(j3);

        // add Undo Edits button
        b3 = new Button("DELETE Selected GROUP(s)");
        b3.addActionListener(this);
        b3.setSize(150, 25);
        frame_delete_group.add(b3, BorderLayout.SOUTH);

        frame_delete_group.setSize(250, 300);
        frame_delete_group.setVisible(false);
        frame_delete_group.setLocationRelativeTo(null);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String ingest = null;
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        if (ae.equals("Open")) {
            returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                try {
                    FileReader read = new FileReader(f);
                    Scanner scan = new Scanner(read);
                    while (scan.hasNextLine()) {
                        String line = scan.nextLine() + "\n";
                        ingest = ingest + line;
                    }
                    scan.close();
                    area.setText(ingest);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            // SAVE
        } else if (ae.equals("Save")) {
            returnValue = jfc.showSaveDialog(null);
            try {
                File f = new File(jfc.getSelectedFile().getAbsolutePath());
                FileWriter out = new FileWriter(f);
                out.write(area.getText());
                out.close();
            } catch (FileNotFoundException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "File not found.");
            } catch (IOException ex) {
                Component f = null;
                JOptionPane.showMessageDialog(f, "Error.");
            }
        } else if (ae.equals("New")) {
            area.setText("");
        } else if (ae.equals("Quit")) {
            System.exit(0);
        } else if (ae.equals("Start a New Group")) {

            int i = JOptionPane.showConfirmDialog(null, "New Group will start on the next line. Are you sure?", "Warning!", JOptionPane.INFORMATION_MESSAGE);
            if (i == 0) {
                EditorActions.createNewGroup(
                        area
                        , undoPropsList
                        , editsValues
                        , table1,undoPropsList2,editsValues2,tableTemp);
            }
        } else if (ae.equals("View/Undo Edit(s)")) {
            frame_edits.remove(b2);
            frame_edits.add(b1, BorderLayout.SOUTH);
            frame_delete_group.setVisible(false);
            frame_edits.setVisible(true);

            EditorActions.deleteEdits(
                    frame_edits
                    , frame_delete_group
                    , b2
                    , b1
                    , table1
                    , area
                    , undoPropsList
                    , PK
                    , "undoEdits",tableTemp,undoPropsList2);

        } else if (ae.equals("Delete Edit(s)")) {
            EditorActions.deleteEdits(
                    frame_edits
                    , frame_delete_group
                    , b1
                    , b2
                    , table1
                    , area
                    , undoPropsList
                    , PK
                    , "removeEdits",tableTemp,undoPropsList2);

        } else if (ae.equals("Delete Group(s)")) {

            EditorActions.removeGroup(
                    table2
                    , table1
                    , b3
                    , frame_delete_group
                    , undoPropsList, groupValues,frame_edits,tableTemp,undoPropsList2);

        } else if (ae.equals("Clear All")) {
            int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear all Edits and Groups?", "Warning!", JOptionPane.INFORMATION_MESSAGE);
            if (i == 0) {
                EditorActions.clearAll(table1, table2, undoPropsList);
            }
        } else if (ae.equals("Undo Prev. (ctrl+z)")) {
            EditorActions.UndoCharacter(table1, table2, undoPropsList, area);
        }
    }
}
