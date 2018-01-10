package tb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

import java.util.ArrayList;

/**
 * The class {@code Server}
 * 
 * @since JDK 1.91 ~ <i>2017</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class Server {
	static String host;
	static int port;
	static boolean[] status; 
	static Utility u;
	static ServerSocket ss;
	static Socket cs;
	static BufferedReader bR;
	static PrintWriter out;
	static SynchronizedList<PhonebookEntry> list; 

	public static void main(String[] args ) throws Exception {
		StreamBuffer.fixConsole();
		u = new Utility();
		ArrayList<PhonebookEntry> pb = u.parsePhonebook("./src/_res/telefonbuch.txt");

		//host = InetAddress.getLocalHost().getHostName();
		//port = Integer.parseInt(98 + host.replaceAll("[^\\d]+", ""));
		host = "localhost";
		port = 8888;
		System.out.println(host + ":" + port + "\n");
		ss = new ServerSocket(port);

		while(true) {
			list = new SynchronizedList<PhonebookEntry>();
			cs = ss.accept();
			bR = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			out = new PrintWriter(cs.getOutputStream());

			String info = bR.readLine();
			if(info.startsWith("GET /favicon")) {
				System.out.println("Blockiert: Favicon-Request");
				continue;
			} else if(info.startsWith("GET / HTTP/1.1")) {
				System.out.println("Ursprüngliche Index-Page: " + info);
				printWelcomeHTML();
			} else if(info.startsWith("GET /?Z")) {
				printWelcomeHTML();
			} else if(info.startsWith("GET /?")) {
				System.out.println("Received Search String: " + info);

				String name = u.hardcodedReplace(info, 0);
				name = URLDecoder.decode(name, "UTF-8");
				String number = u.hardcodedReplace(info, 1);
				number = URLDecoder.decode(number, "UTF-8");
				String action = u.hardcodedReplace(info, 2);

				System.err.print("Isolated: ");
				System.out.println(name + " " + number + " " + action);
				if(action.equals("StarteSuche")) {
					if((name.equals("") && number.equals("")) || (name.matches("^\\s+") && number.matches("^\\s+"))) {
						printErrorHTML(2, "");
					} else if(u.findNameAndNumber(name + " " + number)) {
						ThreadedSearch t1 = new ThreadedSearch(name, pb);
						ThreadedSearch t2 = new ThreadedSearch(Integer.parseInt(number), pb);
						t1.start();
						t2.start();
						t1.join();
						t2.join();
						if(!t1.f && !t2.f) {
							printErrorHTML(0, name + " " + number);
						} else if(!t1.f) {
							printSpecialCaseHTML(name);
						} else if(!t2.f) {
							printSpecialCaseHTML(number);
						} else {
							printResultsHTML();
						}
					} else if(u.findName(name)) {
						ThreadedSearch t = new ThreadedSearch(name, pb);
						t.start();
						t.join();
						if(!t.f) {
							printErrorHTML(0, name);
						} else {
							printResultsHTML();
						}
					} else if(u.findNumber(number)) {
						ThreadedSearch t = new ThreadedSearch(Integer.parseInt(number), pb);
						t.start();
						t.join();
						if(!t.f) {
							printErrorHTML(0, number);
						} else {
							printResultsHTML();
						}
					} else if(u.findNameAndNumber(number + " " + name)) {
						printErrorHTML(0, name + " " + number);
					} else if(u.findNumber(name)) {
						printErrorHTML(0, name);
					} else if(u.findName(number)) {
						printErrorHTML(0, number);
					} else {
						printErrorHTML(1, "");
					}
				} else if(action.equals("BeendeServer")) {
					printExitHTML();
					System.out.println("Der Server wurde beendet. Tschüss!");
					emptyCache();
					ss.close();
					System.exit(0);
				}
			}
			emptyCache();
		}
	}

	final static void emptyCache() throws IOException {
		out.flush();
		out.close();
		bR.close();
		cs.close();
	}

	final static void printHeaderHTML() {
		out.println("HTTP/1.1 200 OK Content-Type: text/html");
		out.println();
		out.println(""
				+ "<html>"
				+ "<head>"
				+ "<meta charset=\"utf-8\">"
				+ "</head>"
				+ "<body>"
				);
	}

	final static void printFooterHTML() {
		out.println(""
				+ "</body>"
				+ "</html>"
				);
	}

	final static void printWelcomeHTML() {
		printHeaderHTML();
		out.println(""
				+ "<h2 align=left>Das Online-Telefonbuch-Verzeichnis</h2>"
				+ "<h3>Suche nach einem <i>Namen</i>, einer <i>Nummer</i> oder beidem (nebenläufig).</h3>"
				+ "<form method=get action='http://" + host + ":" + port + "'>"
				+ "<table>"
				+ "<tr><td valign=top>Name:</td><td><input name=A></td><td></td></tr>"
				+ "<tr><td valign=top>Nummer:</td> <td><input name=B></td><td></td></tr>"
				+ "<tr><td valign=top><input type=submit name=C value=\"Starte Suche\"></td>"
				+ "<td><input type=reset value=\"Löschen\"></td>"
				+ "<td><input type=submit name=D value=\"Beende Server\"></td></tr>"
				+ "</table>"
				+ "</form>"
				);
		printFooterHTML();
	}

	final static void printExitHTML() {
		printHeaderHTML();
		out.println(""
				+ "<h2 align=center>Der Server wurde beendet.</h2>"
				);
		printFooterHTML();
	}

	final static void printErrorHTML(int code, String err) {
		printHeaderHTML();
		String s = "";
		switch(code) {
		case 0:
			s = "Die Suche nach <i>" + err + "</i> war erfolglos.";
			break;
		case 1:
			s = "Die Suche war erfolglos. Keine gültige Eingabe.";
			break;
		case 2:
			s = "Keine leeren Such-Strings verschicken!";
			break;
		}
		out.println(""
				+ "<h2 align=left>Fehler: " + s + "</h2>"
				+ "<form method=get action='http://" + host + ":" + port + "'>"
				+ "<table>"
				+ "<tr><td valign=top><input type=submit name=Z value=\"Zurück\"></td>"
				+ "</table>"
				+ "</form>"
				);
		printFooterHTML();
	}

	public final static void printResultsHTML() {
		printHeaderHTML();
		out.println(""
				+ "<h2 align=left>Ihre Suchergebnisse:</h2>"
				+ "<ul>"
				);
		out.println();
		if(!list.isEmpty()) {
			for(PhonebookEntry e : list) {
				out.println("<li>" + e.getName() + " - " + e.getNumber() + "</li>");
			}
		}
		out.println(""
				+ "</ul>"
				+ "<form method=get action='http://" + host + ":" + port + "'>"
				+ "<table>"
				+ "<tr><td valign=top><input type=submit name=Z value=\"Zurück\"></td>"
				+ "</table>"
				+ "</form>"
				);
		printFooterHTML();
	}

	final static void printSpecialCaseHTML(String err) {
		printHeaderHTML();
		out.println(""
				+ "<h2 align=left>Ihre Suchergebnisse:</h2>"
				+ "<ul>"
				);
		out.println();
		if(!list.isEmpty()) {
			for(PhonebookEntry e : list) {
				out.println("<li>" + e.getName() + " - " + e.getNumber() + "</li>");
			}
		}
		out.println(""
				+ "</ul>"
				+ "<h2 align=left>Fehler: Die Suche nach <i>\"" + err + "\"</i> war allerdings erfolglos.</h2>"
				+ "<form method=get action='http://" + host + ":" + port + "'>"
				+ "<table>"
				+ "<tr><td valign=top><input type=submit name=Z value=\"Zurück\"></td>"
				+ "</table>"
				+ "</form>"
				);
		printFooterHTML();
	}
}