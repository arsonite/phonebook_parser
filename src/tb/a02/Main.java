package tb.a02;

import java.util.ArrayList;

/**
 * This is the {@code Main}-class responsible for the search-threads. 
 * 
 * @since JDK 1.91 ~ <i>2018</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class Main {
	public static void main(String[] args) throws Exception {
		Time t = new Time();
		StreamBuffer.fixConsole();
		Utility u = new Utility();
		ArrayList<PhonebookEntry> pb = u.parsePhonebook("./src/_res/telefonbuch.txt");
		System.out.print("Please enter either a name, phone-number or both.\n> ");
		while(true) {
			try {
				String input = u.getUserInput();
				if(input.isEmpty() || input.matches("^\\s+")) {
					System.err.println("\nError: No whitespace-only characters!");
					continue;
				} else if(u.findExit(input)) {
					System.err.println("\nProcess was terminated.");
					break;
				} else if(u.findNameAndNumber(input)) {
					ThreadedSearch name = new ThreadedSearch(input.replaceAll("\\s\\d+", ""), pb);
					ThreadedSearch numb = new ThreadedSearch(Integer.parseInt(input.replaceAll("\\S+\\s+", "")), pb);
					name.start();
					numb.start();
					name.join();
					numb.join();
				} else if(u.findName(input)) {
					ThreadedSearch name = new ThreadedSearch(input.replaceAll("\\s\\d+", ""), pb);
					name.start();
				} else if(u.findNumber(input)) {
					ThreadedSearch numb = new ThreadedSearch(Integer.parseInt(input.replaceAll("\\S+\\s+", "")), pb);
					numb.start();
				} else {
					System.err.println("\nError: \"" + input + "\" is not a valid input.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println();
		} 
	}

	public static void printAllResults(SynchronizedList<PhonebookEntry> list) {
		if(!list.isEmpty()) {
			for(PhonebookEntry e : list) {
				System.out.println(e.getName() + " " + e.getNumber());
			}
		}
	}
}