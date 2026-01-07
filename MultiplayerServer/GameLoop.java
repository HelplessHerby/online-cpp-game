import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameLoop implements Runnable{
    private Map<String,PlayerManagement> playersMap;
    private Map<Socket, String> socketIDMap;

    private final levelSystem level;
    private final Set<Socket> levelSent = new HashSet<>();

    public GameLoop(Map<String,PlayerManagement> players, Map<Socket, String> socketID){
        this.playersMap=players;
        this.socketIDMap=socketID;
        levelSystem tempLevel = null;
        try{
            //load first level
            tempLevel = levelSystem.loadFromCSV("levels/level1.csv", 1);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        this.level = tempLevel;
    }

    @Override
    public void run(){
        while(true){                    
            //PlayerMovement
            for (String id:playersMap.keySet()){
                PlayerManagement p = playersMap.get(id);
                p.movement();
                p.doMove(level);
            }

            //LevelManagement
            for(Socket s : socketIDMap.keySet()){
                if(!levelSent.contains(s)){
                    try{
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        out.println(level.serialiseLevel());
                        levelSent.add(s);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }


            sendGameData();

            try{Thread.sleep(16);} catch (Exception e) {}
        }
    }


    private void sendGameData(){
        StringBuilder data = new StringBuilder("GAME_DATA");

        for(String id : playersMap.keySet()) {
            PlayerManagement p = playersMap.get(id);
            data.append(",").append(id)
                .append(",").append((int)p.x)
                .append(",").append((int)p.y)
                .append(",").append((int)p.rot)
                .append(",").append((int)p.barrelRot);
        }
        String msg = data.toString();

        for (Socket s : socketIDMap.keySet()){
            try{
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println(msg);
            } catch(Exception ignored){}
        }
    }
}