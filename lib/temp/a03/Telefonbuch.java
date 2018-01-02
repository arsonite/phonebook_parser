
package tb.temp.a03;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.FileInputStream;
/**
 * @author Sprotte Telefonbuch Programm zum testen von Threads
 *
 */
public class Telefonbuch {

	private static Telefonbuch buch = new Telefonbuch();
	public static ArrayList<String> ausgabeName = new ArrayList<String>();
	public static ArrayList<String> ausgabeNummern = new ArrayList<String>();

	public void Suchen(String[] input) throws Exception {
		
		
		if (input[0] != null && input[1] != null) {
			Thread thread1 = buch.new SuchThread( input[0].matches("[0-9]+") ? Integer.parseInt(input[0]) : input[0]);
			Thread thread2 = buch.new SuchThread( input[1].matches("[0-9]+") ? Integer.parseInt(input[1]) : input[1]);
			thread1.start();
			thread2.start();
			thread1.join();
			thread2.join();
		} else {
			Thread thread = buch.new SuchThread(input[0] == null ? input[1]	: input[0]);
			thread.start();
			thread.join();
		}		
	}

	// private Thread Klasse die die Suche realisiert
	public class SuchThread extends Thread {
		String name;
		Integer number;

		// konstruktor wird mit Such Objekt aufgerufen und in seine Instanceof
		// Klasse zurück gecastet
		SuchThread(Object input) {
			if (input instanceof Integer)
				this.number = (Integer) input;
			if (input instanceof String)
				this.name = (String) input;
		}

		// thread run methode
		public void run() {
			try {
				ausgabeName=new ArrayList<String>();
				ausgabeNummern=new ArrayList<String>();
				// ließt per Bufferreader die telefonbuch txt ein und überprüft
				// ob es das such objekt ist
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("telefonbuch.txt"), "UTF8"));
				String zeile = null;
				if (name == null) {
					boolean found = false;
					while ((zeile = in.readLine()) != null) {
						if (zeile.contains(number.toString())) {
							System.out.println("Server Search Thread for Numbers");
							ausgabeNummern.add(zeile);
							found = true;
						}
					}
					if (!found) {
						ausgabeNummern.add("Die Suche nach " + number.toString()
								+ " war erfolglos!");
					}
				} else {
					boolean found = false;
					while ((zeile = in.readLine()) != null) {						
						if (zeile.toLowerCase().contains(name.toLowerCase())) {
							System.out.println("Server Search Thread for Strings");
							ausgabeName.add(zeile);
							found = true;
						}
					}
					if (!found) {
						ausgabeName.add("Die Suche nach " + name.toLowerCase()
								+ " war erfolglos!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
