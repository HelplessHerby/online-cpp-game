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
        float accel = 0.5f;
        float maxSpeed = 4f;
        float rotationSpeed = 3f;

        while(true){

            //PlayerMovement

            for (String id:playersMap.keySet()){
                PlayerManagement p = playersMap.get(id);


                //Rotation
                if(p.left) p.rot -= rotationSpeed;
                if(p.right) p.rot += rotationSpeed;

                double rad = Math.toRadians(p.rot);

                //Forward Back Movement
                if(p.up){
                    p.velX += Math.cos(rad) * accel; 
                    p.velY -= Math.sin(rad) * accel;
                }
                if(p.down){
                    p.velX -= Math.cos(rad) * accel;
                    p.velY += Math.sin(rad) * accel;
                }

                float speed = (float)Math.sqrt(p.velX*p.velX + p.velY*p.velY);

                if(speed > maxSpeed){
                    float scale = maxSpeed / speed;
                    p.velX *= scale;
                    p.velY *= scale;
                }

                p.x += p.velX;
                p.y += p.velY;
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