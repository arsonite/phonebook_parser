package tb.temp.eric;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Pattern;

public class run {

	/**
	 * Eine Endlosschleife die mit einem Scanner, der die Eingabe vom
	 * Nutzer ausließt. Je nach Eingabe werden SuchThreads erstellt
	 * und gestartet. Die Schleife wird mit der Eingabe \"exit\" beendet.
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		while(true) {
			if(input.hasNext()) {
				String line = input.nextLine();
				line.replaceAll("\\n", "");

				//Exit Bedingung
				if(Pattern.matches("exit", line)) {
					input.close();
					System.exit(0);

					//Name Name Nummer	
				} else if(Pattern.matches("[a-zA-ZöÖäÄüÜ]+\\s[a-zA-ZöÖäÄüÜ]+\\s\\d+", line)) {
					SearchThread t1 = new SearchThread(line.replaceAll(
							"\\s\\d", ""));
					SearchThread t2 = new SearchThread(Integer.parseInt(line.replaceAll(
							"[a-zA-ZöÖäÄüÜ]+\\s[a-zA-ZöÖäÄüÜ]+\\s", "")));
					t1.start();
					t2.start();
					try {
						t1.join();
						t2.join();
					} catch (Exception e) {
						System.err.println("join went wrong");
					}
					continue;

					//Name Nummer
				} else if(Pattern.matches("[a-zA-ZöÖäÄüÜ]+\\s\\d+", line)) {
					SearchThread t3 = new SearchThread(line.replaceAll(
							"\\s\\d", ""));
					SearchThread t4 = new SearchThread(Integer.parseInt(line.replaceAll(
							"[a-zA-ZöÖäÄüÜ]+\\s", "")));
					t3.start();
					t4.start();
					try {
						t3.join();
						t4.join();
					} catch (Exception e) {
						System.err.println("join went wrong");
					}
					continue;

					//Name || Name Name
				} else if(Pattern.matches("[a-zA-ZöÖäÄüÜ]+", line)
						|| Pattern.matches("[a-zA-ZöÖäÄüÜ]+\\s[a-zA-ZöÖäÄüÜ]+", line)) {
					SearchThread t5 = new SearchThread(line);
					t5.start();
					continue;

					//Nummer
				} else if(Pattern.matches("\\d+", line)) {
					SearchThread t6 = new SearchThread(Integer.parseInt(line));
					t6.start();
					continue;
				}
				System.err.println("Was machst du da eigentlich?");
			}
		}
	}

	/**
	 * Ausgabe vom querry String, \"Name=&Nummer=\" werden herrausgefiltert
	 * @param output Ergebnis der Suche des SuchThreads.
	 */
	public static void print(Hashtable<Integer, String> querry) {
		Enumeration<Integer> emuKey = querry.keys();

		while(emuKey.hasMoreElements()) {
			Integer key = emuKey.nextElement();
			String output = querry.get(key).replaceAll(
					"^Name=", "").replaceAll(
							"&", " ").replaceAll("Nummer=", "");
			System.out.println(output);
		}
	}
}