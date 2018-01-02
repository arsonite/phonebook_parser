package tb.temp.s_a01;

import java.util.AbstractMap;
import java.util.LinkedList;

/**
 * Created by Alexander Görisch on 10.10.2017.
 */
public class Telefon extends LinkedList {

    public Telefon() {

        this.add(new AbstractMap.SimpleEntry("Hans", "1234"));
        this.add(new AbstractMap.SimpleEntry("Bobbi", "1234"));
        this.add(new AbstractMap.SimpleEntry("Karo", "12345"));
        this.add(new AbstractMap.SimpleEntry("Nive", "12345"));
        this.add(new AbstractMap.SimpleEntry("Hans", "4321"));
        this.add(new AbstractMap.SimpleEntry("Bobbi", "4231"));
        this.add(new AbstractMap.SimpleEntry("Luki", "4231"));
        this.add(new AbstractMap.SimpleEntry("Saschi", "4321"));
        this.add(new AbstractMap.SimpleEntry("Görisch", "4321"));


    }

}
