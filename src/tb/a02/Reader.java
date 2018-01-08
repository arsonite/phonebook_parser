package tb.a02;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Scanner;

public class Reader {
	protected String path, fileCache;
	private String[] sArr;
	private int count;
	protected ArrayList<String> list, listCache;
	private Scanner in;

	public Reader() {
		count = 0;
	}

	public int[] integerArray(String selectedFile) {
		try{
			File file = new File(selectedFile);
			Scanner input = new Scanner (file);
			ArrayList<String> list = new ArrayList<String>();

			while(input.hasNextInt()) {
				String temp = input.next();
				if(temp.equals("")){
					continue;
				}
				list.add(temp);
				count++;
			}
			String[] superString = new String[list.size()];

			int temp;
			int[] array = new int[count];

			for(int i = 0; i < list.size(); i++){
				superString[i] = list.get(i);
				temp = Integer.parseInt(superString[i]);
				array[i] = temp;
			}

			input.close();
			return array;
		} catch (FileNotFoundException exc) {
			System.out.println("File cannot be found");
		}
		return null;	
	}

	public String[] stringArray(String path) {
		try{
			File file = new File(path);
			in = new Scanner(file);
			list = new ArrayList<String>();
			while(in.hasNext()) {
				String temp = in.nextLine();
				if(temp.equals("")){
					continue;
				}
				list.add(temp);
			}
			sArr = new String[list.size()];
			for(int i = 0; i < list.size(); i++){
				sArr[i] = list.get(i);
			}
			in.close();
			return sArr;
		} catch (FileNotFoundException exc) {
			System.out.println("Error 404: File not found.");
		}
		return null;
	}
	
	public void emptyCache() {
		listCache = null;
		fileCache = null;
	}

	public int getLength() { return list.size(); }

	public ArrayList<String> getList() { return list; }

	public String getFile() { return path; }
}