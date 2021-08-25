package main;

import java.util.Vector;

public class EditsTBlColumnNames {
    public static void setName(Vector<String> ColumnNames, String... params) {
        for (String param : params) {
            ColumnNames.add(param);
        }
    }
}
