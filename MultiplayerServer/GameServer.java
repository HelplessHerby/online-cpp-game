import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    private static int playerCount = 1;
    private static Map<String, PlayerManagement> players = new HashMap<>();
    private static Map<Socket, String> socketToID = new HashMap<>();

    public static void main(String[] args) {
        int port = 55555; 
        System.out.println("[Server] Starting on port " + port);

        Thread gameLoopThread = new Thread(new GameLoop(players,socketToID));
        gameLoopThread.start();

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
                //Assign ID
                String playerID = assignPlayerID(socket);
                //Assign player
                PlayerManagement newPlayer = new PlayerManagement();
                players.put(playerID, newPlayer);
                //Input Output streams
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                
                out.println("ASSIGN_ID," + playerID);

                handlePlayerMessages(socket, playerID, in, out);
            } catch(IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    private static String assignPlayerID(Socket socket){
        String playerID = "Player: " + playerCount++;
        socketToID.put(socket, playerID);

        System.out.println("[Server] Assigned " + playerID + " to " + socket.getInetAddress());

        return playerID;
    }

    private static void removePlayer(Socket socket, String playerID){
        System.out.println("[Server] Removing Player: " + playerID);

        players.remove(playerID);

        socketToID.remove(socket);

        playerCount--;

        System.out.println(playerCount);
        try{
            if(!socket.isClosed()) socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private static void handlePlayerMessages(Socket socket, String playerID, BufferedReader in, PrintWriter out) throws IOException{
        try{
            String message;
            while((message = in.readLine()) != null){
                message = message.trim();
                if(message.isEmpty()) continue;

                if (message.startsWith("CLIENT_DATA")) {

                    String[] parts = message.split(",");

                    if (parts.length != 7) {
                        System.out.println("[Server] Malformed CLIENT_DATA (" + parts.length + "): " + message);
                        return;
                    }

                    PlayerManagement player = players.get(playerID);
                    if (player == null) return;

                    boolean moveUp    = parts[1].trim().equals("1");
                    boolean moveDown  = parts[2].trim().equals("1");
                    boolean turnLeft  = parts[3].trim().equals("1");
                    boolean turnRight = parts[4].trim().equals("1");
                    boolean shooting  = parts[5].trim().equals("1");

                    float barrelRot;
                    try {
                        barrelRot = Float.parseFloat(parts[6].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("[Server] Invalid barrel rotation: " + parts[6]);
                        return;
                    }

                    player.applyInput(
                        moveUp,
                        moveDown,
                        turnLeft,
                        turnRight,
                        shooting,
                        barrelRot
                    );

                    System.out.println("[Server] " + playerID + " input applied");
                }
                if(message.startsWith("exit")){
                    System.out.println("[Server]  " + playerID + " disconnected.");
                    removePlayer(socket, playerID);
                    break;
            }
        }
        

    } catch (IOException e){
        removePlayer(socket, playerID);
        System.out.println("[Server] " + playerID + " disconnected abruptly.");
    } finally {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    }
}
