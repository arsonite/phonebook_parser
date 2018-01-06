/**
 * The class {@code ThreadedSearch} is a subclass of {@code Thread} 
 * confirms if the given object is either a String or Integer and then
 * proceeds to search the database for either.
 * 
 * <p>{@code 06.11.2017}
 * @author Burak Günaydin ({@code 853872})
 */

package tb.a01;

import java.util.ArrayList;

public class ThreadedSearch extends Thread {
	private ArrayList<PhonebookEntry> pb;
	public SynchronizedList<PhonebookEntry> list;
	private String s;
	private int i;
	private boolean str, f;

	public ThreadedSearch(Object o, ArrayList<PhonebookEntry> pb) throws Exception {
		this.pb = pb;
		list = new SynchronizedList<PhonebookEntry>();
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
					list.add(e);
					f = true;
				}
			}
			if(!f) {
				System.err.println("The search for the name '" + s + "' failed.\n");
				return;
			}
		} else {
			for(PhonebookEntry e : pb) {
				if(e.getNumber() == i) {
					list.add(e);
					f = true;
				}
			}
			if(!f) {
				System.err.println("The search for the number '" + i + "' failed.\n");
				return;
			}
		}
		Main.printAllResults(list);
		f = false;
	}
}