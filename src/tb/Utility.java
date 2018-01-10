package tb;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * The class {@code Utility} is a utilitarian class consisting of multiple different
 * methods, that don't interact much with each other, but are just a collection of 
 * necessary methods for other classes.
 * 
 * @since JDK 1.91 ~ <i>2018</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class Utility {
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