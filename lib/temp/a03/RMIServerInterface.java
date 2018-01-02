package tb.temp.a03;

import java.rmi.*;
import java.util.ArrayList;

/**
 * Created by Sprotte on 19.01.15.
 */
public interface RMIServerInterface extends Remote{
    public ArrayList<String> search(String[] query) throws Exception;

    public void quit() throws RemoteException;
}
