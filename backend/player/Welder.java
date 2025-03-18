package player;
import java.util.ArrayList;


public class Welder extends Player {

    public Welder(Player player){
        super();
        username = player.getUsername();
        id = player.getId();
        this.maxHealth = 100;
        this.health = maxHealth;
        this.speedMultiplier = 1.0;
        colour = "red";
        //stats are all same as base player
    }

    public static Welder castTo(Player player) {
        return new Welder(player);
    }

    @Override
    public void meleeAttack(ArrayList<Player> players) {
        if (!isAttacking || meleeCooldown <= 0) { //ensure the player isn't already attacking
            this.isAttacking = true;
            this.meleeCooldown = MAX_MELEE_COOLDOWN;
            System.out.println("Melee Attack!");

            //calculate the attack area based on the player's direction
            double attackStartX = position[X]; //start of the attack hitbox(X position)
            double attackEndX = position[X] + (direction ? DEFAULT_MELEE_WIDTH : -DEFAULT_MELEE_WIDTH); //end of the attack hitbox(X position)

            for (Player temp : players) {
                if (temp != this) { //ensure the player isn't attacking themselves
                    double[] tempPos = temp.getPosition();
                    boolean withinHorizontalRange = (direction && tempPos[X] >= attackStartX && tempPos[X] <= attackEndX) || (!direction && tempPos[X] <= attackStartX && tempPos[X] >= attackEndX); //first check for if facing right, then if facing left
                    boolean withinVerticalRange = Math.abs(tempPos[Y] - position[Y]) < DEFAULT_MELEE_HEIGHT; //vertical check
                    if (withinHorizontalRange && withinVerticalRange) {
                        temp.takeDamage(10); //deal damage to the player if they are within the attack range
                        System.out.println("Hit " + temp.getUsername() + " for 10 damage!");
                    }
                }
            }
        }
    }//close attack

    @Override
    public void projectileAttack(ArrayList<Projectile> projectiles) {
        if (!isAttacking && projectileCooldown <= 0) {
            this.isAttacking = true;
            this.projectileCooldown = MAX_PROJECTILE_COOLDOWN;
            System.out.println("Welder Projectile Attack!");
            Projectile projectile = new Projectile(this, 5, 10, "Fireball");
            //change frames to be fireball
            projectiles.add(projectile);
        }
    }
}
