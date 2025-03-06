
import java.util.ArrayList;


public class Projectile {

    private int[] position;
    private int speed;
    private boolean direction;//true = right, false = left

    public Projectile(Player player){
        this.position = player.getPosition();
        this.direction = player.getDirection();
        player.setAttackCooldown(player.MAX_ATTACK_COOLDOWN);

    }

    //skeleton right now, 
    public void somethingHit(ArrayList<Player> players){
        //check for collisions with players in arraylist in Game
        for(int i = 0; i< players.size(); i++){
            Player temp = players.get(i);

            //what happens if a player is hit by projectile
            if(Projectile.this.position == temp.getPosition()){
                temp.setLives(temp.getLives()-1);
            }
            
        }
    }

    //gettes & setters
    public int[] getPosition() {
        return position;
    }
    
    public void setPosition(int[] position) {
        this.position = position;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public boolean getDirection() {
        return direction;
    }
    
    public void setDirection(boolean direction) {
        this.direction = direction;
    }
    
    public boolean getIsMoving(Player temp) {
        return (temp.getAttackCooldown() > 0);
    }
    
    
}//end of Projectile Class
