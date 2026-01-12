import java.util.Map;

public class bulletManagement {
    public float x,y;
    public float velX,velY;
    public float angle;
    public String ownerID;
    public float id;
    public boolean isAlive = true;

    public int bouncesLeft = 2;
    public float time;
    public float maxLifetime = 300;

    public void spawn(float x,float y, float angle){
        float rad = (float)Math.toRadians(angle);
        float speed = 5f;
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.velX = (float)Math.sin(rad) * speed;
        this.velY = (float)-Math.cos(rad) * speed;
        this.isAlive = true;
        this.bouncesLeft = 3;
        this.time = 0;
    }
    public void update(levelSystem level){
        if(!isAlive) return;

        float nextX = x + velX;
        float nextY = y + velY;

        boolean hitX = collides(level, nextX, y);
        boolean hitY = collides(level, x, nextY);

        if(hitX || hitY){
            if(bouncesLeft > 0){
                float normalX = hitX ? -1 : 0;
                float normalY = hitY ? -1 : 0;

                float reflect = velX * normalX + velY * normalY;

                velX = velX -2 * reflect * normalX;
                velY = velY - 2 * reflect * normalY;

                bouncesLeft--;
            }else{
                isAlive = false;
                level.handleBulletHit(nextX, nextY);
                x = 0;
                y = 0;
                velX = 0;
                velY = 0;
                return;
            }
        }
        time ++;

        x += velX;
        y += velY;

        if(time > maxLifetime){
                x = 0;
                y = 0;
                velX = 0;
                velY = 0;
            isAlive = false;
        }

        angle = (float) Math.toDegrees(Math.atan2(velX, -velY));
    }
    


    private boolean collides(levelSystem level, float px, float py) {
    float left = px;
    float right = px + 32;
    float top = py;
    float bottom = py + 32;

    return level.isSolid(left,  top)    ||
           level.isSolid(right, top)    ||
           level.isSolid(left,  bottom) ||
           level.isSolid(right, bottom);
    }

    public void checkPlayerCollision(Map<String, PlayerManagement> players){
        if(!isAlive) return;

        for(PlayerManagement p : players.values()){
            if(p.isAlive == 0) continue;

            //Shoota check 🤑🤑
            if(p.id.equals(ownerID)) continue;

            if(checkIntersect(x, y, 16, p.x, p.y, 32)){
                p.isAlive = 0;
                isAlive = false;

                System.out.println("[Server] Player " + p.id + " was hit!");
                return;
            }
        }
    }

    private boolean checkIntersect(
        float ax, float ay, int aSize,
        float bx, float by, int bSize
    ){
        return ax < bx + bSize &&
               ax + aSize > bx && 
               ay < by + bSize &&
               ay + aSize > by;
    }
}
