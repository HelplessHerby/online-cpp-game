public class PlayerManagement{
    public float x ;
    public float y ;
    public float width = 32f;
    public float height = 32f;

    public float nextX;
    public float nextY;

    public float xVel = 0;
    public float yVel = 0;
    public String id;

    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public int isAlive = 1;
    public boolean shooting;
    public boolean spawned = false;
    public float rot = 0;
    public float rotationSpeed = 360f;
    public float barrelRot = 0;
    float acceleration = 0.5f;
    float maxSpeed = 10f;
	float drag = 0.9f;

    private long lastShotTime = 0;
    private static final long FIRE_RATE = 500;


    public void applyInput(
        boolean moveUp, 
        boolean moveDown, 
        boolean turnLeft,
        boolean turnRight,
        boolean shooting, 
        float barrelRot)
    {
        this.up = moveUp;
        this.down = moveDown;
        this.left = turnLeft;
        this.right = turnRight;
        this.barrelRot = barrelRot;
        this.shooting = shooting;
    }
    
    public void spawnPlayer(float spawnX, float spawnY ){
    if (!spawned) { 
        this.x = spawnX;
        this.y = spawnY;
        this.nextX = spawnX;
        this.nextY = spawnY;
        this.xVel = 0;
        this.yVel = 0;
        this.spawned = true;
    }
    }
    public void movement(){
        double rad = rot * (3.14 / 180.0f);
        if (left) rot -= rotationSpeed * 0.01f;
        if (right) rot += rotationSpeed * 0.01f;
        if (up) {
            xVel += Math.sin(rad) * acceleration ;
            yVel -= Math.cos(rad) * acceleration ;
        }
        if (down) {
            xVel -= Math.sin(rad) * acceleration;
            yVel += Math.cos(rad) * acceleration;
        }

        if (xVel > maxSpeed) xVel = maxSpeed;
        if (yVel > maxSpeed) yVel = maxSpeed;
        if (xVel < -maxSpeed) xVel = -maxSpeed;
        if (yVel < -maxSpeed) yVel = -maxSpeed;

        if (Math.abs(xVel) > 0.1f) xVel *= drag; else xVel = 0;
        if (Math.abs(yVel) > 0.1f) yVel *= drag; else yVel = 0;


        nextX = x + xVel;
        nextY = y + yVel;
    }
    public boolean Shoot(String thisID){
        if(shooting){
            long now = System.currentTimeMillis();
            if (now - lastShotTime < FIRE_RATE) {
                return false;
            }
            lastShotTime = now;
            return true;
        }
        return false;
    }

    public void doMove(levelSystem level) {
        float newX = x + xVel;
        if (!collides(level, newX, y)) {
            x = newX;
        } else {
            xVel = 0;
        }

        float newY = y + yVel;
        if (!collides(level, x, newY)) {
            y = newY;
        } else {
            yVel = 0;
        }

    }

    public void dontMove(){
        nextX = x;
        nextY = y;
        xVel = 0;
        yVel = 0;
        
    }

    private boolean collides(levelSystem level, float px, float py) {
    float left = px;
    float right = px + width;
    float top = py;
    float bottom = py + height;

    return level.isSolid(left,  top)    
        || level.isSolid(right, top)   
        || level.isSolid(left,  bottom) 
        || level.isSolid(right, bottom);
    }
}