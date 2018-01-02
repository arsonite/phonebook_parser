package tb.temp.master.a02;

/**
 * Created by Tronico on 13.10.2015.
 * SearchThread
 * Klasse um ein 2 dimensionales String-Array zu durchsuchen
 * erbt von Thread
 */
public class SearchThread extends Thread{

    final String searchValue;
    final String[][] telTab;
    final String[][] res;

    public SearchThread(String searchValue, String[][] telTab, String[][] res){
        super();
        this.searchValue = searchValue;
        this.telTab = telTab;
        this. res = res;
    }

    @Override
    public void run(){
        int count = 0;

        for (String[] telTab1 : telTab) {
            for (int i = 0; i < 2; i++) {
                if (telTab1[i].equals(searchValue)) {
                    res[count][0] = telTab1[0];
                    res[count][1] = telTab1[1];
                    count++;
                }
            }
        }
    }
}
