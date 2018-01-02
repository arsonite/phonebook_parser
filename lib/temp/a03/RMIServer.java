package tb.temp.a03;

import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.RemoteException;
/**
 * Created by Sprotte on 19.01.15.
 */
public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    private static final long serialVersionUID = 1L;
    public  RMIServer() throws  RemoteException{}

    public ArrayList<String> search(String[] query) throws Exception {
        System.err.println("RMI suche läuft...");
        Telefonbuch telefonbuch = new Telefonbuch();
        telefonbuch.Suchen(query);        
        ArrayList<String> results = new ArrayList<String>();
        results.addAll(telefonbuch.ausgabeNummern);
        results.addAll(telefonbuch.ausgabeName);
        return results;
    }

    public static void main (String[] argv) {

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("RMI : Registry wurde erzeugt.");

            Naming.rebind("RMIServer",new RMIServer());
            System.out.println("RMI : Abteilungsserver registriert");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void quit() throws RemoteException {
        System.out.println("RMI Server herrunter gefahren!");
        Registry registry = LocateRegistry.getRegistry();
        try {
            registry.unbind("RMIServer");
        } catch (Exception e) {
            throw new RemoteException("Could not unregister service, quiting anyway", e);
        }
    }
}
