package player;

import java.util.ArrayList;


public class Projectile {
    private final Player player; //player that shot the projectile
    private double[] position;
    private int[] velocity;
    private int speed;
    private boolean direction;//true = right, false = left
    private int damage;
    private String type;
    private boolean isActive;
//<<<<<<< HEAD
//
//=======
//>>>>>>> 39f4fc30d0e0d4164f93f559fcf6113aa7829aa4

    public Projectile(Player player, int speed, int damage, String type) {
        this.player = player;
        this.position = player.getPosition();
        this.direction = player.getDirection();
        this.speed = speed;
        this.velocity = new int[] {direction ? speed : -speed, 0};
        this.damage = damage;
        this.type = type;
        this.isActive = true;
        player.setProjectileCooldown(Player.MAX_PROJECTILE_COOLDOWN);
    }

    public void somethingHit(ArrayList<Player> players) { //check if projectile hit a player
        for(Player temp : players){
            double[] tempPos = temp.getPosition();
            double projectileRadius = 5; //hitbox radius for the projectile
            double tempRadius = 10; //hitbox radius for the player
            double distance = Math.sqrt(Math.pow(tempPos[0] - this.position[0], 2) + Math.pow(tempPos[1] - this.position[1], 2)); //find distance between projectile and player
            if(distance < (projectileRadius + tempRadius)) { //if hitboxes overlap
                temp.takeDamage(damage); //receive if damage hit
                isActive = false; //projectile is no longer active
                break; //leave loop since projectile can only hit one player
//<<<<<<< HEAD
//                Player.Projectile.this.active = false;
//
//=======
//>>>>>>> 39f4fc30d0e0d4164f93f559fcf6113aa7829aa4
            }
            
        }
    }

    public void updateProjectile(ArrayList<Player> players) {
        position[0] += velocity[0];
        position[1] += velocity[1];
        player.setProjectileCooldown(player.getProjectileCooldown() - 1);

        somethingHit(players);
        //deactivate projectile if it goes off-screen
        if (position[0] < 0 || position[0] > Player.MAX_X_POSITION || position[1] < 0 || position[1] > Player.GROUNDY) {
            isActive = false;
        }

        if(player.getProjectileCooldown() <= 0) {
            isActive = false;
        }
    }

    //gettes & setters
    public double[] getPosition() {
        return position;
    }
    
    public void setPosition(double[] position) {
        this.position = position;
    }
    
    public int[] getVelocity() {
        return velocity;
    }

    public void setVelocity(int[] velocity) {
        this.velocity = velocity;
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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
  
    
    
}//end of Player.Projectile Class
