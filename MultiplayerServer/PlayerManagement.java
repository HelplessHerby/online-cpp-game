public class PlayerManagement{
    public float x = 200;
    public float y = 200;

    public float xVel = 0;
    public float yVel = 0;

    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public float rot = 0;
    public float rotationSpeed = 360f;
    float acceleration = 0.5f;
    float maxSpeed = 5f;
	float drag = 0.9f;

    public void applyInput(String act){
        switch(act){
            case "MOVE_UP_DOWN": up = true; break;
            case "MOVE_UP_UP": up = false; break;

            case "MOVE_DOWN_DOWN": down = true; break;
            case "MOVE_DOWN_UP": down = false; break;

            case "TURN_LEFT_DOWN": left = true; break;
            case "TURN_LEFT_UP": left = false; break;

            case "TURN_RIGHT_DOWN": right = true; break;
            case "TURN_RIGHT_UP": right = false; break;

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


            x += xVel;
            y += yVel;
    }

}