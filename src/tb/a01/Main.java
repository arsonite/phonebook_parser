/**
 * This is the {@code Main}-class where the starting and joining of threads are arranged.
 * 
 * <p>{@code 06.11.2017}
 * @author Burak Günaydin ({@code 853872})
 */

package tb.a01;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws Exception {
		Utility u = new Utility();
		ArrayList<PhonebookEntry> pb = u.parsePhonebook();
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