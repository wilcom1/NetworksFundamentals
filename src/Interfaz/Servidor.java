package Interfaz;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//Configurar un servidor que reciba una conexión de un cliente desde otro computador, envi una cadena al ciente y cierre la aplicación.
public class Servidor extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;
	private ServerSocket servidor;
	private Socket conexion;
	private int contador = 1;
	//configurar la GUI
	public Servidor ()
	{
		super("Servidor");
		
		Container contenedor = getContentPane();
		
		//Crear componente de escucha
		campoIntroducir = new JTextField();
		campoIntroducir.setEditable(false);
		campoIntroducir.addActionListener(new ActionListener() {
				//Enviar datos al cliente en el otro dispositivo
				public void actionPerformed (ActionEvent evento)
				{
					enviarDatos (evento.getActionCommand());
					campoIntroducir.setText("");
				}
			}
		);
		
		contenedor.add( campoIntroducir, BorderLayout.NORTH);
		//crear pantalla 
		areaPantalla = new JTextArea();
		contenedor.add(new JScrollPane (areaPantalla), 
				BorderLayout.CENTER);
		
		setSize(300,150);
		setVisible(true);
	}	
	
	
	//configurar servidor
	public void ejecutarServidor()
	{
		//Configurar servidor para que reciba conexiones y procese dichas conexiones
		try 
		{
			servidor = new ServerSocket (49470,2);
			//Puerto especificado por el proyecto. Puerto servidor 1.
			//Para el caso del programa en el EQUIPO2 el puerto fue 49770
			while (true) 
			{
				try 
				{
					esperarConexion();
					obtenerFlujos();
					procesarConexion();
				}
				//Excepciones para cuando el cliente cierre la aplicación
				catch (EOFException excepcionEOP) 
				{
					System.err.println("El servidor terminó la conexión");
				}
				finally 
				{
				cerrarConexion();
				++contador;
				}	
			}
		}
		catch (IOException excepcionES) 
		{
			excepcionES.printStackTrace();
		}	
	}
	//Esperar conexion y mostrar información de dicha conexión
	private void esperarConexion () throws IOException
	{
		mostrarMensaje ("Esperando una conexión\n");
		conexion = servidor.accept();
		mostrarMensaje ("Conexión " + contador + " recibida de: " + conexion.getInetAddress().getHostName());
	}
	//obtener flujos para enviar y recibir datos
	private void obtenerFlujos() throws IOException
	{
		//flujo de salida de datos
		salida = new ObjectOutputStream (conexion.getOutputStream());
		salida.flush();
		//flujo de entrada de datos
		try
		{
		entrada = new ObjectInputStream (conexion.getInputStream());
		mostrarMensaje ("\nSe recibieron los flujos de E/S\n");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	//Conexión con el cliente desde otro dispositivo
	private void procesarConexion() throws IOException
	{
		//Mensaje de conexión exitosa
		String mensaje = "Conexión exitosa";
		enviarDatos (mensaje);
		int cont=0;
		//habilitar campo para que el usuario pueda enviar informaciÃ³n
		for (int i =0; i<3; i++)
		{
		do 
		{
			//preocesar los mensajes enviados por el cliente
			//leer el mensaje y mostrarlo en la pantalla
			try 
			{		
				if(cont==0)
				mensaje = (String) entrada.readObject();
				else
				mostrarMensaje ("\n" + String.valueOf(entrada.readByte()));
				cont ++;
			}
			catch(Exception e)
			{
				mostrarMensaje(e.toString());
			}
			
		}
			while (cont<Integer.parseInt(mensaje));
		mostrarMensaje("Se lleno ventana:"+i);
		}
	}
	//cerrar flujos y socket 
	private void cerrarConexion()
	{
		mostrarMensaje("\nFinalizando la conexión\n");
		establecerCampoTextoEditable (false);
		try 
		{
			salida.close();
			entrada.close();
			conexion.close();
		}
		catch (IOException excepcionES) {
			excepcionES.printStackTrace();
		}
	}
	
	
	//enviar mensaje al cliente
	private void enviarDatos(String mensaje)
	{
		try 
		{
			salida.writeObject("SERVIDOR>>>"+mensaje);
			salida.flush();
			mostrarMensaje("\nSERVIDOR>>>"+mensaje);
		}
		//procesar problemas que pueden ocurrir al enviar el objeto
		catch (IOException excepcionES) 
		{
			areaPantalla.append("\nError al escribir objeto");
		}
	}
	
	
	private void mostrarMensaje (final String mensajeAMostrar)
	{
		SwingUtilities.invokeLater(new Runnable() 
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
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				campoIntroducir.setEditable(editable);
			}
		}
		);
	}
	
	
	public static void main (String args[])
	{
		Servidor aplicacion = new Servidor();
		aplicacion.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		aplicacion.ejecutarServidor();
	}
}