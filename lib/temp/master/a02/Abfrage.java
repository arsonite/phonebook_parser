package tb.temp.master.a02;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tronico
 */
public class Abfrage {

    /**
     * telTab repraesentiert eine Telefontabelle mit Namen und zugehoerigen
     * Telefonnummer (2 dimensionales String-array)
     */
    final static String[][] telTab = {
        {"Meier", "4711"},
        {"von Schmitt", "0815"},
        {"M\u00FCller", "4711"},
        {"Meier", "0816"}
    };

    private Abfrage() {
    }

    public static String[][] oneValueSearch(String value) {
        String[][] tempRes = new String[telTab.length][2];

        SearchThread suchen = new SearchThread(value, telTab, tempRes);
        suchen.start();
        try {
            suchen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return removeNullRow(tempRes);
    }

    public static String[][] twoValueSearch(String name, String nummer) {
        String[][] tempRes1 = new String[telTab.length][2];
        String[][] tempRes2 = new String[telTab.length][2];

        SearchThread suche1 = new SearchThread(name, telTab, tempRes1);
        SearchThread suche2 = new SearchThread(nummer, telTab, tempRes2);

        suche1.start();
        suche2.start();
        try {
            suche1.join();
            suche2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return mergeStringArray(removeNullRow(tempRes1), removeNullRow(tempRes2));
    }

    private static String[][] mergeStringArray(String[][] array1, String[][] array2) {

        if (array1 == null && array2 == null) {
            return null;
        }

        int rowsNeeded = (array1 != null ? array1.length : 0) + (array2 != null ? array2.length : 0);
        int count = 0;
        String[][] res = new String[rowsNeeded][2];

        if (array1 != null) {
            for (String[] row : array1) {
                res[count][0] = row[0];
                res[count][1] = row[1];
                count++;
            }
        }
        if (array2 != null) {
            for (String[] row : array2) {
                res[count][0] = row[0];
                res[count][1] = row[1];
                count++;
            }
        }

        return res;
    }

    /*
     * this remove all rows with value null
     * if all rows empty return null
     */
    private static String[][] removeNullRow(String[][] array) {
        int notEmptyRows = 0;
        String[][] res;

        for (String[] row : array) {
            if (row[0] != null) {
                notEmptyRows++;
            }
        }

        if (notEmptyRows == 0) {
            return null;
        }

        res = new String[notEmptyRows][2];

        for (int i = 0; i < notEmptyRows; i++) {
            res[i][0] = array[i][0];
            res[i][1] = array[i][1];
        }

        return res;
    }
}
