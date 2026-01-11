public class bulletManagement {
    public float x,y;
    public float velX,velY;
    public float angle;
    public String ownerID;
    public float id;
    public boolean isAlive = true;

    public void spawn(float x,float y, float angle){
        float rad = (float)Math.toRadians(angle);
        float speed = 5f;
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.velX = (float)Math.sin(rad) * speed;
        this.velY = (float)-Math.cos(rad) * speed;
        this.isAlive = true;
    }
    public void update(levelSystem level){
        if(!isAlive) return;
        float nextX = x +velX;
        float nextY = y + velY;

        if(level.isSolid(nextX, nextY)){
            isAlive = false;
            level.handleBulletHit(nextX, nextY);
            return;
        }
        x = nextX;
        y = nextY;
    }
}
