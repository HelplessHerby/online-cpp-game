import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Enemy {
    public int tileX,tileY;

    public float x,y;
    private static final float speed = 2f;
    private boolean isAlive = true;

    public int id;
    private List<Node> curPath;
    private int pathIndex = 0;

    private final AStarPathFinder pathFinder;
    private static final int SEARCH_RADIUS = 50;
    public Enemy(int startTileX, int startTileY, levelSystem level){
        this.tileX = startTileX;
        this.tileY = startTileY;

        this.x = tileX * 32f; //Sprite Size is 32
        this.y = tileY * 32f;

        this.pathFinder = new AStarPathFinder(level);
    }

    public boolean isAlive(){
        return isAlive;
    }

    public void update(PlayerManagement target, levelSystem level){
        if(!isAlive || target == null) return;

        int targetTileX = (int)(target.x/32f);
        int targetTileY = (int)(target.y/32f);

        //Recalc path
        curPath = pathFinder.findPath(tileX, tileY, targetTileX, targetTileY, level);
        pathIndex = 0;
            
        moveAlongPath(level);
        checkExplode(target);
        

    }

    private void moveAlongPath(levelSystem level){
        if(curPath == null || pathIndex >= curPath.size()) { System.out.println("null check"); return;}

        Node next = curPath.get(pathIndex);


        //Skips current tile
        if(next.x == tileX && next.y == tileY){
            pathIndex++;
            if(pathIndex >= curPath.size())return;
            next = curPath.get(pathIndex);
        }
        float targetX = next.x * 32f;
        float targetY = next.y * 32f;

        float dx = targetX - x;
        float dy = targetY - y;

        float distance = (float)Math.sqrt(dx*dx+dy*dy);

        if(distance < 1f){
            tileX = next.x;
            tileY = next.y;
            pathIndex++;
            return;
        }

        x += (dx / distance) * speed;
        y += (dy / distance) * speed;

        tileX = (int)(x / 32f);
        tileY = (int)(y / 32f);
        System.out.println("[Enemy " + id + "] Moving to (" + x + "," + y + ")");

    }

    private void checkExplode(PlayerManagement player){

        int playerTileX = (int)(player.x / 32f);
        int playerTileY = (int)(player.y / 32f);


        if(this.tileX == playerTileX && this.tileY == playerTileY){
            explode(player);
        }
    }

    private void explode(PlayerManagement player){
        isAlive = false;
        player.isAlive = 0;
        System.out.println("[Enemy] Player killed by explosion");
    }
    
    private List<Node> findClosestWalkable(int tx, int ty, levelSystem level) {
        int bestDist = Integer.MAX_VALUE;
        Node closest = null;


        int dist = Math.abs(tx - tileX) + Math.abs(ty - tileY);
        if (dist < bestDist) {
            bestDist = dist;
            closest = new Node(tx, ty);

        }

        if (closest != null) {
            return pathFinder.findPath(tileX, tileY, closest.x, closest.y, level);
        }

        return Collections.emptyList();
    }
}
