package fin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import java.util.regex.Pattern;

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

		host = InetAddress.getLocalHost().getHostName();
		port = Integer.parseInt(98 + host.replaceAll("[^\\d]+", ""));
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
				System.err.print("Blockiert: ");
				System.out.println("Favicon-Request");
				continue;
			} else if(info.startsWith("GET / HTTP/1.1")) {
				System.err.print("Ursprüngliche Index-Page: ");
				System.out.println(info);
				printWelcomeHTML();
			} else if(info.startsWith("GET /?Z")) {
				printWelcomeHTML();
			} else if(info.startsWith("GET /?")) {
				System.err.print("Received Search String: ");
				System.out.println(info);

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
					System.exit(0);
					ss.close();
				}
			}
			System.out.println();
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

class PhonebookEntry {
	final String name;
	final int number;

	public PhonebookEntry(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() { return name; }

	public int getNumber() { return number; }
}

class Reader {
	protected String path, fileCache;
	private String[] sArr;
	private int count;
	protected ArrayList<String> list, listCache;
	private Scanner in;

	public Reader() {
		count = 0;
	}

	public int[] integerArray(String selectedFile) {
		try{
			File file = new File(selectedFile);
			Scanner input = new Scanner (file);
			ArrayList<String> list = new ArrayList<String>();

			while(input.hasNextInt()) {
				String temp = input.next();
				if(temp.equals("")){
					continue;
				}
				list.add(temp);
				count++;
			}
			String[] superString = new String[list.size()];

			int temp;
			int[] array = new int[count];

			for(int i = 0; i < list.size(); i++){
				superString[i] = list.get(i);
				temp = Integer.parseInt(superString[i]);
				array[i] = temp;
			}

			input.close();
			return array;
		} catch (FileNotFoundException exc) {
			System.out.println("File cannot be found");
		}
		return null;
	}

	public String[] stringArray(String path) {
		try{
			File file = new File(path);
			in = new Scanner(file);
			list = new ArrayList<String>();
			while(in.hasNext()) {
				String temp = in.nextLine();
				if(temp.equals("")){
					continue;
				}
				list.add(temp);
			}
			sArr = new String[list.size()];
			for(int i = 0; i < list.size(); i++){
				sArr[i] = list.get(i);
			}
			in.close();
			return sArr;
		} catch (FileNotFoundException exc) {
			System.out.println("Error 404: File not found.");
		}
		return null;
	}

	public void emptyCache() {
		listCache = null;
		fileCache = null;
	}

	public int getLength() { return list.size(); }

	public ArrayList<String> getList() { return list; }

	public String getFile() { return path; }
}

class StreamBuffer {
	private static OutputStream lastStream = null;
	private static boolean isFixed = false;

	private static class FixedStream extends OutputStream {
		private final OutputStream target;

		public FixedStream(OutputStream originalStream) {
			target = originalStream;
		}

		@Override
		public void write(int b) throws IOException {
			if(lastStream != this) {
				swap();
			}
			target.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			if(lastStream != this) {
				swap();
			}
			target.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if(lastStream != this) {
				swap();
			}
			target.write(b, off, len);
		}

		private void swap() throws IOException {
			if(lastStream != null) {
				lastStream.flush();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lastStream = this;
		}

		public void close() throws IOException { target.close(); }

		public void flush() throws IOException { target.flush(); }
	}

	public static void fixConsole() {
		if(isFixed) {
			return;
		}
		isFixed = true;
		System.setErr(new PrintStream(new FixedStream(System.err)));
		System.setOut(new PrintStream(new FixedStream(System.out)));
	}
}

class SynchronizedList<T> implements List<T> {
	private ArrayList<T> arr;

	public SynchronizedList() {
		arr = new ArrayList<T>();
	}

	public boolean add(T e) {
		synchronized(this.getClass()) {
			arr.add(e);
			return true;
		}
	}

	public void add(int i, T e) {
		synchronized(this.getClass()) {
			arr.add(i, e);
		}
	}

	public T get(int index) {
		synchronized(this.getClass()) {
			return arr.get(index);
		}
	}

	public int size() {
		synchronized(this.getClass()) {
			return arr.size();
		}
	}

	public boolean isEmpty() {
		synchronized(this.getClass()) {
			for(T t : arr) {
				if(t!=null) {
					return false;
				}
			}
			return true;
		}
	}

	public Iterator<T> iterator() {
		Iterator<T> it = new Iterator<T>() {
			private int i = 0;

			public boolean hasNext() {
				synchronized(this.getClass()) {
					return i < arr.size() && arr.get(i) != null;
				}
			}

			public T next() {
				synchronized(this.getClass()) {
					return arr.get(i++);
				}
			}

			public void remove() {
				synchronized(this.getClass()) {
					throw new UnsupportedOperationException();
				}
			}
		};
		return it;
	}

	/*	PLEASE	IGNORE	*/

	/*
	 * (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */

	public void clear() {
	}

	public boolean contains(Object o) {
		return false;
	}

	public Object[] toArray() {
		return null;
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		return null;
	}

	public boolean remove(Object o) {
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		return false;
	}

	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		return false;
	}

	public T set(int index, T element) {
		return null;
	}

	public T remove(int index) {
		return null;
	}

	public int indexOf(Object o) {
		return 0;
	}

	public int lastIndexOf(Object o) {
		return 0;
	}

	public ListIterator<T> listIterator() {
		return null;
	}

	public ListIterator<T> listIterator(int index) {
		return null;
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return null;
	}
}

class ThreadedSearch extends Thread {
	private String s;
	private int i;
	public boolean str, f;
	final ArrayList<PhonebookEntry> pb;

	public ThreadedSearch(Object o, ArrayList<PhonebookEntry> pb) throws Exception {
		this.pb = pb;
		if(o.getClass() == String.class) {
			s = o.toString();
			str = true;
		} else if(o.getClass() == Integer.class) {
			i = Integer.parseInt(o.toString());
			str = false;
		} else {
			throw new Exception("Error: Invalid input-type.");
		}
	}

	public void run() {
		if(str) {
			for(PhonebookEntry e : pb) {
				if(e.getName().equals(s)) {
					Server.list.add(e);
					f = true;
				}
			}
			if(f) {
				return;
			} else {
				f = false;
			}
		} else {
			for(PhonebookEntry e : pb) {
				if(e.getNumber() == i) {
					Server.list.add(e);
					f = true;
				}
			}
			if(f) {
				return;
			} else {
				f = false;
			}
		}
	}
}

class Utility {
	private String host;
	private int port;
	final Scanner in;
	private PrintWriter out;

	public Utility() {
		in = new Scanner(System.in);
	}

	public Utility(PrintWriter out, String host, int port) {
		in = new Scanner(System.in);
		this.out = out;
		this.host = host;
		this.port = port;
	}

	/**
	 * A simple method to retrieve the user-input and print the writing-mark
	 * for the user.
	 *
	 * @return The parsed input-String written by the user
	 */
	public final String getUserInput() { return in.nextLine(); }

	/**
	 * A rather simple method reading a text file and parsing a list of names
	 * and phone-numbers.
	 *
	 * @return A PhonebookEntry-array containing names and corresponding numbers
	 */
	public final ArrayList<PhonebookEntry> parsePhonebook(String path) {
		String[] tb = new Reader().stringArray(path);
		ArrayList<PhonebookEntry> pb = new ArrayList<PhonebookEntry>();
		for(int i = 0; i < tb.length; i++) {
			String[] split = tb[i].split("(?<=\\D)(?=\\d)");
			String str = split[0];
			StringBuilder sb = new StringBuilder(str);
			str = sb.substring(0, str.length()-1);
			pb.add(i, new PhonebookEntry(str, Integer.parseInt(split[1])));
		}
		return pb;
	}

	/**
	 * @param pb PhonebookEntry-array containing names and corresponding numbers
	 * @param prntNam Specifies if names should be printed
	 * @param prntNum Specifies if numbers should be printed
	 */
	public void printPhonebook(SynchronizedList<PhonebookEntry> pb, boolean prntNam, boolean prntNum) {
		PrintWriter out = new PrintWriter(System.out, true);
		out.println("\"Telefonbuch\"-Liste:\n" + "--------------------");
		for(PhonebookEntry s : pb) {
			if(prntNam && !prntNum) {
				out.printf("%s%n", s.getName());
			} else if(prntNum && !prntNam) {
				out.printf("%d%n", s.getNumber());
			} else if(prntNam && prntNum) {
				out.printf("%s: %d%n", s.getName(), s.getNumber());
			} else {
				out.println("¯\\_(ツ)_/¯");
			}
		}
		out.println();
	}

	public void printLine(String s) {
		for(int i = 0; i < s.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
	}

	public boolean findExit(String input) { return findPattern(input, "^[Qq]+?uit|^[Ee]+?xit"); }

	public boolean findNameAndNumber(String input) { return findPattern(input, "\\S+\\s+\\d+"); }

	public boolean findName(String input) { return findPattern(input, "^\\S+[^0-9]+"); }

	public boolean findNumber(String input) { return findPattern(input, "^[0-9]+\\S??"); }

	/**
	 * @param input The user-input String
	 * @param regex The given regular expression
	 * @return A boolean giving information if the pattern was found or not
	 */
	final boolean findPattern(String input, String regex) { return Pattern.compile(regex).matcher(input).find(); }

	public final String hardcodedReplace(String s, int i) {
		switch(i) {
		case 0:
			return s.replaceAll("GET /\\?A=", "").replaceAll("&B.*", "").replaceAll("\\+", " ");
		case 1:
			return s.replaceAll("GET /\\?A=.*&B=", "").replaceAll("&[CD].*", "");
		case 2:
			return s.replaceAll("GET /\\?A=.+&[CD]+?=", "").replaceAll("\\+", "").replaceAll(" HTTP/1.1", "");
		default:
			return "";
		}
	}
}
