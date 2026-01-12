import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class levelSystem{
    //Tile Types
    public static final int TILE_EMPTY = 0;
    public static final int TILE_WALL = 1;
    public static final int TILE_BREAKABLE = 2;
    public static final int TILE_HOLE = 3;
    public static final int TILE_SPAWN = 4;
    public static final int SPRITE_SIZE = 32;

    private int levelID;
    private int width;
    private int height;

    private Tile[][] tiles;
    //Player Spawn
    private final List<SpawnPoint> spawnPoints = new ArrayList<>();

    //Server Level
    public String serialiseLevel(){
        StringBuilder sb = new StringBuilder();
        sb.append("LEVEL_LOAD,")
        .append(levelID).append(",")
        .append(width).append(",")
        .append(height);

        for(int y = 0; y < height; y++){
            sb.append(",ROW");
            for(int x = 0; x < width; x++){
                sb.append(",").append(tiles[y][x].type);
            }
        }
        sb.append(",LEVEL_END ");
        System.out.println(sb.toString());
        return sb.toString();
    }

    //Load Level from csv 
    public static levelSystem loadFromCSV(String path, int levelID) throws IOException{
        List<int[]> rows = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            String line;

            while((line = br.readLine()) != null){
                line = line.trim();
                if(line.isEmpty()) continue;
                String[] parts = line.split(",");
                int[] row = new int[parts.length];

                for(int i = 0; i < parts.length; i++){
                    row[i] = Integer.parseInt(parts[i]);
                }
                rows.add(row);
            }
        }
        if(rows.isEmpty()){
            throw new IOException("CSV IS EMPTY: " + path);
        }

        int height = rows.size();
        int width = rows.get(0).length;


        //Init level
        levelSystem level = new levelSystem();
        level.levelID = levelID;
        level.width = width;
        level.height = height;
        level.tiles = new Tile[height][width];


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int type = rows.get(y)[x];

                level.tiles[y][x] = new Tile(type);

                if (type == TILE_SPAWN) {
                    level.spawnPoints.add(new SpawnPoint(x, y));
                }
            }
        }
        return level;
    }
    //Load Level
    public void loadLevel(int id, int[][] tileMap){
        this.levelID = id;
        this.width = tileMap[0].length;
        this.height = tileMap.length;

        tiles = new Tile[height][width];
        spawnPoints.clear();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int type = tileMap[y][x];
                tiles[y][x] = new Tile(type);
                if(type == TILE_SPAWN){
                    spawnPoints.add(new SpawnPoint(x, y));
                }
            }
        }

    }

    public int getLevelId(){
        return levelID;
    }

    //Collisions
    public boolean isWalkable(float x, float y){
        Tile tile = getTileFromLevel(x,y);
        if(tile == null) return true;

        return tile.isWalkable();
    }
    public boolean isSolid(float x,float y){
        Tile tile = getTileFromLevel(x,y);
        if(tile == null) return false;

        return tile.isSolid();
    }

    //Bullet Logic
    public void handleBulletHit(float x,float y){
        int tx = (int)Math.floor(x / SPRITE_SIZE);
        int ty = (int)Math.floor(y / SPRITE_SIZE);

        if(!inBounds(ty,tx)) return;
    }

    //Tile Logic
    private Tile getTileFromLevel(float lx, float ly) {
        int tx = (int)Math.floor(lx /SPRITE_SIZE);
        int ty = (int)Math.floor(ly / SPRITE_SIZE);
        return getTile(tx, ty);
    }

    private boolean inBounds(int x, int y){
        return x>=0 && y>=0 && x < width && y < height;
    }

    private Tile getTile(int tx, int ty) {
    if (tx < 0 || ty < 0 || tx >= width || ty >= height) {
        return null;
    }
    return tiles[ty][tx];
    }

    //Spawn Logic
    public SpawnPoint getSpawnPoint(int index){
        if(spawnPoints.isEmpty()) return null;
        return spawnPoints.get(index);
    }


    public static class Tile{
        public int type;

        public Tile(int type){
            this.type = type;
        }

        public boolean isWalkable(){
            return type == TILE_EMPTY || type == TILE_SPAWN;
        }

        public boolean isSolid(){
            return type == TILE_WALL || type == TILE_BREAKABLE;
        }
    }

    public static class SpawnPoint{
        public final int x;
        public final int y;

        public SpawnPoint(int x, int y){
            this.x = x;
            this.y = y;
        }
    } 
}