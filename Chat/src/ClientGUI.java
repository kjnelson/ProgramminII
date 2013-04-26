
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * Applies GUI elements to the client
 */
public class ClientGUI extends JFrame implements ActionListener 
{

	private static final long serialVersionUID = 1L;
	// Will first hold "Username:", later on "Enter message"
	private JLabel label;
	// To hold the Username and later on the messages
	private JTextField tf;
	// To hold the server address and the port number
	private JTextField tfServer, tfPort;
	// To Logout and get the list of the users
	private JButton login, logout, whoIsIn, shareFile;
	// For the chat room
	private JTextArea ta;
	// If it is for connection
	private boolean connected;
	// The Client object
	private Client client;
	// The default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receives a socket number
	ClientGUI(String host, int port) 
	{

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// The server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// The two JTextField with default values for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// Adds the Server and port field to the GUI
		northPanel.add(serverAndPort);

		// The Label and the TextField for logging in
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("Anonymous");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel containing the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 0, 40);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		ta.setLineWrap(true);
		add(centerPanel, BorderLayout.CENTER);

		// Action buttons to login, logout, and check users
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// Logout requires being logged in first
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// whoIsIn also requires being logged in
		shareFile = new JButton("Share File");
		shareFile.addActionListener(this);
		shareFile.setEnabled(false);		// shareFile requires being logged in

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		southPanel.add(shareFile);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	// Called by the Client to append text in the TextArea 
	void append(String str) 
	{
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// Called by the GUI if the connection failed;
	// reset our buttons, label, and textfield
	void connectionFailed() 
	{
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		shareFile.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// Reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// Let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// Don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) 
	{
		Object o = e.getSource();
		// If it is the Logout button
		if(o == logout) 
		{
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		// If it is the WhoIsIn button
		if(o == whoIsIn) 
		{
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));				
			return;
		}

		// Source is the JTextField
		if(connected) 
		{
			// Sends the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));				
			tf.setText("");
			return;
		}
		

		if(o == login) 
		{
			// Ok it is a connection request
			String username = tf.getText().trim();
			// Empty username, ignore it
			if(username.length() == 0)
				return;
			// Empty serverAddress, ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// Empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try 
			{
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) 
			{
				return;   // Nothing I can do if the port number is not valid
			}

			// Try creating a new Client with the GUI
			client = new Client(server, port, username, this);
			// Test if the Client can start
			if(!client.start()) 
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			
			// Disable login button
			login.setEnabled(false);
			// Enable the three buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			shareFile.setEnabled(true);
			// Disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when a user enters a message
			tf.addActionListener(this);
		}

	}

	// Starts the program with a default username and port number
	public static void main(String[] args) 
	{
		new ClientGUI("localhost", 1500);
	}

}

