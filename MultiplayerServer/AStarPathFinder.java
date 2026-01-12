import java.util.*;


public class AStarPathFinder{
    private final levelSystem level;

    public AStarPathFinder(levelSystem level){
        this.level = level;
    }

    public List<Node> findPath(int sx,int sy,int gx, int gy, levelSystem level){
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparing(Node::f));
        
        Map<String,Node> allNodes = new HashMap<>();
        
        Set<String> closed = new HashSet<>();
        
        Node start = new Node(sx,sy);
        start.gCost = 0;
        start.hCost = heuristic(sx, sy,gx,gy);
        open.add(start);

        allNodes.put(xyStr(sx,sy), start);


        while(!open.isEmpty()){
            Node current = open.poll();

            if(current.x == gx && current.y == gy){
                return reconstructPath(current);
            }

            closed.add(current.x + "," + current.y);

            for(int[] dir : directions()){
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                String nXYStr = xyStr(nx, ny);
                if(closed.contains(nXYStr)) continue;

                if(!(nx == gx && ny == gy) && !level.isWalkable(nx, ny)) continue;

                int tentG = current.gCost +1;

                Node neighbour = allNodes.get(nXYStr);
                if(neighbour == null){
                    neighbour = new Node(nx,ny);
                    allNodes.put(nXYStr,neighbour);
                }
                if(neighbour.parent == null || tentG < neighbour.gCost){
                    neighbour.parent = current;
                    neighbour.gCost = tentG;
                    neighbour.hCost = heuristic(nx, ny,gx,gy);

                    if(!open.contains(neighbour)){
                        open.add(neighbour);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
    private int heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }
    private String xyStr(int x, int y){
        return x + "," + y;
    }
    private List<Node> reconstructPath(Node end){
    List<Node> path = new ArrayList<>();
    Node current = end;
    while(current != null){
        path.add(current);
        current = current.parent;
    }
    Collections.reverse(path);
    return path;
    }
    private int[][] directions(){
        return new int[][]{
            {1,0}, //right
            {-1,0}, //Left
            {0,1},//Down
            {0,-1}//Up
        };
    }
}