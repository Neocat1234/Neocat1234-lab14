import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<LocalDateTime> connectedTimes;
    
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        connectedTimes = new ArrayList<>();
    }
    
    public void serve(int numberOfClients) {
        for (int i = 0; i < numberOfClients; i++) {
            try {
                Socket clientSocket = serverSocket.accept();
                connectedTimes.add(LocalDateTime.now());
                
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            
            String handshake = in.readLine();
            
            if (!"12345".equals(handshake)) {
                out.println("couldn't handshake");
                out.flush();
                in.close();
                out.close();
                clientSocket.close();
                return;
            }
            
            String request;
            while ((request = in.readLine()) != null) {
                try {
                    int number = Integer.parseInt(request);
                    int factorCount = countFactors(number);
                    out.println("The number " + number + " has " + factorCount + " factors");
                    out.flush();
                } catch (NumberFormatException e) {
                    out.println("There was an exception on the server");
                    out.flush();
                } catch (Exception e) {
                    out.println("There was an exception on the server");
                    out.flush();
                }
            }
            
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private int countFactors(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Number must be positive");
        }
        
        int count = 0;
        int sqrt = (int) Math.sqrt(n);
        
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
    
    public ArrayList<LocalDateTime> getConnectedTimes() {
        Collections.sort(connectedTimes);
        return connectedTimes;
    }
    
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
