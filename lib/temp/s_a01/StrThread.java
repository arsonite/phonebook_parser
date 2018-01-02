package tb.temp.s_a01;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alexander Görisch on 10.10.2017.
 */

public class StrThread extends Thread {
    private String name;
    public List ausgabe;

    protected StrThread(String name, List ausgabe) {
        this.name = name;
        this.ausgabe = ausgabe;
    }

    public void run() {
        boolean found = false;
        Telefon buch = new Telefon();
        Iterator<AbstractMap.SimpleEntry> it = buch.iterator();
        while (it.hasNext()) {
            AbstractMap.SimpleEntry entry = it.next();
            if (entry.getKey().equals(name)) {
                ausgabe.add(entry.getKey() + " " + entry.getValue());
                found = true;
            }
        }
        if (!found) {
            ausgabe.add("Kein Eintrag zu " + name);
        }
    }
}
