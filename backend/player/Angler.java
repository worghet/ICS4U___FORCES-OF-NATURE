package player;

// == ANGLER | PLAYER ==============
public class Angler extends Player {

    // Constructor.

    public Angler(Player player){

        // Use Player constructor.

        super();

        // Keep casting integrity.

        id = player.getId();
        username = player.getUsername();

        // Change health stats.

        this.maxHealth = 90; // Lower than base player.
        this.health = maxHealth;

        // Change movement variables.

        this.speedMultiplier = 3; // Higher than base player.
        MAX_X_VELOCITY = 14; // Faster.
        MAX_Y_VELOCITY = 12; // Faster.
        JUMP_FORCE = 14; // Higher jump.
        FRICTION = 0.9; // More slippery.
        CROUCH_FRICTION = 0.95; // Faster.
        MAX_CROUCH_VELOCITY = 25; // Faster (more like sliding).

        // Character identifier (used in javascript).

        colour = "aqua";

        // Weaker, but faster hitting.

        MAX_MELEE_COOLDOWN = 7;
        DEFAULT_MELEE_DAMAGE = 10;

    }

    // Caster.

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

    // @Override
    // public void projectileAttack(ArrayList<Projectile> projectiles) {
    //     if (!isAttacking && projectileCooldown <= 0) {
    //         this.isAttacking = true;
    //         this.projectileCooldown = MAX_PROJECTILE_COOLDOWN;
    //         System.out.println("Angler Projectile Attack!");
    //         Projectile projectile = new Projectile(this, 5, 10, "Waterball");
    //         //change frames to be waterball
    //         projectiles.add(projectile);
    //     }
    // }
    
}
