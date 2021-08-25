package main;

import java.io.Serializable;

public class EditsProps implements Serializable {
    private Integer pk;
    private char character;
    private String Action;
    private Integer column;
    private Integer line;
    private Integer group;
    private Integer charIndex;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public Integer getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(Integer charIndex) {
        this.charIndex = charIndex;
    }

    @Override
    public String toString() {
        return "EditsProps{" +
                "pk=" + pk +
                ", character=" + character +
                ", Action='" + Action + '\'' +
                ", column=" + column +
                ", line=" + line +
                ", group=" + group +
                ", charIndex=" + charIndex +
                '}';
    }
}
