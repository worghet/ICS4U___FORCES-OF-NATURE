package player;

import java.util.ArrayList;

public class Player {


    // == CLASS / TRACKER VARIABLES (+ RELATED METHODS) ========


    private static int numUsers = 0;

    public static void resetPlayerCount() {
        numUsers = 0;
    }

    public static int getPlayerCount() {
        return numUsers;
    }


    // == CONSTANTS ============================================


    // Constants for position and velocity.
    protected static final int X = 0;
    protected static final int Y = 1;


    // == PLAYER RELATED DATA ==================================


    protected String username;
    protected int id;
    protected int lives;
    protected int health;
    protected int maxHealth;
    protected double speedMultiplier;
    protected PlayerAction latestActionPerformed = new PlayerAction();


    // == CONTROL-BASED VARIABLES ==============================


    // Default player stats; are configured in each character individually.
    // TODO: These are NOT all static. Configure them and update in each character's class accordingly.
    // TODO: I think protected is unnecessary here.

    // Physical constants (static final); might be moved to Map object.
    protected static final double GRAVITY = 0.55;
    protected static final double ACCELERATION = 0.5;
    protected double[] position;
    protected double[] velocity;
    protected boolean direction; // If you insist on using direction instead of velocity, make constants LEFT = false, RIGHT = true

    // Default movement variables (not crouched or jumped).
    protected static final int MAX_VELOCITY = 10;
    protected static double FRICTION = 0.55;

    // Default crouched variables (active when crouched).
    protected static final double CROUCH_FRICTION = 0.2;
    protected static final int MAX_CROUCH_VELOCITY = 4;
    protected static final int CROUCH_GRAVITY = 15;
    protected boolean isCrouching;

    // Default jump-related variables.
    protected static int JUMP_FORCE = 15;
    protected static double AIR_FRICTION = 0.97;
    protected static int MAX_CONSECUTIVE_JUMPS = 3;
    protected boolean previouslyJumped = false;
    protected boolean isJumping;
    protected int numJumps; // jumps remaining

    // Default attack-based variables.
    protected static final int MAX_PROJECTILE_COOLDOWN = 10;
    protected static final int MAX_MELEE_COOLDOWN = 2;
    protected static final int DEFAULT_MELEE_WIDTH = 30; //default hitbox width for melee attacks
    protected static final int DEFAULT_MELEE_HEIGHT = 20; //could be changed depending on the character
    protected static final int DEFAULT_MELEE_DAMAGE = 10; //all constant for now
    protected int projectileCooldown; //only applies to projectile attacks
    protected int meleeCooldown; //only applies to melee attacks
    protected boolean isAttacking;


    // == CONSTRUCTOR ==========================================


    // Character classes will override this constructor,
    // and manipulate the movement / damage variables.
    // THIS CONSTRUCTS A DEFAULT CHARACTER.
    public Player(String username) {

        // Increment player count, set ID.
        numUsers++;
        id = numUsers;

        // Set username.
        this.username = username;

        // TODO: note that here on out, the variables do not technically have to be initialized in the constructor; they can be initialized where they are declared; they may be changed in overriden constructors.

        // Initialize position; will set to random when map loads.
        position = new double[] {0, 0};
        velocity = new double[] {0, 0};

        // Vital variables.
        lives = 3;
        health = 100;

        // Movement-based variables
        numJumps = 3;
        isCrouching = false;
        isJumping = false;
        isAttacking = false;
        speedMultiplier = 1.0;
        direction = true;

        // Attack-based variables.
        projectileCooldown = 0;
        meleeCooldown = 0;
    }


    // == GETTER AND SETTERS ===================================


    public PlayerAction getLatestActionPerformed() {
        return latestActionPerformed;
    }

    public void setLatestPlayerActionPerformed(PlayerAction playerAction) {
        latestActionPerformed = playerAction;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public double[] getPosition() {
        return position;
    }
    public double[] getVelocity() {
        return velocity;
    }
    public int getHealth() {
        return health;
    }
    public int getLives() {
        return lives;
    }
    public int getNumJumps() {
        return numJumps;
    }
    public boolean getIsCrouching() {
        return isCrouching;
    }
    public boolean getIsJumping() {
        return isJumping;
    }
    public boolean getIsAttacking() {
        return isAttacking;
    }
    public int getProjectileCooldown() {
        return projectileCooldown;
    }
    public int getMeleeCooldown() {
        return meleeCooldown;
    }
    public boolean getDirection() {
        return direction;
    }
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    public void setPosition(double[] position) {

        // TODO reconfigure for map oriented collisions

        if (position[Y] > 400) {
            position[Y] = 400;
        } else if (position[X] < 0) {
            position[X] = 0;
        }
//        } if (position[X] > MAX_X_POSITION) {
//            position[X] = MAX_X_POSITION;
//        }
        // this.position = position;
    }
    public void capVelocity() {
        if (isCrouching) {
            if (velocity[X] > MAX_CROUCH_VELOCITY) {
                velocity[X] = MAX_CROUCH_VELOCITY;
            } else if (velocity[X] < -MAX_CROUCH_VELOCITY) {
                velocity[X] = -MAX_CROUCH_VELOCITY;
            }

//            COMMENTED THIS: IT PREVENTS "SLAMS"
//            if (velocity[Y] > MAX_CROUCH_VELOCITY) {
//                velocity[Y] = MAX_CROUCH_VELOCITY;
//            } else if (velocity[Y] < -MAX_CROUCH_VELOCITY) {
//                velocity[Y] = -MAX_CROUCH_VELOCITY;
//            }
        } else {
            if (velocity[X] > MAX_VELOCITY) {
                velocity[X] = MAX_VELOCITY;
            } else if (velocity[X] < -MAX_VELOCITY) {
                velocity[X] = -MAX_VELOCITY;
            }
            // CHANGED - else if is not suitable in combining these velocities.
            // we check X velocities and Y velocities individually.
            if (velocity[Y] > MAX_VELOCITY) {
                velocity[Y] = MAX_VELOCITY;
            } else if (velocity[Y] < -MAX_VELOCITY) {
                velocity[Y] = -MAX_VELOCITY;
            }
        }
    }
    public void setHealth(int health) {
        if (health > maxHealth) {
            health = maxHealth;
        } else if (health < 0) {
            health = 0;
        }
        this.health = health;
    }
    public void setLives(int lives) {
        if (lives < 0) {
            lives = 0;
        } else if (lives > 3) {
            lives = 3;
        }
        this.lives = lives;
    }
    public void setNumJumps(int numJumps) {
        if (numJumps < 0) {
            numJumps = 0;
        } else if (numJumps > 3) {
            numJumps = 3;
        }
        this.numJumps = numJumps;
    }
    public void setCrouching(boolean isCrouching) {
        this.isCrouching = isCrouching;
    }
    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }
    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }
    public void setProjectileCooldown(int projectileCooldown) {
        if (projectileCooldown < 0) {
            projectileCooldown = 0;
        } else if (projectileCooldown > MAX_PROJECTILE_COOLDOWN) {
            projectileCooldown = MAX_PROJECTILE_COOLDOWN;
        }
        this.projectileCooldown = projectileCooldown;
    }
    public void setMeleeCooldown(int meleeCooldown) {
        if (meleeCooldown < 0) {
            meleeCooldown = 0;
        } else if (meleeCooldown > MAX_MELEE_COOLDOWN) {
            meleeCooldown = MAX_MELEE_COOLDOWN;
        }
        this.meleeCooldown = meleeCooldown;
    }
    public  void setDirection(boolean direction) {
        this.direction = direction;
    }
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    // Methods
    public void moveLeft() {
        this.velocity[X] -= ACCELERATION * speedMultiplier;
        this.direction = false;
    }
    public void moveRight() {
        this.velocity[X] += ACCELERATION * speedMultiplier;
        this.direction = true;
    }
    public void crouch() {
        isCrouching = true;
    }
    public void jump() {
        if(numJumps > 0) {
            this.velocity[Y] = -JUMP_FORCE;
            this.numJumps--;
        }
        isJumping = true;
    }
    public void takeDamage(int damage) {
        this.health -= damage;
        if(this.health <= 0) {
            this.health = 0;
            this.lives--;
        }
    }
    public boolean isDead() {
        return this.lives <= 0;
    }
    public void checkLanded() { // checks for ground collision

        // TODO reconfig ground

        if (this.position[Y] >= 400) {
            this.position[Y] = 400;
            this.velocity[Y] = 0;
            this.numJumps = MAX_CONSECUTIVE_JUMPS; // reset jumps when on the ground
            isJumping = false;
        }
    }
    public void reset() {
        this.position = new double[]{500, 0};
        this.velocity = new double[]{0, 0};
        this.health = 100;
        this.numJumps = MAX_CONSECUTIVE_JUMPS;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.projectileCooldown = 0;
        this.meleeCooldown = 0;
        this.direction = true;
    }
    public void meleeAttack(ArrayList<Player> players) {
        if (!isAttacking && meleeCooldown <= 0) { //ensure the player isn't already attacking
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
    }
    public void projectileAttack(ArrayList<Projectile> projectiles) {
        if (!isAttacking && projectileCooldown <= 0) {
            this.isAttacking = true;
            this.projectileCooldown = MAX_PROJECTILE_COOLDOWN;
            System.out.println("Default Projectile Attack!");
            Projectile projectile = new Projectile(this, 5, 10, "projectile");
            projectiles.add(projectile);
        }
    }
    public void updatePosition() {
        boolean[] keys_pressed = latestActionPerformed.getKeys_Pressed();

        // -- VELOCITY ------------------------------
        if (keys_pressed[PlayerAction.LEFT]) {
            moveLeft();  // Or apply acceleration
        }
        if (keys_pressed[PlayerAction.RIGHT]) {
            moveRight();  // Or apply acceleration
        }
        if (keys_pressed[PlayerAction.UP]) {
            // check that person is not holding but just jumping (check previous input)
            if (!previouslyJumped) {
                jump();
                previouslyJumped = true;
            }

        }
        else {
            previouslyJumped = false;
        }

        isCrouching = keys_pressed[PlayerAction.DOWN];

        // -- GRAVITY --------------------------------------

        if (isCrouching) {
            velocity[Y] += CROUCH_GRAVITY;  // Apply crouch gravity

        } else {
            velocity[Y] += GRAVITY;  // Apply normal gravity
        }

        // -- FRICTION -----------------------

        if (isJumping) {
            if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {
                velocity[X] *= AIR_FRICTION;
            }
        }
        else if (isCrouching) {
            if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {
                velocity[X] *= CROUCH_FRICTION;  // Apply crouch friction
            }
        }
        else {
            if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {
                velocity[X] *= FRICTION;  // Apply crouch friction
            }
        }


        // -- UPDATE POSITION THROUGH VELOCITY ------------

        position[X] += velocity[X];
        position[Y] += velocity[Y];

        capVelocity();

        // Check if player has landed
        checkLanded();

        // Convert to integer values for rendering
//        int pixelX = Math.round((float) position[X]);
//        int pixelY = Math.round((float) position[Y]);

        // Update the player's render position (use pixelX, pixelY in your render function)
//        renderCharacter(pixelX, pixelY);

        // Update attack cooldown
        if (this.projectileCooldown > 0 || this.meleeCooldown > 0) {
            this.meleeCooldown--;
            this.projectileCooldown--;
        }

        // Reset attacking state after attack is complete
        if (this.isAttacking && this.projectileCooldown <= 0 && this.meleeCooldown <= 0) {
            this.isAttacking = false;
        }
    }

//    public void TESTING_ONLY_moveRight() {
//        position[X] += 2;
//    }

}
