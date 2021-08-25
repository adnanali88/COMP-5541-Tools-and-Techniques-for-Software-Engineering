package main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Actions {
    static int[] rows;
    static boolean selectListnerInit = false;

    public static void deleteEdits(
            JFrame frameEdits
            , JFrame frameDeleteGroup
            , Button b1
            , Button b2
            , JTable table1
            , Vector<Vector> editValues
            , JTextArea area
            , Vector<Vector> groupValues
            , List<EditsProps> undoPropsList) {
        Set<Integer> editList = new HashSet<>();
        frameEdits.remove(b1);
        frameDeleteGroup.setVisible(false);
        frameEdits.setVisible(true);
        frameEdits.add(b2, BorderLayout.SOUTH);
        if (!selectListnerInit) {
            table1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectListnerInit = true;
                    rows = table1.getSelectedRows();
                    for (int row : rows) {
                        editList.add(row);
                    }
                }
            });
        }

        if (b2.getActionListeners().length > 1)
            return;


        b2.addActionListener(e -> {
            int k = 0;
            Vector<Vector> vectorData = new Vector<>();
            frameEdits.setVisible(false);

//            while (iterator.hasNext()) {
//                int next = iterator.next();
//                EditsProps prop = undoPropsList.get(next);
//                Vector<String> vector = new Vector<>();
//                vector.addElement(prop.getGroup().toString());
//                vector.addElement(prop.getLine().toString());
//                vector.addElement(prop.getColumn().toString());
//                vector.addElement(prop.getAction());
//                vector.addElement(String.valueOf(prop.getCharacter()));
//                vector.addElement(prop.getPk().toString());
//                vectorData.addElement(vector);


            List<EditsProps> newList = new ArrayList<>();

//                    System.out.println("nextttttttt:"+next);
            for (int i = 0; i < undoPropsList.size(); i++) {

                boolean add = true;

                Iterator<Integer> iterator = editList.iterator();
                while (iterator.hasNext()) {
                    int next = iterator.next();
                    if (i == next) {
                        add = false;
                    }
                }
                if (add) {

                    EditsProps props = undoPropsList.get(i);
                    props.setPk(k++);
                    Vector<String> vector1 = new Vector<>();
                    vector1.addElement(props.getGroup().toString());
                    vector1.addElement(props.getLine().toString());
                    vector1.addElement(props.getColumn().toString());
                    vector1.addElement(props.getAction());
                    vector1.addElement(String.valueOf(props.getCharacter()));
                    vector1.addElement(props.getPk().toString());
                    vectorData.addElement(vector1);
                    newList.add(props);
                }
            }
//            }
//            editValues.removeAll(vectorData);
            editValues.removeAllElements();
            editValues.addAll(vectorData);
            undoPropsList.clear();
            undoPropsList.addAll(newList);
            editList.clear();
            removeRows(table1, rows);
        });
    }

    private static void removeRows(JTable table, int[] rowsSelected) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = rowsSelected.length - 1; i >= 0; i--) {
            int k = rowsSelected[i];
            model.removeRow(k);
        }
    }
}
