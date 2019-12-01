package Interfaz;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.Socket;
public class Cliente extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private String mensaje = "";
	private String servidorChat;
	private Socket cliente;
	public Cliente (String host)
	
	{
		super ("Cliente");
		
		servidorChat = host;
		
		Container contenedor = getContentPane();
		
		campoIntroducir = new JTextField();
		campoIntroducir.setEditable(false);
		campoIntroducir.addActionListener(new ActionListener() 
		{			
			public void actionPerformed (ActionEvent evento)
			{
				enviarDatos (evento.getActionCommand());
				campoIntroducir.setText("");
			}
		}
		);
		contenedor.add(campoIntroducir, BorderLayout.NORTH);
		areaPantalla = new JTextArea();
		contenedor.add(new JScrollPane (areaPantalla), 
		BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	
	public void ejecutarCliente()
	{
		try {
			conectarAServidor();
			obtenerFlujos();
			procesarConexion();
	}
	
		catch (EOFException excepcionEOF) {
			System.err.println("El cliente terminó la conexion");
		}
		
		catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		}
		
		finally {
			cerrarConexion();
		}
	}
	
	
	private void conectarAServidor() throws IOException
	{
		mostrarMensaje ("Intentando realizar conexion\n");
		
		cliente = new Socket (InetAddress.getByName(servidorChat),49470);
		
		mostrarMensaje ("Conectado a: " + 
				cliente.getInetAddress().getHostName());
	}
	
	
	private void obtenerFlujos() throws IOException
	{
		salida = new ObjectOutputStream(cliente.getOutputStream());
		salida.flush();
		
		entrada = new ObjectInputStream (cliente.getInputStream());
		mostrarMensaje ("\nSe recibieron los flujos de E/S\n");
	}
	
	
	private void procesarConexion() throws IOException
	{
		establecerCampoTextoEditable(true);
		do 
		{
			try 
			{
				mensaje = (String) entrada.readObject();
				mostrarMensaje ("\n"+mensaje);
			}
			catch (ClassNotFoundException excepcionClaseNoEncontrada) 
			{
				mostrarMensaje ("\nSe recibió un objeto de tipo desconocido");
			}
		}
		while (!mensaje.equals("SERVIDOR>>> TERMINAR"));
	}
	
	private void cerrarConexion()
	{
		mostrarMensaje ("\nCerrando conexion");
		establecerCampoTextoEditable(false);
		try 
		{
			salida.close();
			entrada.close();
			cliente.close();
		}
		catch (IOException excepcionES) 
		{
			excepcionES.printStackTrace();
		}
	}
	
	private void enviarDatos (String mensaje)
	{
		try 
		{
			salida.writeObject("CLIENTE>>> " + mensaje);
			salida.flush();
			mostrarMensaje("\nCLIENTE>>> " + mensaje);
		}
		catch (IOException excepcionES) 
		{
			areaPantalla.append("\nError al escribir el objeto");
		}
	}
	private void mostrarMensaje (final String mensajeAMostrar)
	{
		SwingUtilities.invokeLater(
				new Runnable() 
				{	
					public void run()
					{
						areaPantalla.append(mensajeAMostrar);
						areaPantalla.setCaretPosition(
								areaPantalla.getText().length());
					}
				}
			);
	}
	
	private void establecerCampoTextoEditable (final boolean editable)
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					
					public void run()
					{
						campoIntroducir.setEditable(editable);
					}
				}
			);	
	}	
	
	public static void main (String args[])
	{
		
		Cliente aplicacion;
		if (args.length == 0){
			aplicacion = new Cliente ("127.0.0.1");
			aplicacion.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			aplicacion.ejecutarCliente();
			aplicacion.windowSizeTransfer();
			aplicacion.fileTransfer();
			}
		else
			aplicacion = new Cliente (args[0]);
		
		
	}
	
	
	private void fileTransfer() {
		
	}
	
	private void windowSizeTransfer() {
		String windowSize = "5";
		Socket socket;
		InputStream io = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos;
		int bufferSize = 0;
		FileInputStream fis;
		BufferedInputStream bis;
		try {
			salida.writeObject(windowSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}