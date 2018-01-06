package tb.a02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class {@code Server}
 * 
 * @since JDK 1.91 ~ <i>2017</i>
 * @author Burak Günaydin <b>{@code (853872)}</b>
 */
public class TestServer {
	static String host;
	static int port;
	static boolean[] status; 
	static Utility u;
	static ServerSocket ss;
	static Socket cs;
	static BufferedReader bR;
	static PrintWriter out;

	public static void main(String[] args ) throws Exception {
		StreamBuffer.fixConsole();
		host = InetAddress.getLocalHost().getHostName();
		//port = Integer.parseInt(98 + host.replaceAll("[^\\d]+", ""));
		port = 8888;
		System.out.println(host + ":" + port);
		ss = new ServerSocket(port);
		u = new Utility();
		status = new boolean[10];
		while(true) {
			cs = ss.accept();
			bR = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			out = new PrintWriter(cs.getOutputStream());
			String info = bR.readLine();
			if(info.equals("GET / HTTP/1.1")) {
				System.err.println("");
				printWelcomeHTML();
			}
			if(info.startsWith("GET /favicon")) {
				System.err.println("Favicon-Request");
				bR.close();
				continue;
			}
			System.out.println(info);
			System.out.println();
			/*
			String info = null;
			while((info = bR.readLine()) != null) {
				if(info.equals("GET / HTTP/1.1")) {
					printWelcomeHTML();
				}
				//if(antwort.startsWith("GET /?Name")) {
				//	readInput(antwort);
				//}
				if(info.startsWith("GET /favicon")) {
					break;      
				}
				if(info.isEmpty()) {
					break;
				}
				System.err.println(info);
			}
			 */
			out.flush();
			out.close();
			bR.close();
			cs.close();
		}
	}

	final static void printWelcomeHTML() {
		out.println("HTTP/1.1 200 OK +"
				+ "Content-Type: text/html");
		out.println();
		out.println("<html>"
				+	"<head>"
				+	"<meta charset=\"utf-8\">"
				+	"</head>"
				+ 	"<body>"
				+ 	"<h2 align=left>Das Online-Telefonbuch-Verzeichnis</h2>"
				+	"<h3>Suche nach einem <i>Namen</i>, einer <i>Nummer</i> oder beidem (nebenläufig).</h3>"
				+		"<form method=get action='http://" + host + ":" + port + "'>"
				+			"<table>"
				+           		"<tr><td valign=top>Name:</td><td><input name=A></td><td></td></tr>"
				+           		"<tr><td valign=top>Nummer:</td> <td><input name=B></td><td></td></tr>"
				+           		"<tr><td valign=top><input type=submit name=C value=\"Starte Suche\"></td>"
				+           		"<td><input type=reset value=\"Löschen\"></td>"
				+				"<td><input type=submit name=D value=\"Beende Server\"></td></tr>"
				+			"</table>"
				+		"</form>"
				+	"</body>"
				+ "</html>");
		out.println();
		out.println();
	}
}