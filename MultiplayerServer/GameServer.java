import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    private static int playerCount = 1;
    private static Map<Socket,String> playerMap = new HashMap<>();

    public static void main(String[] args) {
        int port = 55555; 
        System.out.println("[Server] Starting on port " + port);

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("[Server] Waiting for clients...");

            while (true) {
                Socket socket = server.accept();
                handleNewPlayer(socket);
            }
        }
        
    catch (IOException e){
        e.printStackTrace();
    }
}

    private static void handleNewPlayer(Socket socket){
        new Thread(() -> {
            try{

                String playerID = assignPlayerID(socket);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                
                handlePlayerMessages(socket, playerID, in, out);

                in.close();
                out.close();
                socket.close();
                playerMap.remove(socket);
            } catch(IOException e){
                e.printStackTrace();
            }
        }).start();
    }


    private static String assignPlayerID(Socket socket) throws IOException{
        String playerID = "Player: " + playerCount++;
        playerMap.put(socket, playerID);

        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println("ASSIGN_ID," + playerID);
        System.out.println("[Server] Assigned " + playerID + " to " + socket.getInetAddress());

        return playerID;
    }

    private static void handlePlayerMessages(Socket socket, String playerID, BufferedReader in, PrintWriter out) throws IOException{
        try{
            String message;
            while((message = in.readLine()) != null){
                message = message.trim();
                if(message.isEmpty()) continue;

                if(message.startsWith("CLIENT_DATA")){
                    String[] parts = message.split(",");
                    for (int i = 1; i < parts.length; i++) { 
                        String action = parts[i].trim();
                        if (!action.isEmpty()) {
                            System.out.println("[Server] " + playerID + " sent: " + action);
                        }
                    }
                
                }
                if(message.startsWith("exit")){
                    System.out.println("[Server]  " + playerID + " disconnected.");
                    break;
            }
        }
        

    } catch (IOException e){
        System.out.println("[Server] " + playerID + " disconnected abruptly.");
    } finally {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        playerCount--;
        playerMap.remove(socket);
    
    }
    }
}
