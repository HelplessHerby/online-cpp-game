public class PlayerManagement{
    public float x = 200;
    public float y = 200;

    public float nextX;
    public float nextY;

    public float xVel = 0;
    public float yVel = 0;

    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public float rot = 0;
    public float rotationSpeed = 360f;
    public float barrelRot = 0;
    float acceleration = 0.5f;
    float maxSpeed = 10f;
	float drag = 0.9f;

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

    public void doMove(){
        x = x += xVel;
        y = y += yVel;
    }

    public void dontMove(){
        nextX = x;
        nextY = y;
        xVel = 0;
        yVel = 0;
    }
}