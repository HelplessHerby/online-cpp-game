import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;


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
                    for(PlayerManagement p : playersMap.values()){
                        p.spawned = false;
                        p.isAlive = 1;
                        p.xVel = 0;
                        p.yVel = 0;
                        p.yVel = 0;
                    }
                    try {
                        levelHandler();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case DURING_LEVEL:
                    for(PlayerManagement p : playersMap.values()){
                        if(p.isAlive == 0){
                              
                        }
                    }
                    duringLevel();
                    break;
                    
                case END_LEVEL:
                    levelSent.clear();
                    if(levelIndex == 5) levelIndex = 0;
                    levelIndex++;
                    gs = gameState.START_LEVEL;
                    break;
                default:
                    break;
            }



            sendGameData();

            try{Thread.sleep(16);} catch (Exception e) {}
        }
    }

    private void duringLevel(){
        for(String id :playersMap.keySet()){
            PlayerManagement p = playersMap.get(id);
            if(!p.spawned && p.isAlive == 1){
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
                spawnPlayer(p, id);
            }
        }
        for (String id:playersMap.keySet()){
            PlayerManagement p = playersMap.get(id);
            if(p.isAlive == 0) continue;

            isShooting = p.Shoot(p.id);
            if(isShooting){
                spawnBullet(p);
            }

            p.movement();
            p.doMove(curLevel);
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
            b.checkPlayerCollision(playersMap);
            b.update(curLevel);

        }
        if(alivePlayers() <= 1 && spawnedPlayers() >= 2){
            gs = gameState.END_LEVEL;
        }    
    }
    private int spawnedPlayers(){
        int count = 0;
        for(PlayerManagement p : playersMap.values()){
            if(p.spawned) count++;
            
        }
        return count;
    }
    private int alivePlayers(){
        int count = 0;
        for(PlayerManagement p : playersMap.values()){
            if(p.isAlive == 1 && p.spawned) count++;
            
        }
        return count;
    }
    private void levelHandler() throws IOException {
        switch (levelIndex) {
            case 1:
                curLevel = levelSystem.loadFromCSV("levels/level1.csv", 1);
                break;
            case 2:
                curLevel = levelSystem.loadFromCSV("levels/level2.csv", 2);
                break;
            case 3:
                curLevel = levelSystem.loadFromCSV("levels/level3.csv", 3);
                break;
            case 4:
                curLevel = levelSystem.loadFromCSV("levels/level4.csv", 4);
                break;
            case 5:
                curLevel = levelSystem.loadFromCSV("levels/level5.csv", 5);
                break;
            default:
                break;
        }
        
        gs = gameState.DURING_LEVEL;
    }

    //Spawns player at corresponding spawn point
    private void spawnPlayer(PlayerManagement p , String id){
        sp = curLevel.getSpawnPoint();
        p.spawnPlayer(sp.x * 32,sp.y*32);
        p.isAlive = 1;
        p.id = id;
        p.spawned = true;

        
    }
    private void sendGameData(){
        StringBuilder data = new StringBuilder("GAME_DATA");

        //Player Server Data
        for(String id : playersMap.keySet()) {
            PlayerManagement p = playersMap.get(id);
            data.append(",").append(id)
                .append(",").append((int)p.x)
                .append(",").append((int)p.y)
                .append(",").append((int)p.rot)
                .append(",").append((int)p.barrelRot)
                .append(",").append((int)p.isAlive);
        }

        //bullet Server Data
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
            System.out.println("Bullet owner " + b.ownerID + " p id" + p.id);
                b.ownerID = p.id;
                b.spawn(p.x, p.y, p.barrelRot);
                System.out.println("Bullet id: " + b.ownerID + " Player id: " + p.id);
                return;
            }
        }
    }
}