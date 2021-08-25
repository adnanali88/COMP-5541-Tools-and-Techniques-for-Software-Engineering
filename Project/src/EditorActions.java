package main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditorActions {
    private static int[] rows;
    private static boolean selectListenerInit = false, selectListenerInitGroup = false, firstTime = true, first = true, last = true;
    private static Map map = new ConcurrentHashMap();
    private static int counter = 0, count = 0, groupRowNumber = 0;
    private static List<CharModel> list = new ArrayList<>();
    private static List<CharModel> list2 = new ArrayList<>();
    private static List<StringBuilder> editorList = new ArrayList<>();
    private static List<Character> finalList = new ArrayList<>();
    private static StringBuilder stringBuilder = new StringBuilder();

    public static void deleteEdits(
            JFrame frameEdits
            , JFrame frameDeleteGroup
            , Button b1
            , Button b2
            , JTable table1
            , JTextArea area
            , List<EditsProps> undoPropsList
            , int PK
            , String action, JTable tableTemp, List<EditsProps> undoPropsList2) {

        Set<Integer> editList = new HashSet<>();
        frameEdits.remove(b1);
        frameDeleteGroup.setVisible(false);
        frameEdits.setVisible(true);
        frameEdits.add(b2, BorderLayout.SOUTH);

        table1.getColumnModel().getColumn(5).setMinWidth(0);
        table1.getColumnModel().getColumn(5).setMaxWidth(0);

        checkTableListener(table1, editList, selectListenerInit);

        if (b2.getActionListeners().length > 1) {
            return;
        }

        b2.addActionListener(e -> {
            frameEdits.setVisible(false);
//            counter=counter+rows.length;
            removeRows2(table1, rows, area, undoPropsList, action, tableTemp, undoPropsList2);

        });
    }

    public static void createNewGroup(JTextArea area, List<EditsProps> undoPropsList, Vector<Vector> editValues, JTable table, List<EditsProps> undoPropsList2, Vector<Vector> editValues2, JTable table2) {
        area.insert("\n", area.getCaretPosition());
        int rowCount = table.getRowCount();
        int size = undoPropsList.size();
        EditsProps props = undoPropsList.get(size - 1);
        EditsProps prop = new EditsProps();
        prop.setAction("newLine");
        prop.setCharIndex(props.getCharIndex() + 1);
        prop.setColumn(props.getColumn() + 1);
        prop.setGroup(props.getGroup());

        UserInputTables.setGroup(UserInputTables.getGroup() + 1);

        Integer line = props.getLine();
        prop.setLine(line++);
        prop.setPk(rowCount);
        Vector<String> vector = new Vector<>();
        vector.addElement(prop.getGroup().toString());
        vector.addElement(prop.getLine().toString());
        vector.addElement(prop.getColumn().toString());
        vector.addElement(prop.getAction());
        vector.addElement(String.valueOf(prop.getCharacter()));
        vector.addElement(String.valueOf(prop.getCharIndex()));
        vector.addElement(prop.getPk().toString());
        editValues.addElement(vector);
        editValues2.addElement(vector);
        undoPropsList.add(prop);
        undoPropsList2.add(prop);
        UserInputTables.setStartNewGroup(true);
        UserInputTables.setFirstLine(false);
        UserInputTables.setStartNewGroupCounter(0);

    }

    private static void removeRows2(JTable table, int[] rowsSelected, JTextArea area, List<EditsProps> undoPropsList, String action, JTable tableTemp, List<EditsProps> undoPropsList2) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        if (first) {
        list.clear();
        for (int a = 0; a < model.getRowCount(); a++) {
            CharModel charModel = new CharModel();
            charModel.setCharPos(Integer.parseInt(model.getValueAt(a, 5).toString()));
            charModel.setCharacter(table.getValueAt(a, 4).toString().charAt(0));
            list.add(charModel);
        }
//            first = false;
//        }
        if ("undoEdits".equals(action)) {
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                removeFromEditor(area, list.get(rowsSelected[i]).getCharPos());
                list.remove(rowsSelected[i]);
                if (i == 0) {
                    for (int j = rowsSelected[i]; j < table.getRowCount(); j++) {
                        int i1 = Integer.parseInt(String.valueOf(table.getValueAt(j, 5))) - (rowsSelected.length);
                        table.setValueAt(i1, j, 5);
                    }
                }
            }
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                int k = rowsSelected[i];
                model.removeRow(k);
                int t = k;
                if (i == 0) {
                    for (int j = k; j < table.getRowCount(); j++) {
                        table.setValueAt(t++, j, 6);
                    }
                }
            }
        }else {
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                int k = rowsSelected[i];
                model.removeRow(k);
                int t = k;
                if (i == 0) {
                    for (int j = k; j < table.getRowCount(); j++) {
                        table.setValueAt(t++, j, 6);
                    }
                }
                list.remove(k);
            }
        }
    }

    private static void removeRows(JTable table, int[] rowsSelected, JTextArea area, List<EditsProps> undoPropsList, String action, JTable tableTemp, List<EditsProps> undoPropsList2) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        DefaultTableModel model2 = (DefaultTableModel) tableTemp.getModel();

        //////////////////////////////////////////////
        if (first) {
            list.clear();
//        list2.clear();
            System.out.println("***************************************");
            for (int a = 0; a < model.getRowCount(); a++) {
                CharModel charModel = new CharModel();
                charModel.setCharPos(Integer.parseInt(model.getValueAt(a, 5).toString()) + 1);
                charModel.setCharacter(table.getValueAt(a, 4).toString().charAt(0));
                list.add(charModel);
            }
//        for (int a = 0; a < model2.getRowCount(); a++) {
//            CharModel charModel = new CharModel();
//            charModel.setCharPos(Integer.parseInt(model2.getValueAt(a, 5).toString()) + 1);
//            charModel.setCharacter(tableTemp.getValueAt(a, 4).toString().charAt(0));
//            list2.add(charModel);
//        }
            first = false;
        }
        System.out.println("updated list: " + list);

        if ("undoEdits".equals(action)) {

            int t = 0;
            for (int j = 0; j < list.size(); j++) { // int j=w bud
                CharModel charModel = new CharModel();
//                    charModel.setCharPos(t+1);
                charModel.setCharPos(t + 1);
                charModel.setCharacter(list.get(j).getCharacter());
                list.set(t, charModel);
//                    t++;
                t++;
            }

            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                int w = rowsSelected[i];
                if (count == 0) {
//                    System.out.println("char removed: "+list.get(rowsSelected[i]).getCharacter());
//                    System.out.println("char position: "+list.get(rowsSelected[i]).getCharPos());
                    if (Integer.parseInt((String) table.getValueAt(table.getRowCount() - 1, 0)) < groupRowNumber) {
                        System.out.println("****************************omid****************************");
//                        counter=0;
                    }
                    removeFromEditor(area, list.get(rowsSelected[i]).getCharPos() + counter);
                    list.remove(rowsSelected[i]);
////                    list2.remove(rowsSelected[i]);
                } else {
//                    System.out.println("char removed: "+list.get(rowsSelected[i]).getCharacter());
                    removeFromEditor(area, list.get(rowsSelected[i]).getCharPos() + counter);
                    list.remove(rowsSelected[i]);
////                    list2.remove(rowsSelected[i]);
                }

//                System.out.println("char removed: "+list.get(rowsSelected[i]).getCharacter());
//                System.out.println("char position: "+list.get(rowsSelected[i]).getCharPos());
//                if(counter==0) {
//                    removeFromEditor(area, list.get(rowsSelected[i]).getCharPos());
//                    counter=counter-rowsSelected.length;
//                }
//                else {
//                    removeFromEditor(area, list.get(rowsSelected[i]).getCharPos()+counter);
//                    counter=counter-rowsSelected.length;
////                    counter=0;
//                }
                count++;
//                int t=w;
////                int t=0;
//                for(int j=w;j<list.size();j++){ // int j=w bud
//                    CharModel charModel = new CharModel();
//                    charModel.setCharPos(t+1);
////                    charModel.setCharPos(t+1);
//                    charModel.setCharacter(list.get(j).getCharacter());
//                    list.set(t,charModel);
//                    t++;
////                    t++;
//                }

//                for(int j=w;j<list2.size();j++){
//                    CharModel charModel = new CharModel();
//                    charModel.setCharPos(t+1);
//                    charModel.setCharacter(list2.get(j).getCharacter());
//                    list2.set(t,charModel);
//                    t++;
//                }
            }

            count = 0;
        }
        /////////////////////////////////////////////

        for (int i = rowsSelected.length - 1; i >= 0; i--) {
            int k = rowsSelected[i];
            model.removeRow(k);
//            if("undoEdits".equals(action)){
//                model2.removeRow(k);
//                undoPropsList2.remove(k);
//            }

            if (!"undoEdits".equals(action)) {
                list.remove(k);
//                counter=rowsSelected.length;
            }
//            if ("undoEdits".equals(action)) {
////                removeFromEditor(area, k);
//                System.out.println("heyyyyyyyyyyyyy: "+list.get(k).getCharPos());
//                removeFromEditor(area, list.get(k).getCharPos());
//                list.remove(k);
//            }
            undoPropsList.remove(k);

            int t = k;
            if (i == 0) {
                for (int j = k; j < table.getRowCount(); j++) {
                    table.setValueAt(t++, j, 5);
                }
//                for (int j = k; j < tableTemp.getRowCount(); j++) {
//                    tableTemp.setValueAt(t++, j, 5);
//                }

            }

//            System.out.println("row count: "+table.getRowCount());
            table.getRowCount();
        }
//        map.clear();
//        for(int s=0;s<table.getRowCount();s++){
//            CharModel charModel = new CharModel();
////                System.out.println("hey: "+table.getValueAt(s,5));
//            charModel.setCharPos(Integer.parseInt(table.getValueAt(s,5).toString())+1);
//            charModel.setCharacter(table.getValueAt(s,4).toString().charAt(0));
//            map.put(++counter,charModel);
//        }
//        counter=0;
//        for (int i = rowsSelected.length - 1; i >= 0; i--) {
//            int k = rowsSelected[i];
//            int t = k;
//            if (i == 0) {
//                for (int j = k; j < table.getRowCount(); j++) {
//                    table.setValueAt(t++, j, 5);
//                }
//            }
//        }
//        System.out.println("map: "+map);
        System.out.println("final list 1: " + list);
        System.out.println("final list 2: " + list2);
    }

    private static void removeFromEditor(JTextArea area, int k) {
        try {
//            int lineNum,columnNum;
//            int charPos = area.getCaretPosition();
//            System.out.println("pos: "+charPos);
//            lineNum = area.getLineOfOffset(charPos);
//            columnNum = charPos - area.getLineStartOffset(lineNum);
//            System.out.println("column No: "+columnNum);
//            Document document = area.getDocument();
//            document.remove(k-1, 1);
//            area.selectAll();
//            if(rows.length==1){
//                System.out.println("hey hey");
            area.select(k - 1, k);
            area.replaceSelection("");
//            }
//            else {
//                area.select(k - 1, k);
//                area.replaceSelection("");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeGroup(JTable table, JTable tableEdits, Button button, JFrame frame, List<EditsProps> editsPropsList, Vector<Vector> groupValues, JFrame frameEdits, JTable tableTemp, List<EditsProps> editsPropsList2) {
        frameEdits.setVisible(false);
        frame.setVisible(true);
        if (firstTime) {
            showGroup(editsPropsList, groupValues, table);
            firstTime = false;
        }
        showGroup(editsPropsList, groupValues, table);

        Set<Integer> editListGroup = new HashSet<>();

        checkTableListener(table, editListGroup, selectListenerInitGroup);

        if (button.getActionListeners().length > 1) {
            return;
        }

        button.addActionListener(e -> {
            frame.setVisible(false);
            removeGroupTableRows(table, tableEdits, rows, editsPropsList, tableTemp, editsPropsList2);
        });
    }

    private static void removeGroupTableRows(JTable table, JTable tableEdits, int[] rowsSelected, List<EditsProps> editsPropsList, JTable tableTemp, List<EditsProps> editsPropsList2) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<Integer> selected = new ArrayList<>();
        for (int i = rowsSelected.length - 1; i >= 0; i--) {
            int k = rowsSelected[i];
            for (int j = 0; j < tableEdits.getRowCount(); j++) {
                if (table.getValueAt(k, 0).toString().equals(tableEdits.getValueAt(j, 0))) {
                    int valueAt = Integer.parseInt(tableEdits.getValueAt(j, 6).toString());
                    selected.add(valueAt);
                    if (table.getModel().getRowCount() - 1 != Integer.parseInt((String) tableEdits.getValueAt(j, 0))) {
                        counter++;
                        groupRowNumber = Integer.parseInt((String) tableEdits.getValueAt(j, 0));
                    }
                }
            }
            model.removeRow(k);
            removeRows2(tableEdits, selected.stream().mapToInt(t -> t).toArray(), null, editsPropsList, "", tableTemp, editsPropsList2);
            selected.clear();
        }
    }

    private static void checkTableListener(JTable table, Set<Integer> editList, boolean flag) {
        if (!flag) {
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
//                    selectListenerInit = true;
                    rows = table.getSelectedRows();
                    for (int row : rows) {
                        editList.add(row);
                    }
                }
            });
        }
    }

    public static void clearAll(JTable editsTable, JTable groupTable, List<EditsProps> editsProps) {
        removeRowFromTable(editsTable);
        removeRowFromTable(groupTable);
        editsProps.clear();
    }

    private static void removeRowFromTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    public static void UndoCharacter(JTable editsTable, JTable groupTable, List<EditsProps> editsProps, JTextArea area) {
        removeLastRow((DefaultTableModel) editsTable.getModel(), area);
        removeLastRow((DefaultTableModel) groupTable.getModel(), area);
        editsProps.remove(editsProps.size() - 1);
    }

    private static void removeLastRow(DefaultTableModel model, JTextArea area) {
        if (model.getRowCount() >= 1) {
            Object valueAt = model.getValueAt(model.getRowCount() - 1, 6);
//            removeFromEditor(area, Integer.valueOf((String) valueAt) + 1);
            removeFromEditor(area, Integer.valueOf(String.valueOf(valueAt)) + 1);
            model.removeRow(model.getRowCount() - 1);
        }
    }

    private static void showGroup(List<EditsProps> editsPropsList, Vector<Vector> groupValues, JTable gropTable) {
        if (!firstTime) {
            removeRowFromTable(gropTable);
        }
        Map<Integer, List<EditsProps>> collect = editsPropsList.stream().collect(Collectors.groupingBy(EditsProps::getGroup));
        Map<Integer, Map<String, Integer>> mapMap = new ConcurrentHashMap<>();
        GroupProps params = new GroupProps();
        for (Integer x : collect.keySet()) {

            Integer min = collect.get(x).stream().mapToInt(EditsProps::getLine).min().getAsInt();
            int last = collect.get(x).stream().mapToInt(EditsProps::getLine).max().getAsInt();

            Map<String, Integer> xl = new ConcurrentHashMap<>();
            xl.put("first", min);
            xl.put("last", last);
            xl.put("count", Math.toIntExact(collect.get(x).stream().count()));
            mapMap.put(x, xl);
        }
        for (Integer x : mapMap.keySet()) {
            Vector<String> vector = new Vector<>();
            params.setGroupId(x);
            params.setStartLine(mapMap.get(x).get("first"));
            params.setEndLine(mapMap.get(x).get("last"));
            params.setEditsNo(mapMap.get(x).get("count"));
            vector.addElement(params.getGroupId().toString());
            vector.addElement(params.getStartLine().toString());
            vector.addElement(params.getEndLine().toString());
            vector.addElement(params.getEditsNo().toString());
            groupValues.addElement(vector);
        }
    }
}

