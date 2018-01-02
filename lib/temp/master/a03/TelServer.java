package tb.temp.master.a03;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tronico
 */
import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

public class TelServer extends UnicastRemoteObject implements TelInterface{

  public TelServer() throws RemoteException {}

  @Override
  public String[][] oneValueSearch(String searchName) throws RemoteException {
      System.out.println("RMI hat geantwortet");
    return Abfrage.oneValueSearch(searchName);
  }
  
  @Override
  public String[][] twoValueSearch(String searchName, String searchNumber) throws RemoteException {
      System.out.println("RMI hat geantwortet");
    return Abfrage.twoValueSearch(searchName, searchNumber);
  }
  
  @Override
    public void rmiBeenden() throws RemoteException {
        System.out.println("RMI wurde beendet");
        System.exit(1);
    }

  public static void main(String[] args) throws Exception {
    LocateRegistry.createRegistry(1099);     // Port übergabe durch Parameter beim start
    TelServer telServer = new TelServer();

    // Anmeldung des Dienstes mit
    // rmi://Serverhostname/Eindeutige Bezeichnung des Dienstes
    // ---------------------------------------------------------
    String host = InetAddress.getLocalHost().getHostName();
    
    Naming.rebind("rmi://"+host+"/TelService", telServer);
    System.out.println("Server wartet auf RMIs");
  }

}

