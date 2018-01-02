package tb.temp.s_a01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Alexander Görisch on 10.10.2017.
 */
public class Main {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                System.out.println("Bitte Name eingeben(Exit zum schließen)");
                String name = reader.readLine();
                if (name.equals("Exit") || name.equals("exit")) {
                    break;
                }
                if(!name.isEmpty()){//Test ob Name valide
                    if(!name.matches("[a-zA-Z]+")){
                        System.out.println("Nicht valider Name");
                        continue;
                    }
                }
                System.out.print("Bitte Nummer eingeben");
                String nummer = reader.readLine();

                if(!nummer.isEmpty()){//Test ob Nummer valide
                    if(!nummer.matches("[0-9]+")){
                        System.out.println("Nicht valide Nummer");
                        continue;
                    }
                }
                if (name.isEmpty() && nummer.isEmpty()) {
                    System.out.println("Bitte etwas eingeben.");
                    continue;
                }
                List<String> ausgabe= Scan.analyse("Name=" + name + "&Nummer=" + nummer);
                Iterator<String> it = ausgabe.iterator();
                while (it.hasNext()) {
                    System.out.println(it.next());
                }
            }
            reader.close();
        } catch (IOException e) {
        }

    }
}
