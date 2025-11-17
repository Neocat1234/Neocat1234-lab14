import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    //the main server socket that listens for connections
    private ServerSocket serverSocket;
    private ArrayList<LocalDateTime> connectedTimes;
    
    public Server(int port) throws IOException {
        //create the server socket on the port
        serverSocket = new ServerSocket(port);
        connectedTimes = new ArrayList<>();
    }
    
    public void serve(int numberOfClients) {
        //loop to accept the number of clients we expect
        for (int i = 0; i < numberOfClients; i++) {
            try {
                // wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                //record when this client connected
                connectedTimes.add(LocalDateTime.now());
                
                //create a new thread to handle this client so we can keep accepting more
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start(); //start the thread
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleClient(Socket clientSocket) {
        try {
            //setup reader and writer for this specific client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            
            // first read the handshake code from client
            String handshake = in.readLine();
            
            // check if they sent the right passcode
            if (!"12345".equals(handshake)) {
                // if wrong passcode, tell them and disconnect
                out.println("couldn't handshake");
                out.flush();
                in.close();
                out.close();
                clientSocket.close();
                return; 
            }
            
            //keep reading requests from the client until they disconnect
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    int number = Integer.parseInt(request);
                    int factorCount = countFactors(number);
                    //send back the result
                    out.println("The number " + number + " has " + factorCount + " factors");
                    out.flush(); 
                } catch (NumberFormatException e) {
                    out.println("There was an exception on the server");
                    out.flush(); //exception handling
                } catch (Exception e) {
                    out.println("There was an exception on the server");
                    out.flush();
                }
            }
            
            //clean up when client disconnects
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //counts how many factors a number has
    private int countFactors(int n) {
        //make sure the number is positive
        if (n <= 0) {
            throw new IllegalArgumentException("Number must be positive"); //throw error 
        }
        
        int count = 0; // keeps track of how many factors we find
        int sqrt = (int) Math.sqrt(n); // only need to check up to square root
        
        // loop through potential factors
        for (int i = 1; i <= sqrt; i++) {
            if (n % i == 0) { 
                count++; 
                if (i != n / i) { 
                    count++; 
                }
            }
        }
        
        return count; 
    }
    
    // returns the list of when clients connected (sorted)
    public ArrayList<LocalDateTime> getConnectedTimes() {
        Collections.sort(connectedTimes); 
        return connectedTimes;
    }
    
    // shuts down the server
    public void disconnect() {
        try {
            
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
