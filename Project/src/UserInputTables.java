package main;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Vector;

public class UserInputTables {
    private static int pk = 0, temp = 0, group = 0, lastPK, j = 0;
    private static JTable table;
    private static boolean startNewGroup = false, firstLine = true;
    private static int startLine = 1, endLine = 1,startNewGroupCounter = 0;

    public static int getLastPK() {
        return lastPK;
    }

    public static void setLastPK(int lastPK) {
        UserInputTables.lastPK = lastPK;
    }

    public static int getGroup() {
        return group;
    }

    public static void setGroup(int group) {
        UserInputTables.group = group;
    }

    public static boolean isFirstLine() {
        return firstLine;
    }

    public static void setFirstLine(boolean firstLine) {
        UserInputTables.firstLine = firstLine;
    }

    public static int getStartLine() {
        return startLine;
    }

    public static void setStartLine(int startLine) {
        UserInputTables.startLine = startLine;
    }

    public static int getEndLine() {
        return endLine;
    }

    public static void setEndLine(int endLine) {
        UserInputTables.endLine = endLine;
    }

    public static boolean isStartNewGroup() {
        return startNewGroup;
    }

    public static void setStartNewGroup(boolean startNewGroup) {
        UserInputTables.startNewGroup = startNewGroup;
    }

    public static void setStartNewGroupCounter(int startNewGroupCounter) {
        UserInputTables.startNewGroupCounter = startNewGroupCounter;
    }

    public static void setPkAndTable(int PK, JTable table1) {
        pk = PK;
        table = table1;
    }

    public static void setValues(JTextArea area, Vector<Vector> vectorValues, Vector<Vector> groupValues, List<EditsProps> undoPropsList, JTable groupTable, JTable editsTable,List<EditsProps> undoPropsList2,Vector<Vector> vectorValues2) {
        area.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                int lineNum;
                int columnNum;

                EditsProps prop = new EditsProps();
                try {
                    int charPos = area.getCaretPosition();
                    System.out.println("pos: "+charPos);
                    lineNum = area.getLineOfOffset(charPos);
                    columnNum = charPos - area.getLineStartOffset(lineNum);
                    if (e.getKeyCode() != 10) {
                        prop.setCharacter(e.getKeyChar());
                        prop.setAction("insert");
                        prop.setLine(++lineNum);
                        setEndLine(lineNum);
                        prop.setColumn(columnNum);
                        temp = columnNum;
                        if (table.getRowCount() > 0) {
                            pk = table.getRowCount();
                            prop.setPk(table.getRowCount());
                        } else {
                            prop.setPk(pk);
                        }
//                        prop.setCharIndex(pk + 1);
                        prop.setCharIndex(charPos);
                        undoPropsList.add(prop);
                        undoPropsList2.add(prop);
                        prop.setGroup(getGroup());
                        setGroupNumber(pk, prop, getEndLine());

                    } else {
                        pk = table.getRowCount();
                        addNewLine(prop, lineNum, pk,charPos);
                        setEndLine(lineNum + 1);
                        undoPropsList.add(prop);
                        undoPropsList2.add(prop);
                        setGroupNumber(pk, prop, getEndLine());
                        temp = 0;
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                Vector<String> vector = new Vector<>();
                vector.addElement(prop.getGroup().toString());
                vector.addElement(prop.getLine().toString());
                vector.addElement(prop.getColumn().toString());
                vector.addElement(prop.getAction());
                vector.addElement(String.valueOf(prop.getCharacter()));
                vector.addElement(String.valueOf(prop.getCharIndex())); //********************************************************************
                vector.addElement(prop.getPk().toString());
                vectorValues.addElement(vector);
                vectorValues2.addElement(vector);
            }
        });
    }

    private static void setGroupNumber(int pk, EditsProps prop, int line) {
        GroupProps params = new GroupProps();
        setGroupProps(params, prop, 1, line - 1, getGroup(), pk, getGroup());
        setGroupProps(params, prop, line - 1, line - 1, getGroup(), pk - getLastPK(), getGroup());
    }

    public static void addNewLine(EditsProps prop, int lineNo, int pk,int charPos) {
        prop.setAction("newLine");
        prop.setLine(lineNo);
        prop.setColumn(++temp);
        prop.setPk(pk);
//        prop.setCharIndex(pk + 1);
        prop.setCharIndex(charPos);
        prop.setCharacter('\n');
    }

    private static void setGroupProps(GroupProps params, EditsProps prop, int startLine, int endLine, int groupId, int editsNo, int group) {
        params.setStartLine(startLine);
        params.setEndLine(endLine);
        if (!firstLine) {
            groupId = groupId - 1;
        }
        params.setGroupId(groupId);
        params.setEditsNo(editsNo);
        if (!isStartNewGroup()) {
            prop.setGroup(group);
        }
        prop.setGroup(group);
    }
}
