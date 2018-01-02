package tb.a02;

/**
 * The class {@code PhonebookEntry} is a simple dual-primitive class,
 * consisting of a string and integer depicting the selectable name
 * and phone-number of a contact.
 * The variables are final, because there's no need in the task
 * for you to be able to modify the content of the class.
 * 
 * @since JDK 1.91 ~ <i>2018</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class PhonebookEntry {
	final String name;
	final int number;

	public PhonebookEntry(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() { return name; }
	
	public int getNumber() { return number; }
}