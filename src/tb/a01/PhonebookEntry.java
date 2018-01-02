/**
 * The class {@code PhonebookEntry} is a simple dual-primitive class,
 * consisting of a string and integer depicting the selectable name
 * and phone-number of a contact.
 * The variables are final, because there's no need in the task
 * for you to be able to modify the content of the class.
 * 
 * <p>{@code 06.11.2017}
 * @author Burak Günaydin (853872)
 */

package tb.a01;

public class PhonebookEntry {
	private final String name;
	private final int number;

	public PhonebookEntry(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return name;
	}
	
	public int getNumber() {
		return number;
	}
}