import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

public class GameServer extends JFrame{
    private JTextArea consoleArea;
    private JButton startButton;
    private JButton stopButton;
    ServerSocket serverSocket;
    private Thread serverThread;
    private boolean serverRunning = false;

    private static int playerCount = 1;
    private static Map<String, PlayerManagement> players = new HashMap<>();
    private static Map<Socket, String> socketToID = new HashMap<>();

    public GameServer(){
        setTitle("Herby's Tanks Server");
        setSize(600,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Console
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleArea);
        add(scrollPane,BorderLayout.CENTER);

        //Buttons
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        add(buttonPanel,BorderLayout.SOUTH);

        //System out into the console
        PrintStream printStream = new PrintStream(new OutputStream(){
            @Override
            public void write(int b){
                consoleArea.append(String.valueOf((char)b));
                consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
            }
        });
        System.setOut(printStream);
        System.setErr(printStream);

        //Button Logic
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e->stopServer());
    }

    private void startServer(){
            if (serverRunning) return;
    serverRunning = true;

    serverThread = new Thread(() -> {
        int port = 55555;



        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[Server] Starting on port " + port);
            System.out.println("[Server] Waiting for clients...");

            while (serverRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    handleNewPlayer(socket);
                } catch (SocketException se) {
                    if (serverRunning) System.out.println("[Server] Socket exception: " + se);
                } catch (IOException e) {
                    System.out.println("[Server] IOException: " + e);
                }
            }

        } catch (IOException e) {
            if (serverRunning) System.out.println("[Server] Failed to start: " + e);
        } finally {
            if(serverSocket != null && !serverSocket.isClosed()){
                try{
                    serverSocket.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            System.out.println("[Server] Server stopped.");
        }
    });

    serverThread.start();
    startButton.setEnabled(false);
    stopButton.setEnabled(true);
    }



private void stopServer() {
    if (!serverRunning) return;
    serverRunning = false;

    if(serverSocket != null && !serverSocket.isClosed()){
        try{
            serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    if (serverThread != null && serverThread.isAlive()) {
        try {
            serverThread.join(500); // wait for thread to exit cleanly
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    startButton.setEnabled(true);
    stopButton.setEnabled(false);
    System.out.println("[Server] Closed");
}
    public static void main(String[] args) {
        // Start the game loop
        Thread gameLoopThread = new Thread(new GameLoop(players, socketToID));
        gameLoopThread.start();
        SwingUtilities.invokeLater(() -> {
            GameServer gui = new GameServer();
            gui.setVisible(true);
        });
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
                        System.out.println("[Server] Wrong CLIENT_DATA (" + parts.length + "): " + message);
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
