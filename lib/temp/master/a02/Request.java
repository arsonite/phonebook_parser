package tb.temp.master.a02;

// Datei: Request.java
// Autor: Brecht
// Datum: 24.05.14
// Thema: Stream-Socket-Verbindungen zwischen Browser und Web-
//        Server. GET-Request herausfiltern, falls POST-Requests
//        nicht benutzt werden.
// -------------------------------------------------------------

import java.io.*;      // Fuer den Reader
import java.net.*;     // Fuer den Socket
import java.util.StringTokenizer;
import java.util.regex.Pattern;

class Request {

    public static void main(String[] args) throws Exception {

        // Vereinbarungen
        // ---------------------------------------------------------
        ServerSocket ss = null;  // Fuer das accept()
        Socket cs = null;  // Fuer die Requests
        InputStream is = null;  // Aus dem Socket lesen
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;  // In den Socket schreiben
        PrintWriter pw = null;
        String zeile = null;  // Eine Zeile aus dem Socket
        String host = null;  // Der Hostname
        int port = 0;     // Der lokale Port

        // Programmstart und Portbelegung
        // ---------------------------------------------------------
        host = InetAddress.getLocalHost().getHostName();
        port = Integer.parseInt(args[0]);
        System.out.println("Server startet auf " + host + " an " + port);

        // ServerSocket einrichten und in einer Schleife auf 
        // Requests warten.
        // ---------------------------------------------------------
        ss = new ServerSocket(port);
        while (true) {
            System.out.println("Warte im accept()");
            cs = ss.accept();               // <== Auf Requests warten

            // Den Request lesen (Hier nur erste Zeile)
            // -------------------------------------------------------
            is = cs.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            zeile = br.readLine();
            System.out.println("Kontrollausgabe: " + zeile);

            // Favicon-Requests nicht bearbeiten
            // -------------------------------------------------------
            if (zeile.startsWith("GET /favicon")) {
                System.out.println("Favicon-Request");
                br.close();
                continue;                       // Zum naechsten Request
            }

            // Erster Aufruf
            if (zeile.equals("GET / HTTP/1.1")) {
                System.out.println("Erster Aufruf, Session wird gestartet!");
                os = cs.getOutputStream();
                pw = new PrintWriter(os);

                pw.println("HTTP/1.1 200 OK"
                        + "Content-Type: text/html");    // Der Header
                pw.println();
                
                // Erste Html-Seite
                pw.println("<html>"
                            + "<body>"
                            +    "<h2 align=center>Telefonverzeichnis</h2>"
                            +    "<h3>Sie k&oumlnnen nach Name oder nach Telefonnummer oder nach beiden (nebenl&aumlufig) suchen.</h3>"
                            +    "<form method=get action='http://"+host+":"+port+"'>"
                            +        "<table>"
                            +           "<tr> <td valign=top>Name:</td> <td><input name=A></td> <td></td> </tr>"
                            +           "<tr> <td valign=top>Nummer:</td> <td><input name=B></td> <td></td> </tr>"
                            +           "<tr> <td valign=top><input type=submit name=C value=Suchen></td>"
                            +           "<td><input type=reset></td>"
                            +           "<td><input type=submit name=D value='Server beenden' ></td> </tr>"
                            +        "</table>"
                            +    "</form>"
                            + "</body>"
                            +"</html>");
                pw.println();
                pw.println();
                pw.flush();
                pw.close();
                br.close();
                continue;
            }

            // Den Request bearbeiten
            // -------------------------------------------------------
            System.out.println("Request wird bearbeitet");

            String[] requestValue = httpRequestValue(zeile);

            String name = requestValue[0];
            String nummer = requestValue[1];
            os = cs.getOutputStream();
            pw = new PrintWriter(os);

            pw.println("HTTP/1.1 200 OK"
                    + "Content-Type: text/html");    // Der Header
            pw.println();

            // Die HTML-Seite
            pw.println("<html>"
                    + "<body>"
                    + "<h2><font color=blue><u>"
                    + "Ergebnis:"
                    + "</u></font></h2>"
            );

            if (requestValue[2].equals("Server+beenden")) {
                pw.print("Der Server wurde beendet!<br>");
            } else {
                pw.print(getContent(name, nummer));
            }
            
            pw.println("<br><form method=get action='http://"+host+":"+port+"'><input type=submit value='Back' value=''></form>"
                    + "</body>"
                    + "</html>"
            );
            pw.println();
            pw.flush();
            pw.close();
            br.close();

            if (requestValue[2].equals("Server+beenden")) {
                break;  // Server beenden
            }
        }  // end while
    } // end main()

    //liest aus dem HTTP-Request die Werte aus und returned sie als String[]
    //----------------------------------------------------------------------------
    private static String[] httpRequestValue(String httpRequest) {
        String[] tempList;
        StringTokenizer sT = new StringTokenizer(httpRequest);

        sT.nextToken();
        sT = new StringTokenizer(sT.nextToken(), "&");

        tempList = new String[sT.countTokens()];

        String[] splitArray;
        int i = 0;
        while (sT.hasMoreTokens()) {
            splitArray = sT.nextToken().split("=");
            tempList[i] = splitArray.length == 2 ? splitArray[1] : null;
            i++;
        }

        return tempList;
    }

    // change the String from HTTP to normal String or normal String to HTTP
    private static String changeHttpString(String string) {
        String res = "";
        char[] charArray;

        if (Pattern.matches(".*[\\+]+.*", string)) {
            charArray = string.toCharArray();

            for (char x : charArray) {
                if (x == '+') {
                    res += " ";
                } else {
                    res += x;
                }
            }
        } else {
            res = string;
        }
        
        if (Pattern.matches(".*%.*", res)) {
            charArray = res.toCharArray();
            res = "";

            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '%') {
                    System.out.println(charArray[i + 1] + charArray[i + 2]);
                        switch (charArray[i + 1] + charArray[i + 2]) {
                            case 137:
                                res += "ü";
                                i += 2;
                                break;
                            case 121:
                                res += "ä";
                                i += 2;
                                break;
                            case 124:
                                res += "ö";
                                i += 2;
                                break;
                        
                    }
                } else {
                    res += charArray[i];
                }
            }
        } else if (Pattern.matches(".*[üäö].*", res)) {
            charArray = res.toCharArray();
            res = "";

            for (int i = 0; i < charArray.length; i++) {
                switch (charArray[i]) {
                    case 'ü':
                        res += "&uuml";
                        break;
                    case 'ä':
                        res += "&auml";
                        break;
                    case 'ö':
                        res += "&ouml";
                        break;
                    default:
                        res += charArray[i];
                }
            }
        }

        return res;
    }

    // returned a String with content for Http
    private static String getContent(String name, String number) {
        Boolean nameCheck = true;
        Boolean numberCheck = true;
        String string = "";
        String searchName = "";
        String searchNumber = "";
        int searchID = 0;

        if (name == null && number == null) {
            string += "Es muss mindestens ein Suchwert eingegeben werden!<br>";
            return string;
        }

        if (name != null) {
            searchName = changeHttpString(name);

            if (searchName.trim().equals("")) {
                string += "Der Name darf nicht aus Leerzeichen oder Tabs bestehen<br>";
                nameCheck = false;
            } else if (!(Pattern.matches(".*[^a-zA-Z äüöÄÜÖ]+.*", searchName))) {
                nameCheck = true;
                searchID = 1;
            } else {
                string += "Im Namen sind nur Buchstaben und Leerzeichen erlaubt!<br>";
                nameCheck = false;
            }
        }

        if (number != null) {
            searchNumber = changeHttpString(number);

            if (searchNumber.trim().equals("")) {
                string += "Die Nummer darf nicht aus Leerzeichen oder Tabs bestehen<br>";
                numberCheck = false;
            } else if (!(Pattern.matches("\\d*\\D+\\d*", searchNumber))) {
                numberCheck = true;
                searchID = searchID == 1 ? 3 : 2;
            } else {
                string += "Nur Zahlen erlaubt!<br>";
                numberCheck = false;
            }
        }

        if (nameCheck && numberCheck) {
            String[][] temp = null;
            switch (searchID) {
                case 1:
                    temp = Abfrage.oneValueSearch(searchName);
                    break;
                case 2:
                    temp = Abfrage.oneValueSearch(searchNumber);
                    break;
                case 3:
                    temp = Abfrage.twoValueSearch(searchName, searchNumber);
                    break;
                default:
                    throw new IllegalArgumentException("searchID false");
            }
            string = temp != null ? buildHttpTable(temp) : "Keine Eintr&aumlge enthalten!<br>";
        }

        return string;
    }

    // build a table as Http-String 
    private static String buildHttpTable(String[][] table) {
        String res = "<table style='border: solid 1px black'>"
                + "<tr><td style='text-align: center; width: 100px; border: solid 1px black;'>Name</td>"
                + "<td style='text-align: center; width: 100px; border: solid 1px black;'>Nummer</td></tr>";

        int rows = table.length - 1;

        while (true) {
            res += "<tr><td style='text-align: center'>" + changeHttpString(table[rows][0]) + "</td>"
                    + "<td style='text-align: center'>" + table[rows][1] + "</td></tr>";
            if (rows == 0) {
                break;
            }
            rows--;
        }

        res += "</table>";

        return res;
    }
}  // end class
