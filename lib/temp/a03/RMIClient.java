package tb.temp.a03;
/*
 * Telefonbuch Server  *
 * Paul Sprotte *
 * 08.12.2014 *
 */


import java.io.*;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.RMISecurityManager;

public class RMIClient extends Thread implements Remote {
    private static  String rmiIp;
    private RMIServerInterface rmiServer;
    //Html Inhalt der Hauptseite
    static final String HTML_START = "<html>"+ "<title>HTTP POST Server in java</title>" + "<body>";
    static final String HTML_END = "</body>" + "</html>";
    private String responseStringIndex =  RMIClient.HTML_START
            + "<h2 align=center>Telefonverzeichnis</h2>\n"
            + "<h3>Sie k\u00f6nnen nach Name oder nach Telefonnummer oder nach beiden (nebenl\u00e4ufig) suchen.</h3>\n"
            + "<form action=\"./\" "
            + "method=\"get\" accept-charset=\"UTF-8\">\n"
            + "<table>\n"
            + "<tr> <td valign=top>Name:</td> <td><input name=A></td> <td></td> </tr>\n"
            + "<tr> <td valign=top>Nummer:</td> <td><input name=B></td> <td></td> </tr>\n"
            + "<tr> <td valign=top><input type=submit value=Suchen></td>\n"
            + " <td><input type=reset></td></form>\n"
            + " <tr><td><form action=\"./\" "
            + "method=\"get\" accept-charset=\"UTF-8\">"
            + "<input type=submit value=\"Server beenden\" name=X></td> </tr>\n"
            + "</table>\n" + "</form>\n";

    Socket connectedClient = null;
    ServerSocket server = null;
    BufferedReader inFromClient = null;
    DataOutputStream outToClient = null;

    public RMIClient(Socket client,ServerSocket server) {
        connectedClient = client;
        this.server = server;



        try {
            Registry registry = LocateRegistry.getRegistry(rmiIp,Registry.REGISTRY_PORT);
            rmiServer = (RMIServerInterface) registry.lookup("RMIServer");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


    }

    public void run() {

        String currentLine = null;

        try {
            //Schaft einen  BufferReader zum lesen des Client Inputs
            System.out.println("The Client " + connectedClient.getInetAddress()+":" + connectedClient.getPort() + " is connected");
            inFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            //Schaft einen  DataOutputStream zum schreiben zum Client
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            //Liet die gesendeten Daten des Clients ein
            currentLine = inFromClient.readLine();
            String headerLine = currentLine;
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String httpQueryString = tokenizer.nextToken();
            if(!httpQueryString.equals("/favicon.ico"))
                System.out.println("Server Handels"+ httpMethod +"Request of Client: "+ connectedClient.getInetAddress());
            //wird das get forumlar angesendet
            if (httpMethod.equals("GET")) {
                //f�r den Fall  des ersten Aufruf ohne Parameter
                if (httpQueryString.equals("/")||httpQueryString.equals("/?")||httpQueryString.equals("/favicon.ico")) {
                    // Zeigt die normale Suche
                    String responseString = responseStringIndex;
                    responseString += RMIClient.HTML_END;
                    sendResponse(200, responseString, false);
                }else if (httpQueryString.equals("/?X=Server+beenden")) {
                    String responseString = RMIClient.HTML_START
                            + "<h2 align=center>Server Shutdown</h2>\n"
                            + "<h3 align=center>Aufwiedersehen</h3>\n"
                            + RMIClient.HTML_END;
                    sendResponse(200, responseString, false);
                    System.out.println("Server beendet!");
                    server.close();
                    rmiServer.quit();
                }else{
                    //Splite die get Values in Arrays
                    String[] getValues = httpQueryString.split("\\?");
                    String[] splitAnd = getValues[1].split("&");
                    String[] firstWord = splitAnd[0].split("=");
                    String[] secondWord = splitAnd[1].split("=");

                    //ueberprueft auf Leereeingaben und zeigt Fehlermeldung
                    if((firstWord.length==1 || firstWord[1].matches("[+]*") ) && (secondWord.length==1||secondWord[1].matches("[+]*")))
                    {
                        System.out.println("Server Handels Empty Request of Client: "+ connectedClient.getInetAddress());
                        String responseString = responseStringIndex;
                        responseString +="<table>\n<tr>\n<th>"+"Leereingaben sind Ung\u00FCltig"+"</th>\n</tr>\n</table>\n"+ RMIClient.HTML_END;
                        sendResponse(200, responseString, false);

                    }else{
                        //Fuegt die uebergebenen Parameter einem Array hinzu im Falle das sie nicht null und auch nicht aus nur + (html leerstellen) bestehen
                        //und codiert sie zurueck in utf 8 um die sonderzeichen nutzen zu koennen
                        String[] input = new String[2];
                        System.out.println("Server searchs for Client: "+ connectedClient.getInetAddress());
                        input[0] = firstWord.length!=1 && firstWord[1].matches("[+]*") == false ? java.net.URLDecoder.decode(firstWord[1], "UTF-8") : null;
                        input[1] =  secondWord.length!=1 && secondWord[1].matches("[+]*") == false ? java.net.URLDecoder.decode(secondWord[1], "UTF-8") : null;
                        System.out.println("Searchrequest: "+ (input[0] == null ? "" : input[0])+" "+(input[1] == null ? "" : input[1]));


                        ArrayList<String> results = rmiServer.search(input);
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+results.size());


                        String responseString = RMIClient.HTML_START
                                + "<h2 align=center>Suchergebnisse</h2>\n"
                                + "<h3>Das ergab die Suche</h3>\n"
                                + "<form action=\"./\" "
                                + "method=\"get\">\n"
                                + "<table>\n";
                        System.out.println("Server showing results for Client: "+ connectedClient.getInetAddress());
                        //Baut die Html Seite zum einzeigen der Suchergebnisse
                        for (int i = 0; i < results.size();i++){
                            responseString +="<tr>\n<th>"+results.get(i)+"</th>\n</tr>\n";
                        }
                        responseString +="<td><input type=submit value=\"Zur\u00FCck\"></td>\n</tr>\n"
                                + "</table>\n"+"</form>\n"
                                + RMIClient.HTML_END;
                        sendResponse(200, responseString, false);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //schreibt einen String in Bytes zum Client
    public void sendResponse(int statusCode, String responseString,
                             boolean isFile) throws Exception {

        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String contentTypeLine = "Content-Type: text/html" + "\r\n";
        FileInputStream fin = null;
        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";

        responseString = RMIClient.HTML_START + responseString
                + RMIClient.HTML_END;
        contentLengthLine = "Content-Length: " + responseString.length()
                + "\r\n";
        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(contentTypeLine);
        outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");
        outToClient.writeBytes(responseString);
        outToClient.close();
    }

    public static void main(String args[])  {
        //rmiIp = args[0];
        try {
            //ServerSocket server = new ServerSocket(5000, 10,InetAddress.getByName("127.0.0.1"));
            String host = InetAddress.getLocalHost().getHostName();
            int port = 1337;
            ServerSocket server = new ServerSocket(port);
            System.out.println("HTTP Server Waiting for client on port: "+port+" Host: "+host);
            while (true) {
                Socket connected = server.accept();
                new RMIClient(connected,server).start();
            }
        } catch (Exception e) {
        }

    }

}
