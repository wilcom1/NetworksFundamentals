package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {
	
	private Socket s;
	
	public Client(String host, int port, String file) {
		try {
			loopResend(host,port,file, 0);
			sendFileWave(host,port,file);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void loopResend(String host, int port,String file, int iteration) throws IOException {
		int n = 5000*(iteration+1);
		if(iteration<3) {
			try {
				s = new Socket(host, port);
				sendHeader(String.valueOf(n));
				sendFile(file);
				s.close();
			} catch (IOException e) {
				loopResend(host,port,file, iteration+1);
				s.close();
			} finally {
				s.close();
			}
		}else {
			System.out.println("Se agotaron los intentos de reenvío");
		}
	}
	
	
	public void sendFile(String file) throws IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
			//Get the return message from the server
		}
		
		
		 InputStream is = s.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String message = br.readLine();
         System.out.println("Message received from the server : " +message);
		
		fis.close();
		dos.close();	
	}
	
	
	public void sendFileWave(String host, int port, String file) throws IOException {
		
		s = new Socket(host, port);
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[1640];
		int contador = 0;
		byte[] buffer2 = null;
		while (fis.read(buffer) > 0) {
			double limit = 2;
			buffer2 = new byte[(int)limit];
			for(int i=0; i<buffer.length;i++) {
				if(contador<limit) {
					buffer2[contador]=buffer[i];
					contador++;
					System.out.println("contador: "+ contador);
				}else {
					dos.write(buffer2);
					System.out.println("Se envía buffer de: "+ limit);
					limit = Math.pow(limit,2);
					buffer2 = new byte[(int)limit];
					contador = 0;
				}
			}
		}
		
		
		 InputStream is = s.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String message = br.readLine();
         System.out.println("Message received from the server : " +message);
		
		fis.close();
		dos.close();	
	}
	
	public void sendHeader(String msg) throws IOException {
		//Send the message to the server
        OutputStream os = s.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        String number = msg;

        String sendMessage = number + "\n";
        bw.write(sendMessage);
        bw.flush();
        System.out.println("Message sent to the server : "+sendMessage);
	}
	
	public static void main(String[] args) {
		Client fc = new Client("127.0.0.1", 49470, "source/British publisher.docx");
	}

}