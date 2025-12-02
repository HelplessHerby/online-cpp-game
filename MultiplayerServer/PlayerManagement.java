public class PlayerManagement{
    public float x = 200;
    public float y = 200;

    public float velX = 0;
    public float velY = 0;

    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public float rot = 0;

    public void applyInput(String act){
        switch(act){
            case "MOVE_UP_DOWN": up = true; break;
            case "MOVE_UP_UP": up = false; break;

            case "MOVE_DOWN_DOWN": down = true; break;
            case "MOVE_DOWN_UP": down = false; break;

            case "MOVE_LEFT_DOWN": left = true; break;
            case "MOVE_LEFT_UP": left = false; break;

            case "MOVE_RIGHT_DOWN": right = true; break;
            case "MOVE_RIGHT_UP": right = false; break;

        }
    }
}