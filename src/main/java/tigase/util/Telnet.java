/*
 * Telnet.java
 *
 * Tigase Jabber/XMPP Utils
 * Copyright (C) 2004-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Describe class Telnet here.
 * <p>
 * <p>
 * Created: Sat Jan 28 21:18:46 2006
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class Telnet {

	private static boolean continuous = false;
	private static boolean debug = false;
	private static long delay = 100;
	private static String file = null;
	private static String hostname = "localhost";
	private static int port = 5222;
	private static boolean stopped = false;

	public static String help() {
		return "\n" + "Parameters:\n" + " -h                this help message\n" + " -n hostname       host name\n" +
				" -p port           port number\n" + " -f file           file with content to send to remote host\n" +
				" -c                continuous sending file content\n" +
				" -t millis         delay between sending file content\n" +
				" -v                prints server version info\n" + " -d [true|false]   turn on|off debug mode\n";
	}

	/**
	 * Describe <code>main</code> method here.
	 *
	 * @param args a <code>String[]</code> value
	 */
	public static void main(final String[] args) throws Exception {
		parseParams(args);
		String data = null;
		if (file != null) {
			FileReader fr = new FileReader(file);
			char[] buff = new char[16 * 1024];
			int res = -1;
			StringBuilder sb = new StringBuilder();
			while ((res = fr.read(buff)) != -1) {
				sb.append(buff, 0, res);
			} // end of while ((res = fr.read(buff)) != -1)
			fr.close();
			data = sb.toString();
		} // end of if (file != null)
		Socket sock = new Socket(hostname, port);
		new Telnet(sock, data);
	}

	public static void parseParams(final String[] args) throws Exception {
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-h")) {
					System.out.print(help());
					System.exit(0);
				} // end of if (args[i].equals("-h"))
				if (args[i].equals("-v")) {
					System.out.print(version());
					System.exit(0);
				} // end of if (args[i].equals("-h"))
				if (args[i].equals("-f")) {
					if (i + 1 == args.length) {
						System.out.print(help());
						System.exit(1);
					} // end of if (i+1 == args.length)
					else {
						file = args[++i];
					} // end of else
				} // end of if (args[i].equals("-h"))
				if (args[i].equals("-n")) {
					if (i + 1 == args.length) {
						System.out.print(help());
						System.exit(1);
					} // end of if (i+1 == args.length)
					else {
						hostname = args[++i];
					} // end of else
				} // end of if (args[i].equals("-h"))
				if (args[i].equals("-p")) {
					if (i + 1 == args.length) {
						System.out.print(help());
						System.exit(1);
					} // end of if (i+1 == args.length)
					else {
						port = Integer.decode(args[++i]);
					} // end of else
				} // end of if (args[i].equals("-h"))
				if (args[i].equals("-d")) {
					if (i + 1 == args.length || args[i + 1].startsWith("-")) {
						debug = true;
					} // end of if (i+1 == args.length)
					else {
						++i;
						debug = args[i].charAt(0) != '-' && (args[i].equals("true") || args[i].equals("yes"));
					} // end of else
				} // end of if (args[i].equals("-d"))
				if (args[i].equals("-c")) {
					if (i + 1 == args.length || args[i + 1].startsWith("-")) {
						continuous = true;
					} // end of if (i+1 == args.length)
					else {
						++i;
						continuous = args[i].charAt(0) != '-' && (args[i].equals("true") || args[i].equals("yes"));
					} // end of else
				} // end of if (args[i].equals("-d"))
			} // end of for (int i = 0; i < args.length; i++)
		}
	}

	public static String version() {
		return "\n" + "-- \n" + "Tigase XMPP Telnet, version: " + Telnet.class.getPackage().getImplementationVersion() +
				"\n" + "Author:	Artur Hefczyc <artur.hefczyc@tigase.org>\n" + "-- \n";
	}

	/**
	 * Creates a new <code>Telnet</code> instance.
	 */
	public Telnet() {
	}

	public Telnet(Socket sock, String data) throws IOException {
		StreamListener sl1 = new StreamListener(sock.getInputStream(), System.out,
												"Hello, this Tigase Telnet program, type your input...\n");
		StreamListener sl2 = new StreamListener(System.in, sock.getOutputStream(), data);
		new Thread(sl1).start();
		new Thread(sl2).start();
	}

	private static class StreamListener
			implements Runnable {

		private String data = null;
		private InputStream is = null;
		private OutputStream os = null;

		private StreamListener(InputStream is, OutputStream os, String data) {
			this.is = is;
			this.os = os;
			this.data = data;
		}

		public void run() {
			try {
				if (data != null) {
					os.write(data.getBytes());
				} // end of if (data != null)
				while (data != null && continuous && !stopped) {
					os.write(data.getBytes());
					if (os == System.out) {
						break;
					} // end of if (os == System.out)
					Thread.currentThread().sleep(delay);
				} // end of while (continuous && !stopped)
				while (!stopped) {
					int chr = is.read();
					if (chr == -1) {
						break;
					} // end of if (chr == -1)
					os.write(chr);
					os.flush();
				} // end of while (true)
			} catch (Exception e) {
				e.printStackTrace();
			} // end of try-catch
			System.exit(1);
		}

	}

} // Telnet