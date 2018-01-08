package tb.a02;

import java.util.ArrayList;

/**
 * The class {@code ThreadedSearch} is a subclass of {@code Thread} 
 * confirms if the given object is either a String or Integer and then
 * proceeds to search the database for either.
 * 
 * @since JDK 1.91 ~ <i>2017</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class ThreadedSearch extends Thread {
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
				// TODO: Temporary, to be suplemented by Token
				System.err.println("The search for the name '" + s + "' failed.\n");
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
				// TODO: Temporary, to be suplemented by Token
				System.err.println("The search for the number '" + s + "' failed.\n");
			}
		}
	}
}