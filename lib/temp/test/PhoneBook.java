package tb.temp.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PhoneBook {
	private static PhoneBook phoneBook = new PhoneBook();
	private static ArrayList<String> nameOutput = new ArrayList<String>();
	private static ArrayList<String> numberOutput = new ArrayList<String>();

	public static String search(String name, String number) throws Exception {

		//input number and name, two threads starting
		if(!name.isEmpty() && !number.isEmpty()){
			Thread thread1 = phoneBook.new searchNameThread(name);
			Thread thread2 = phoneBook.new searchNumberThread(number.trim());
			thread1.start();
			thread2.start();
			thread1.join();
			thread2.join();
		}else{
			//just number, one thread starting
			if(!number.isEmpty()){
				Thread thread = phoneBook.new searchNumberThread(number.trim());
				thread.start();
				thread.join();
			}
			//just name, one thread starting
			if(!name.isEmpty()){
				Thread thread2 = phoneBook.new searchNameThread(name.trim());
				thread2.start();
				thread2.join();
			}
		}
		return printMessage(name, number);		
	}

	//private Thread Class for name search
	private class searchNameThread extends Thread {
		String name; 

		//constructor
		searchNameThread(String name) {
			this.name = name;
		}

		//thread run methode
		public void run() {		    	

			try (BufferedReader in = new BufferedReader(new FileReader("telefonbuch.txt"))) {
				//reads Textfile and searches for name

				String zeile = null;
				while ((zeile = in.readLine()) != null) { 
					if(zeile.toLowerCase().contains(name.toLowerCase())){
						System.out.println("Name-Thread active");
						nameOutput.add(zeile);
					}	    				
				}
			} catch (IOException e) {
				e.printStackTrace();
			}		    	
		}
	}

	//private Thread Class for number search
	private class searchNumberThread extends Thread {
		String number; 

		//constructor
		searchNumberThread(String number) {
			this.number = number;
		}

		//thread run methode
		public void run() {		    	

			try (BufferedReader in = new BufferedReader(new FileReader("telefonbuch.txt"))) {
				//reads Textfile and searches for number

				String zeile = null;

				while ((zeile = in.readLine()) != null) {
					if(zeile.contains(number)){
						System.out.println("Number-Thread active");
						numberOutput.add(zeile);	    					
					}
				}	  			

			} catch (IOException e) {
				e.printStackTrace();
			}		    	
		}
	}


	//print search results
	private static String printMessage(String name, String number){
		StringBuilder message = new StringBuilder();

		if(!nameOutput.isEmpty()){
			message.append("<br>Namen-Suche nach <b>"+name.trim()+"</b> ergab: <br>");
			for (String e : nameOutput) {
				message.append(e);
				message.append("<br>");
			}
			nameOutput.clear();
		}
		else{
			if(!name.isEmpty())
				message.append("<br>Namen-Suche nach <b>"+name+"</b> war erfolglos! <br>");
		}



		if(!numberOutput.isEmpty()){
			message.append("<br>Nummern-Suche nach <b>"+number+"</b> ergab: <br>");
			for (String e : numberOutput) {
				message.append(e);
				message.append("<br>");
			}		
			numberOutput.clear();
		}
		else{
			if(!number.isEmpty())
				message.append("<br>Nummern-Suche nach <b>"+number+"</b> war erfolglos!<br><br>");
		}		


		return message.toString();
	}

}
