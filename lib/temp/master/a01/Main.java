package tb.temp.master.a01;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by Tronico on 13.10.2015.
 * Main
 * Die Main-Klasse oeffnet ein Dialog zum suchen nach Telefoneintraegen
 * und gibt die gefundenen Eintraege in der Konsole zurueck.
 */
public class Main {

    /**
     * telTab repraesentiert eine Telefontabelle mit Namen und zugehoerigen Telefonnummer
     * (2 dimensionales String-array)
     */
    final static String[][] telTab = {
                                        {"Meier", "4711"},
                                        {"von Schmitt", "0815"},
                                        {"M\u00FCller", "4711"},
                                        {"Meier", "0816"}
                                     };
    
    /**
     * suchName beinhaltet den zu suchenden Namen als String
     */
    static String suchName = "";
    /**
     * suchNr beinhaltet die zu suchende Telefonnummer als String
     */
    static String suchNr = "";
    /**
     * suchart beinhaltet eine Zahl, die die Art der Suche repraesentiert
     * 1 - suche nach Name
     * 2 - suche nach Telefonnummer
     * 3 - suche nach Name und Telefonnummer
     */
    static int suchArt = 0;
    
    /**
     * ergTelTabNr beinhaltet das Ergebnis der suche nach der Nummer
     */
    static String[][] ergTelTabNr = new String[telTab.length][2];
    /**
     * ergTelTabNa beinhaltet das Ergebnis der suche nach der Name
     */
    static String[][] ergTelTabNa = new String[telTab.length][2];
    /**
     * scanner dient zum einlesen von der Konsole
     */
    static Scanner scanner = new Scanner(System.in);

    /**
     * Main-Methode zum starten des Programmes
     * ist eine Dauerschleife, welche mit "exit" verlassen werden kann
     * @param args not supported
     */
    public static void main(String[] args) {
        int searchOption;

        while (true) {
            openingDialogue();          //eroeffnet die Interaktion mit dem User
                switch (suchArt) {
                    case 1:
                        search(suchName, ergTelTabNa);
                        break;
                    case 2:
                        search(suchNr, ergTelTabNr);
                        break;
                    case 3:
                        search(suchName, suchNr);
                }
            printOut();
            }
    }

    /**
     * Die methode dialog ist zur interaktion mit dem User
     * erst wird nach der Art der Suche gefragt und dann nach dem Suchwert
     * beide Angaben werden den Variablen suchart und suchValue gespeichert
     */
    private static void openingDialogue(){

        System.out.println("\nHallo ich bin ein Telefonverzeichnis!\n------------------------------------------");
        System.out.println("Wonach willst du suchen?");
        System.out.println("1 --> nach dem Namen,\n2 --> nach der Nummer,\n3 --> nach Namen und Nummer,\nexit --> f\u00FCr einen Abbruch");
        System.out.println("Bitte gib die zutreffende Zahl ein(1, 2, 3 oder exit): ");
        String input = scanner.nextLine();
        System.out.println("\n---------------------------------------------");
        while(true) {
            if (input.equals("1") || input.equals("2") || input.equals("3")) {
                suchArt = Integer.parseInt(input);
                break;
            } else if (input.equals("exit")){
                abbruch();
            }
            System.out.println("\nFalsche Angabe!\n\n" +
                    "---------------------------------------------" +
                    "\nBitte gib die zutreffende Zahl ein(1, 2, 3 oder exit): ");
            input = scanner.nextLine();
        }
            switch (suchArt) {
                case 1:
                    suchName = namensAbfrage();
                    break;
                case 2:
                    suchNr = nummerAbfrage();
                    break;
                case 3:
                    suchName = namensAbfrage();
                    suchNr = nummerAbfrage()
                    ;
                    break;
                default:
                    System.out.println("valuefehler in Suchart! '" + suchArt + "'");
            }
        System.out.println();
    }

    static void abbruch(){
        System.out.println();
        System.out.println("Auf Wiedersehen!");
        System.exit(0);
    }

    /**
     * Methode namenAbfrage fuehrt ein dialog um den Namen zu erfragen
     * Zahlen als Antwort sind nicht erlaubt
     * @return zu suchender Name als String
     */
    private static String namensAbfrage(){
        while(true){
            System.out.println("Bitte gib den Namen an:");
            String eingabe = scanner.nextLine().trim();

            if(eingabe.equals("exit"))abbruch();
            if(eingabe.isEmpty()){
                System.out.println("Eine leere Eingabe ist nicht g\u00FCltig!\n---------------------------------------------\n");
                continue;
            }
            if(!(Pattern.matches("\\W+", eingabe)) && Pattern.matches("\\D+", eingabe))return eingabe;
            else System.out.println("Nur Buchstaben und Leerzeichen erlaubt!\n---------------------------------------------\n");
        }
    }

    /**
     * Methode nummerAbfrage fuehrt ein dialog um die Telefonnummer zu erfragen
     * es sind nur Zahlen erlaubt
     * @return zu suchende Telefonnummer als String
     */
    private static String nummerAbfrage(){
        while(true){
            System.out.println("Bitte gib die Telefonnummer an:");
            String eingabe = scanner.nextLine().trim();

            if(eingabe.equals("exit"))abbruch();
            if(eingabe.isEmpty()){
                System.out.println("Eine leere Eingabe ist nicht g\u00FCltig!\n---------------------------------------------\n");
                continue;
            }
            int hit = 0;
            for(char i : eingabe.toCharArray()){
                if((i + "").matches("\\d")) hit++;
            }
            if(hit < eingabe.length()) System.out.println("Nur Zahlen erlaubt!\n---------------------------------------------\n");
            else return eingabe;
        }
    }

    /**
     * durchsucht die Telefonliste
     * @param suchValue name oder tefonnummer nach der gesucht werden soll
     * @param ergTab ergebnisstabelle in die das ergebnis geschrieben werden soll
     */
    private static void  search(String suchValue, String[][] ergTab){

            SearchThread suchen = new SearchThread(suchValue, telTab, ergTab);
            suchen.start();
            try {
                suchen.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    /**
     * doppelsuche (nach name und telefonnummer) in der Telefonliste
     * @param suchName name, nachdem gesucht werden soll
     * @param suchNr nummer, nach der gesucht werden soll
     */
    private static void  search(String suchName, String suchNr){

        SearchThread suche1 = new SearchThread(suchName, telTab, ergTelTabNa);
        SearchThread suche2 = new SearchThread(suchNr, telTab, ergTelTabNr);

            suche1.start();
            suche2.start();
            try {
                suche1.join();
                suche2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    /**
     * printOut dient der ausgabe des Ergebnisses in der Konsole
     */
    private static void printOut(){
        boolean hit = false;
        System.out.println("---------------------------------------------\n");
        for(int i = 0; i < ergTelTabNa.length; i++){
               if(ergTelTabNa[i][0] != null){
                   System.out.println(ergTelTabNa[i][0] + " : " + ergTelTabNa[i][1]);
                   hit = true;
               }
        }

        for(int i = 0; i < ergTelTabNr.length; i++){
            if(ergTelTabNr[i][0] != null){
                System.out.println(ergTelTabNr[i][0] + " : " + ergTelTabNr[i][1]);
                hit = true;
            }
        }
        if(!hit)System.out.println("Es gab leider keinen Treffer!");
        resetErgList();
        System.out.println("\n---------------------------------------------");
    }

    /**
     * leer die Ergebnislisten
     */
    private static void resetErgList(){
        ergTelTabNr = new String[telTab.length][2];
        ergTelTabNa = new String[telTab.length][2];
    }
}
