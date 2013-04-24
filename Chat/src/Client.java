
import java.net.*;
import java.io.*;
import java.util.*;

/*
 * Client that runs off the console; GUI is optional
 */
public class Client  
{

	// I/O classes
	private ObjectInputStream sInput;		// To read from the socket
	private ObjectOutputStream sOutput;		// To write on the socket
	private Socket socket;

	// Used if Client runs from GUI
	private ClientGUI cg;
	
	// The server, port and username classes
	private String server, username;
	private int port;

	/*
	 *  Constructor called by console mode
	 *  Server: the server address
	 *  Port: the port number
	 *  Username: the username
	 */
	Client(String server, int port, String username) 
	{
		// Call for the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/*
	 * Constructor call when used from a GUI
	 * in console mode; the ClientGUI parameter is null
	 */
	Client(String server, int port, String username, ClientGUI cg) 
	{
		this.server = server;
		this.port = port;
		this.username = username;
		// Save if we are in GUI mode or not
		this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() 
	{
		// Try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// Exception if failed, likely from mismatching address/port
		catch(Exception ec) 
		{
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) 
		{
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// Creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server, this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) 
		{
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// Success: we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) 
	{
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");		// Append to the ClientGUI JTextArea (Or other applicable object)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) 
	{
		try 
		{
			sOutput.writeObject(msg);
		}
		catch(IOException e) 
		{
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect.
	 */
	private void disconnect() 
	{
		try 
		{ 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // Not much else I can do
		try 
		{
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // Not much else I can do
        try
        {
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // Not much else I can do
		
		// Inform the GUI of failed connection
		if(cg != null)
			cg.connectionFailed();
			
	}
	/*
	 * To start the Client in console mode use one of the following commands:
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * At the console prompt:
	 * If the portNumber is not specified, 1500 is used
	 * If the serverAddress is not specified, "localHost" is used
	 * If the username is not specified, "Anonymous" is used
	 * > java Client 
	 * is equivalent to
	 * > java Client Anonymous 1500 localhost 
	 * are equivalent
	 * 
	 * In console mode, if an error occurs the program simply stops
	 * when a GUI ID used, the GUI is informed of the disconnection
	 */
	public static void main(String[] args) 
	{
		// Default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// Depending of the number of arguments provided we fall through
		switch(args.length) 
		{
			// > javac Client username portNumber serverAddress
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try 
				{
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) 
				{
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// Create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// Test if a connection to the Server can be established
		if(!client.start())
			return;
		
		// Wait for messages from user
		Scanner scan = new Scanner(System.in);
		// Loop forever for message from the user
		while(true) 
		{
			System.out.print("> ");
			// Read message from user
			String msg = scan.nextLine();
			// Logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) 
			{
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// Break to do the disconnect
				break;
			}
			// Message WhoIsIn
			else if(msg.equalsIgnoreCase("WHOISIN")) 
			{
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			}
			else 
			// Default to ordinary message
			{				
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// Disconnect the client and close scanner
		client.disconnect();
		scan.close();
	}

	/*
	 * A class that waits for the message from the server and appends them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread 
	{

		public void run() 
		{
			while(true) 
			{
				try 
				{
					String msg = (String) sInput.readObject();
					// If in console mode, print the message and add back the prompt
					if(cg == null) 
					{
						System.out.println(msg);
						System.out.print("> ");
					}
					else 
					{
						cg.append(msg);
					}
				}
				catch(IOException e) 
				{
					display("Server has closed the connection: " + e);
					if(cg != null) 
						cg.connectionFailed();
					break;
				}
				// Required but not applicable with a String object
				catch(ClassNotFoundException e2) 
				{
				}
			}
		}
	}
}

