/**
 * The class {@code Utility} is a utility class consisting of mutliple different
 * methods, that don't interact much with each other, but are just a collection of 
 * necessary methods for other classes.
 * 
 * <p>{@code 06.11.2017}
 * @author Burak Günaydin ({@code 853872})
 */

package tb.a01;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import teaType.util.io.Reader;

public class Utility {
	private final Scanner in;

	public Utility() {
		in = new Scanner(System.in);
	}

	/**
	 * A simple method to retrieve the user-input and print the writing-mark
	 * for the user.
	 * 
	 * @return The parsed input-String written by the user
	 */
	public final String getUserInput() {
		String input = in.nextLine();
		//input = input.replaceAll("\\n", "");
		return input;
	}

	/**
	 * A rather simple method reading a text file and parsing a list of names
	 * and phone-numbers.
	 * 
	 * @return A PhonebookEntry-array containing names and corresponding numbers
	 */
	public final ArrayList<PhonebookEntry> parsePhonebook() {
		String[] tb = new Reader().fileToString("./src/_res/telefonbuch.txt");
		ArrayList<PhonebookEntry> pb = new ArrayList<PhonebookEntry>();
		StringBuilder sb;
		String[] split;
		String str;
		int i;
		int count = 0;
		for(String s : tb) {
			split = s.split("(?<=\\D)(?=\\d)");
			str = split[0];
			sb = new StringBuilder(str);
			str = sb.substring(0, str.length()-1);
			i = Integer.parseInt(split[1]);
			pb.add(count, new PhonebookEntry(str, i));
			count++;
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

	public boolean findExit(String input) {
		return findPattern(input, "^[Qq]+?uit|^[Ee]+?xit");
	}
	
	public boolean findNameAndNumber(String input) {
		return findPattern(input, "\\S+\\s+\\d+");
	}

	public boolean findName(String input) {
		return findPattern(input, "^\\S+[^0-9]+");
	}

	public boolean findNumber(String input) {
		return findPattern(input, "^[0-9]+\\S??");
	}
	
	/**
	 * @param input The user-input String
	 * @param regex The given regular expression
	 * @return A boolean giving information if the pattern was found or not
	 */
	private final boolean findPattern(String input, String regex) {
		if(Pattern.compile(regex).matcher(input).find()) {
			return true;
		}
		return false;
	}
}