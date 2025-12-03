import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class GameLoop implements Runnable{
    private Map<String,PlayerManagement> playersMap;
    private Map<Socket, String> socketIDMap;

    public GameLoop(Map<String,PlayerManagement> players, Map<Socket, String> socketID){
        this.playersMap=players;
        this.socketIDMap=socketID;
    }

    @Override
    public void run(){


        while(true){

            //PlayerMovement

            for (String id:playersMap.keySet()){
                PlayerManagement p = playersMap.get(id);

                p.movement();    	

                    
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
                .append(",").append((int)p.rot);
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