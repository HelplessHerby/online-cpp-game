public class Node {
    public int x,y;
    public Node parent;
    public int gCost,hCost;

    public Node(int x,int y){
        this.x = x;
        this.y = y;
    }

    public int f(){
        return gCost+hCost;
    }
}
