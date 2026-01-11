import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerGUI {

    private JFrame frame;
    private JTextArea logArea;
    private JScrollPane scrollPane;

    private Map<String, PlayerManagement> players;
    private Map<Socket, String> socketToID;

    public ServerGUI(Map<String, PlayerManagement> players, Map<Socket, String> socketToID) {
        this.players = players;
        this.socketToID = socketToID;

        setupGUI();
        systemOutputs();
        startServer();
    }

    private void setupGUI() {
        frame = new JFrame("Herby's Tanks: Server Application");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Roman", Font.PLAIN, 12));

        scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void systemOutputs() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                appendText(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendText(new String(b, off, len));
            }
        };

        PrintStream ps = new PrintStream(out, true);
        System.setOut(ps);
        System.setErr(ps);
    }

    private void appendText(final String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void startServer() {
        new Thread(() -> GameServer.main(new String[0])).start();
    }

    public static void main(String[] args) {
        // Use the same maps as your server
        Map<String, PlayerManagement> players = new HashMap<>();
        Map<Socket, String> socketToID = new HashMap<>();
        new ServerGUI(players, socketToID);
    }
}
