package player;
import java.util.ArrayList;

public class Angler extends Player {


    public Angler(Player player){

        super();

        // keep casting integrity

        id = player.getId();
        username = player.getUsername();

        // change health stats

        this.maxHealth = 90; //lower than base player
        this.health = maxHealth;

        // movement stuffz
        this.speedMultiplier = 3; //higher than base player
        MAX_X_VELOCITY = 14;
        MAX_Y_VELOCITY = 12;
        JUMP_FORCE = 14;
        FRICTION = 0.9; // slippery
        CROUCH_FRICTION = 0.95;
        MAX_CROUCH_VELOCITY = 25;

        // sprite sheet / color change
        colour = "aqua";


        MAX_MELEE_COOLDOWN = 7;
        DEFAULT_MELEE_DAMAGE = 10;

        //quicker with less health
    }

    public static Angler castTo(Player player) {
        return new Angler(player);
    }


//    @Override
//    public void meleeAttack(ArrayList<Player> players) {
//        if (!isAttacking || meleeCooldown <= 0) { //ensure the player isn't already attacking
//            this.isAttacking = true;
//            this.meleeCooldown = MAX_MELEE_COOLDOWN;
//            System.out.println("Melee Attack!");
//
//            //calculate the attack area based on the player's direction
//            double attackStartX = position[X]; //start of the attack hitbox(X position)
//            double attackEndX = position[X] + (direction ? DEFAULT_MELEE_WIDTH : -DEFAULT_MELEE_WIDTH); //end of the attack hitbox(X position)
//
//            for (Player temp : players) {
//                if (temp != this) { //ensure the player isn't attacking themselves
//                    double[] tempPos = temp.getPosition();
//                    boolean withinHorizontalRange = (direction && tempPos[X] >= attackStartX && tempPos[X] <= attackEndX) || (!direction && tempPos[X] <= attackStartX && tempPos[X] >= attackEndX); //first check for if facing right, then if facing left
//                    boolean withinVerticalRange = Math.abs(tempPos[Y] - position[Y]) < DEFAULT_MELEE_HEIGHT; //vertical check
//                    if (withinHorizontalRange && withinVerticalRange) {
//                        temp.takeDamage(10); //deal damage to the player if they are within the attack range
//                        System.out.println("Hit " + temp.getUsername() + " for 10 damage!");
//                    }
//                }
//            }
//        }
//    }//close attack

    @Override
    public void projectileAttack(ArrayList<Projectile> projectiles) {
        if (!isAttacking && projectileCooldown <= 0) {
            this.isAttacking = true;
            this.projectileCooldown = MAX_PROJECTILE_COOLDOWN;
            System.out.println("Angler Projectile Attack!");
            Projectile projectile = new Projectile(this, 5, 10, "Waterball");
            //change frames to be waterball
            projectiles.add(projectile);
        }

    }
    
}
