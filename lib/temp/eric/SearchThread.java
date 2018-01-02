package tb.temp.eric;

import java.util.Hashtable;

public class SearchThread extends Thread {
	private int tel;
	private String name;
	private boolean isString;
	private Hashtable<Integer, String> ht;
	// Übergabe Strings
	private final String rohStringName = "Name=";
	private final String rohStringTel = "&Nummer=";
	// Telefonbuch
	private String[] nA = {"Meier", "Meier", "Franz", "Franz Meier", "Ülm"};
	private int[] tA = {1, 2, 1, 2, 3};

	/**
	 * Konstrucktor für Suche nach einer Nummer.
	 * @param tel Integer Wert nach dem im int Array \"tA\" gesucht wird 
	 */
	public SearchThread(int tel) {
		super();
		this.tel=tel;
		isString = false;
		ht = new Hashtable<>(tA.length);
	}

	/**
	 * Konstrucktor für die Suche nach einem Namen.
	 * @param name String Wert nach dem im String Array \"nA\" gesucht wird.
	 */
	public SearchThread(String name) {
		super();
		this.name = name;
		isString = true;
		ht = new Hashtable<>(nA.length);
	}

	/**
	 * Je nachdem ob ein Integer oder String übergeben worden ist wird
	 * eine suche gestartet. 
	 */
	public void run() {
		if(isString) {
			search(name);
		}
		if(!isString){
			search(tel);
		}
	}

	/**
	 * Methode zur Suche nach einem Namen im String Array \"nA\".
	 * Treffer werden in eine Hashtable eingefügt, diese wird dann der
	 * run.print Methode übergeben.
	 * @param String name nach dem im String Array \"nA\" gesucht wird.
	 */

	private void search(String name) {
		boolean found = false;
		int i2 = 0;
		for(int i = 0; i < nA.length; i++) {
			if(name.equals(nA[i])) {
				i2++;
				ht.put(i2, rohStringName + nA[i] + rohStringTel + tA[i]);
				found = true;
			}
		}
		if(!found) {
			System.err.println("Die Suche nach " + name + " war erfolglos");
		}
		run.print(ht);
	}

	/**
	 * Methode zur Suche nach einer Nummer im int Array \"tA\".
	 * Treffer werden in eine Hashtable eingefügt, diese wird dann der
	 * run.print Methode übergeben.
	 * @param tel Nummer nach dem im int Array \"tA\" gesucht wird.
	 */
	private void search(int tel) {
		boolean found = false;
		int i2 = 0;
		for(int i = 0; i < tA.length; i++) {
			if(tel == tA[i]) {
				i2++;
				ht.put(i2, rohStringName + nA[i] + rohStringTel + tA[i]);
				found = true;
			}
		}
		if(!found) {
			System.err.println("Die Suche nach " + tel + " war erfolglos");
			return;
		}
		run.print(ht);
	}
}