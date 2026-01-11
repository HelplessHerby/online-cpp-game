import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class GameLoop implements Runnable{
    private Map<String,PlayerManagement> playersMap;
    private Map<Socket, String> socketIDMap;

    private static final int MAX_BULLETS = 10;
    private final bulletManagement[] bulletPool = new bulletManagement[MAX_BULLETS];
    private final Set<Socket> levelSent = new HashSet<>();

    public enum gameState {
        START_LEVEL,
        DURING_LEVEL,
        END_LEVEL
    }
    levelSystem.SpawnPoint sp;
    gameState gs = gameState.START_LEVEL;
    levelSystem curLevel = null;
    int levelIndex = 1;
    boolean isShooting = false;
    public GameLoop(Map<String,PlayerManagement> players, Map<Socket, String> socketID){
        this.playersMap=players;
        this.socketIDMap=socketID;     
        initBullets();   
        try{
            //load first level
            curLevel = levelSystem.loadFromCSV("levels/level1.csv", 1);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void run(){
        while(true){                    
            switch (gs) {
                case START_LEVEL:
                    try {
                        levelHandler();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case DURING_LEVEL:
                    for(String id:playersMap.keySet()){
                        PlayerManagement p = playersMap.get(id);
                        if(p.isAlive == 0){
                             gs = gameState.END_LEVEL;
                        }else{
                            duringLevel();
                        }
                    }
                    break;
                case END_LEVEL:

                    break;
                default:
                    break;
            }
            //LevelManagement
            for(Socket s : socketIDMap.keySet()){
                if(!levelSent.contains(s)){
                    try{
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        out.println(curLevel.serialiseLevel());
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

    private void duringLevel(){
        
        for (String id:playersMap.keySet()){
            PlayerManagement p = playersMap.get(id);
            isShooting = p.Shoot();
            if(isShooting){
                spawnBullet(p);
            }
            p.movement();
            p.doMove(curLevel);
            if(!p.spawned){
                spawnPlayer();
                p.spawned = true;
            }
            if(isShooting){
                StringBuilder data = new StringBuilder("SHOOTING");
                data.append(",").append(id)
                    .append(",").append((int)p.x)
                    .append(",").append((int)p.y)
                    .append(",").append((int)p.barrelRot);
                
                String msg = data.toString();

                for (Socket s : socketIDMap.keySet()){
                    try{
                        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                        out.println(msg);
                    } catch(Exception ignored){}
                }
            }
            

        }   
        for (bulletManagement b : bulletPool) {
            b.update(curLevel);
    }
                
    }
    
    private void levelHandler() throws IOException {
        switch (levelIndex) {
            case 1:
                curLevel = levelSystem.loadFromCSV("levels/level1.csv", 1);
                break;
            case 2:
                curLevel = levelSystem.loadFromCSV("levels/level2.csv", 2);
                break;
            default:
                break;
        }

        gs = gameState.DURING_LEVEL;
    }
    private void spawnPlayer(){
                sp = curLevel.getSpawnPoint(0);
                for(String id : playersMap.keySet()){
                    PlayerManagement p = playersMap.get(id);
                    p.x = sp.x * levelSystem.SPRITE_SIZE;
                    p.y = sp.y * levelSystem.SPRITE_SIZE;
                    p.xVel = 0;
                    p.yVel = 0;
                    p.spawned = true;
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
                .append(",").append((int)p.barrelRot)
                .append(",").append((int)p.isAlive);
        }

        data.append(",BULLETS");

        for(bulletManagement b : bulletPool){
           if(b.isAlive){
            data.append(",").append(b.id)
            .append(",").append((int)b.x)
            .append(",").append((int)b.y)
            .append(",").append((int)b.angle)
            .append(",").append(b.isAlive);
           }else{
            data.append(",").append((int)b.id)
                .append(",").append(-1)
                .append(",").append(-1)
                .append(",").append(-1)
                .append(",").append(b.isAlive);
           }
            
        }
        String msg = data.toString();

        for (Socket s : socketIDMap.keySet()){
            try{
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println(msg);
            } catch(Exception ignored){}
        }

        
    }
    private void initBullets() {
        for(int i = 0; i < MAX_BULLETS; i++) {
            bulletPool[i] = new bulletManagement();
            bulletPool[i].id = i;
            bulletPool[i].isAlive = false;
        }
    }   
    private void spawnBullet(PlayerManagement p) {
    for (bulletManagement b : bulletPool) {
        if (!b.isAlive) {
                b.spawn(p.x, p.y, p.barrelRot);
                return;
            }
        }
    }
}