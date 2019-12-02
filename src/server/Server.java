package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	
	private ServerSocket ss;
	int filesize = 15123;
	public Server(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				try {
					readHeader(clientSock);
				}catch(Exception e) {
					System.out.println("Error leyendo Header");
				}
				saveFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void readHeader(Socket clientSock) throws IOException {
		 InputStream is = clientSock.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String number = br.readLine();
         filesize = Integer.parseInt(number);
         System.out.println("El tamaño de la ventana es: "+number);
	}

	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
		FileOutputStream fos = new FileOutputStream("British publisher.docx");
		byte[] buffer = new byte[4096];
		
		 // Send file size in separate msg
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		//Responder
		OutputStream os = clientSock.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(filesize);
        System.out.println("Message sent to the client is "+filesize);
        bw.flush();
		
		fos.close();
		dis.close();
	}
	
	private void sendMessageToClient(String msg) {
		
	}
	
	public static void main(String[] args) {
		Server fs = new Server(49470);
		fs.start();
	}

}
