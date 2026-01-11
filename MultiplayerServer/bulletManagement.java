public class bulletManagement {
    public float x,y;
    public float velX,velY;
    public float angle;
    public String ownerID;
    public float id;
    public boolean isAlive = true;

    public int bouncesLeft = 3;
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
}
