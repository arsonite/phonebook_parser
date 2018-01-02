package tb.temp.s_a01;


import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander Görisch on 10.10.2017.
 */
public class Scan {
    /**
     * Analysiert den gegebenen Input undruft die entsprechenden Suchmethoden auf.
     *
     * @param s sei ein Eingabestring der aus Nummern und Buchstaben besteht
     */
    protected static List<String> analyse(String s) {
        Pattern r = Pattern.compile("(\\d)");
        Matcher m = r.matcher((s));
        String se = "";
        while (m.find()) {
            se += m.group();
        }
        String sn = s.substring(s.indexOf("=") + 1, s.indexOf("&"));
        if (se.isEmpty()) {
            return searchName(sn);
        }
        else if (sn.isEmpty()) {
            return searchNumber(se);
        }
        else {
            return searchJoin(sn, se);
        }

    }

    /**
     * Startet einen Thread der Im Telefonbuch die gegebene Nummer sucht
     *
     * @param i sei ein Integer Wert
     */
    protected static List<String> searchNumber(String i) {
        List<String> ausgabe = new ArrayList();
        Thread th = new NumThread(i, ausgabe);
        th.start();
        try {
            th.join();
        } catch (Exception e) {
        }
        return ausgabe;
    }

    /**
     * Startet einen Thread der im Telefonbuch den gegebenen Namen sucht
     *
     * @param s sei ein String
     */
    protected static List<String> searchName(String s) {
        List<String> ausgabe = new ArrayList();
        Thread th = new StrThread(s, ausgabe);
        th.start();
        try {
            th.join();
        } catch (Exception e) {
        }
        return ausgabe;
    }

    /**
     * Startet zwei Threads die nebenläufig Im Telefonbuch nach Namen und Nummer suchen
     *
     * @param s sei ein String
     * @param i sei ein Integer
     */
    protected static List<String> searchJoin(String s, String i) {
        List ausgabe = new ArrayList();
        Thread threadStr = new StrThread(s, ausgabe);
        Thread threadNum = new NumThread(i, ausgabe);
        try {
            threadNum.start();
            threadStr.start();
            threadNum.join();
            threadStr.join();

        } catch (Exception e) {
            System.out.println("Thread interrupted");
        }
        return ausgabe;
    }
}
