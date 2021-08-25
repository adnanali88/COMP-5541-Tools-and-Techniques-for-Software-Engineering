package main;

public class CharModel {
    private Integer charPos;
    private char character;

    public Integer getCharPos() {
        return charPos;
    }

    public void setCharPos(Integer charPos) {
        this.charPos = charPos;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "CharModel{" +
                "charPos=" + charPos +
                ", character=" + character +
                '}';
    }
}
