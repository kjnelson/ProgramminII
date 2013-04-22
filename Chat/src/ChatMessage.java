import java.io.*;

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    /* Message Types
     LOGOUT to disconnect from the Server
     WHOISIN to receive the list of users
     MESSAGE an ordinary message
     PRIVMESSAGE to send a Private Message
     SENDFILE to send a file
    */ 
    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, PRIVMESSAGE = 3, SENDFILE = 4;
    private int type;
    private String message;
   
    // message constructor

   ChatMessage(int type, String message) 
   	{
        this.type = type;
        this.message = message;
    }

    int getType() 
    {
        return type;
    }

    String getMessage() 
    {
        return message;
    }
}
