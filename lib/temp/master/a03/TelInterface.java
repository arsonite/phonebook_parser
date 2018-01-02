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
import java.rmi.*;
public interface TelInterface extends Remote {
  String[][] oneValueSearch(String searchName) throws RemoteException;
  String[][] twoValueSearch(String searchName, String searchNumber) throws RemoteException;
  void rmiBeenden() throws RemoteException;
}