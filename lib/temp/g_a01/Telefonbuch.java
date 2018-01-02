package tb.temp.g_a01;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 * @author Sprotte
 * Telefonbuch Programm zum testen von Threads
 *
 */
public class Telefonbuch {

	private static Telefonbuch buch = new Telefonbuch();
	private static ArrayList<String> ausgabeName = new ArrayList<String>();
	private static ArrayList<String> ausgabeNummer = new ArrayList<String>();


	public static void main(String[] args)throws Exception {
		//endless loop der auf Abfragen wartet
		while (true) {

			System.out.println("Durchsuchen Sie nach einem Namen, einer Nummer oder beidem! Quit zum Schlie�en der Anwendung!");

			//startet Scanner und splittet die Eingabe in einen Array
			Scanner scanner = new Scanner(System.in);
			String[] input = scanner.nextLine().trim().split(" ");			

			//�berpr�ft auf eine leere Eingabe
			if(input.length==0||input[0].equals("")){
				System.out.println("Leere Eingaben sind Ung�ltig!");
				continue;
			}

			//�berpr�ft ob der User das Programm mit Quit abgebrochen hat
			if(input[0].equals("quit")){
				System.out.println("Programm wird beendet");
				System.exit(0);
			}
			ArrayList<Integer> numbers= new ArrayList<Integer>();
			String name= "";

			//teilt die Eingabe in einen Array f�r Nummer und eine Namen
			for (int i = 0; i < input.length; i++) {
				if(input[i].matches("[0-9]+")){					
					numbers.add(Integer.parseInt(input[i]));
				}else if(input[i].matches("\\D*")){					
					name +=input[i].trim()+" ";
				}else{
					System.out.println(input[i]+" ist Ung�ltig");
				}
			}

			//wurde eine Nummer und ein Name eingegeben dann werden 2 Suchanfragen in separten threads gestartet 
			if(!name.equals("")&&numbers.size()!=0){
				Thread thread1 = buch.new SuchThread(name.trim());
				Thread thread2 = buch.new SuchThread(numbers.get(0));
				thread1.start();
				thread2.start();
				thread1.join();
				thread2.join();
			}else{
				//wurde nur eine nummer gesucht dann startet die suche in einem neuen thread
				for (int i = 0; i < numbers.size(); i++) {
					Thread thread = buch.new SuchThread(numbers.get(i));
					thread.start();
					thread.join();
				}
				//wurde nur ein name gesucht dann startet die suche in einem neuen thread
				if(!name.equals("")){
					Thread thread = buch.new SuchThread(name.trim());
					thread.start();
					thread.join();
				}
			}				
			//gibt die Suchergebnisse aus 
			for (String e : ausgabeName) {
				System.out.println(e);
			}
			ausgabeName.clear();

			for (String e : ausgabeNummer) {
				System.out.println(e);
			}
			ausgabeNummer.clear();

		}
	}
	//private Thread Klasse die die Suche realisiert
	private class SuchThread extends Thread {
		String name; 
		Integer number;
		//konstruktor wird mit Such Objekt aufgerufen und in seine Instanceof Klasse zur�ck gecastet
		SuchThread(Object input) {
			if(input instanceof Integer)
				this.number = (Integer)input;
			if(input instanceof String)
				this.name = (String)input;
		}
		//thread run methode
		public void run() {
			try {
				//lie�t per Bufferreader die telefonbuch txt ein und �berpr�ft ob es das such objekt ist
				BufferedReader in = new BufferedReader(new FileReader("telefonbuch"));
				String zeile = null;
				if(name==null){
					boolean found = false;
					while ((zeile = in.readLine()) != null) {
						if(zeile.contains(number.toString())){
							System.out.println("A");
							ausgabeNummer.add(zeile);
							found = true;
						}
					}
					if(!found){
						ausgabeNummer.add("Die Suche nach "+number.toString()+" war erfolglos!");
					}
				}else{
					boolean found = false;
					while ((zeile = in.readLine()) != null) { 
						if(zeile.toLowerCase().contains(name.toLowerCase())){
							System.out.println("B");
							ausgabeName.add(zeile);
							found = true;	    					
						}	    				
					}
					if(!found){
						ausgabeName.add("Die Suche nach "+name.toLowerCase()+" war erfolglos!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}