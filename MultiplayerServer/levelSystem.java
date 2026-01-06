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

    private int levelID;
    private int width;
    private int height;

    private Tile[][] tiles;

    //Dynamic Tiles
    private final List<TileChange> tileChanges = new ArrayList<>();

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
                sb.append(",").append(tiles[x][y].type);
            }
        }
        sb.append(",LEVEL_END");
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
        int height = rows.size();
        int width = rows.get(0).length;

        levelSystem level = new levelSystem();
        level.levelID = levelID;

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                level.tiles[y][x] = new Tile(rows.get(y)[x]);
            }
        }
        return level;
    }
    //Load Level
    public void loadLevel(int id, int[][] tileMap){
        this.levelID = id;
        this.width = tileMap.length;
        this.height = tileMap[0].length;

        tiles = new Tile[width][height];
        tileChanges.clear();
        spawnPoints.clear();

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                int type = tileMap[x][y];
                tiles[x][y] = new Tile(type);
                if(type == TILE_SPAWN){
                    spawnPoints.add(new SpawnPoint(x,y));
                }
            }
        }
    }

    public int getLevelId(){
        return levelID;
    }

    //Collisions
    public boolean isWalkable(float x, float y){
        Tile tile = getTileFromWorld(x,y);
        if(tile == null) return false;

        return tile.isWalkable();
    }
    public boolean isSolid(float x,float y){
        Tile tile = getTileFromWorld(x,y);
        if(tile == null) return true;

        return tile.isSolid();
    }

    //Bullet Logic
    public void handleBulletHit(float x,float y){
        int tx = (int) x;
        int ty = (int) y;

        if(!inBounds(tx,ty)) return;

        Tile tile = tiles[tx][ty];
        if(tile.type == TILE_BREAKABLE){
            tile.type = TILE_EMPTY;
            tileChanges.add(new TileChange(tx,ty,TILE_EMPTY));
        }
    }

    //Tile Logic
    private Tile getTileFromWorld(float x, float y){
        int tx = (int) x;
        int ty = (int) y;
        if(!inBounds(tx,ty)) return null;
        return tiles[tx][ty];
    }

    private boolean inBounds(int x, int y){
        return x>=0 && y>=0 && x < width && y < height;
    }

    //Spawn Logic
    public SpawnPoint getSpawnPoint(int index){
        if(spawnPoints.isEmpty()) return null;
        return spawnPoints.get(index % spawnPoints.size());
    }

    //Network Sync
    public List<TileChange> networkTileChanges(){
        List<TileChange> tc = new ArrayList<>(tileChanges);
        tileChanges.clear();
        return tc;
    }

    //Classes
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

    public static class TileChange{
        public final int x;
        public final int y;
        public final int newType;

        public TileChange(int x , int y, int newType){
            this.x = x;
            this.y = y;
            this.newType = newType;
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