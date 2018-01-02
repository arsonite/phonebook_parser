package tb.temp.master.a01;

/**
 * Created by Tronico on 13.10.2015.
 * SearchThread
 * Klasse um ein 2 dimensionales String-Array zu durchsuchen
 * erbt von Thread
 */
public class SearchThread extends Thread{

    private String searchValue;
    private String[][] telTab;
    private String[][] ergTab;

    public SearchThread(String searchValue, String[][] telTab, String[][] ergTab){
        super();
        this.searchValue = searchValue;
        this.telTab = telTab;
        this. ergTab = ergTab;
    }

    @Override
    public void run(){
        int zaehler = 0;

        for(int i = 0; i < telTab.length; i++){
            for(int j = 0; j < 2; j++){
                if(telTab[i][j].equals(searchValue)){
                    ergTab[zaehler][0] = telTab[i][0];
                    ergTab[zaehler][1] = telTab[i][1];
                    zaehler++;
                }
            }
        }
    }
}
