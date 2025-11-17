import java.io.*;
import java.net.*;

public class Client {
    private Socket socket; // the socket that connects to the server
    private PrintWriter out; // for sending messages to server
    private BufferedReader in; // for reading messages from server
    
    public Client(String host, int port) throws IOException {
        // create the socket connection with the host name and port number
        socket = new Socket(host, port);// setup the writer so we can send stuff
        out = new PrintWriter(socket.getOutputStream()); // setup the reader so we can get stuff back from server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    // getter method that returns the socket
    public Socket getSocket() {
        return socket;
    }
    
    public void handshake() throws IOException {
        out.println("12345"); // send the secret code
        out.flush(); 
    }
    
    //send a message to the server and get back the response
    public String request(String message) throws IOException {
        out.println(message); //send our message
        out.flush(); 
        return in.readLine(); //wait for and return the server's response
    }
    
    //closes everything when we're done
    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }
}
