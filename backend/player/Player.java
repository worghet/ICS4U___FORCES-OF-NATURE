package player;

// == IMPORTS =============
import game.Game;
import game.GameMap;
import game.GameServer;
import game.Island;

import java.util.ArrayList;

// == PLAYER ========
public class Player {


    // == CLASS / TRACKER VARIABLES (+ RELATED METHODS) ========


    private static int numPlayers = 0;

    public static void resetPlayerCount() {
        numPlayers = 0;
    }

    public static int getPlayerCount() {
        return numPlayers;
    }


    // == CONSTANTS ============================================


    // Constants for position and velocity.
    protected static final int X = 0;
    protected static final int Y = 1;


    // == PLAYER RELATED DATA ==================================


    // temporary
    String colour;

    //to keep track of where in the animation it is (ex: 3 running frames)
    protected int animationFrame = 1;

    protected boolean isSpectating;
    protected boolean isReady;
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
    protected boolean direction;

    // Default movement variables (not crouched or jumped).
    protected int MAX_X_VELOCITY;
    protected double FRICTION;

    // Default crouched variables (active when crouched).
    protected double CROUCH_FRICTION;
    protected int MAX_CROUCH_VELOCITY;
    protected static final int CROUCH_GRAVITY = 7;
    protected boolean isCrouching;

    // Default jump-related variables.
    protected int JUMP_FORCE;
    protected static double AIR_FRICTION = 0.97;
    protected double MAX_Y_VELOCITY;
    protected int MAX_CONSECUTIVE_JUMPS;
    protected boolean previouslyJumped = false;
    protected boolean isJumping;
    protected int numJumpsRemaining; // jumps remaining

    // Default attack-based variables.
    protected int MAX_PROJECTILE_COOLDOWN;
    protected  int MAX_MELEE_COOLDOWN;
    protected  int DEFAULT_MELEE_WIDTH; //default hitbox width for melee attacks
    protected  int DEFAULT_MELEE_HEIGHT; //could be changed depending on the character
    protected  int DEFAULT_MELEE_DAMAGE; //all constant for now
    protected boolean previouslyPressedMeleeAttack = false;
    protected int projectileCooldown; //only applies to projectile attacks
    protected int meleeCooldown; //only applies to melee attacks
    protected boolean isAttacking;
    protected int damageDealt;


    // == CONSTRUCTOR ==========================================


    // Character classes will override this constructor,
    // and manipulate the movement / damage variables.
    // THIS CONSTRUCTS A DEFAULT CHARACTER.
    public Player(String username) {

        // Increment player count, set ID.
        numPlayers++;
        id = numPlayers;

        // Set username.
        this.username = username;

        MAX_X_VELOCITY = 10;
        MAX_Y_VELOCITY = 10;
        FRICTION = 0.55;
        CROUCH_FRICTION = 0.2;
        MAX_CROUCH_VELOCITY = 4;
        JUMP_FORCE = 15;
        MAX_CONSECUTIVE_JUMPS = 3;


        MAX_PROJECTILE_COOLDOWN = 10;
        MAX_MELEE_COOLDOWN = 10;
        DEFAULT_MELEE_WIDTH = 130;
        DEFAULT_MELEE_HEIGHT = 20;
        DEFAULT_MELEE_DAMAGE = 15;
        isSpectating = false;
        isReady = false;

        // TECHNICALLY WHAT IS BELOW IS NOT NEEDED..

        colour = "grey";

        // TODO: note that here on out, the variables do not technically have to be initialized in the constructor; they can be initialized where they are declared; they may be changed in overriden constructors.

        // Initialize position; will set to random when map loads.
        position = new double[] {0, 0};
        velocity = new double[] {0, 0};

        // Vital variables.
        lives = 3;
        health = 60;
        maxHealth = 60;

        // Movement-based variables
        numJumpsRemaining = 3;
        isCrouching = false;
        isJumping = false;
        isAttacking = false;
        speedMultiplier = 1;
        direction = true;

        // Attack-based variables.
        projectileCooldown = 0;
        meleeCooldown = 0;
        damageDealt = 0;
    }

    public Player() {

        MAX_X_VELOCITY = 10;
        MAX_Y_VELOCITY = 10;
        FRICTION = 0.55;
        CROUCH_FRICTION = 0.2;
        MAX_CROUCH_VELOCITY = 4;
        JUMP_FORCE = 15;
        MAX_CONSECUTIVE_JUMPS = 3;
        MAX_PROJECTILE_COOLDOWN = 10;
        MAX_MELEE_COOLDOWN = 10;
        DEFAULT_MELEE_WIDTH = 100;
        DEFAULT_MELEE_HEIGHT = 20;
        DEFAULT_MELEE_DAMAGE = 15;
        isSpectating = false;
        isReady = false;

        // TODO: note that here on out, the variables do not technically have to be initialized in the constructor; they can be initialized where they are declared; they may be changed in overriden constructors.

        // Initialize position; will set to random when map loads.
        position = new double[] {0, 0};
        velocity = new double[] {0, 0};

        // Vital variables.
        lives = 3;
        health = 60;
        maxHealth = 60;

        // Movement-based variables
        numJumpsRemaining = 3;
        isCrouching = false;
        isJumping = false;
        isAttacking = false;
        speedMultiplier = 1;
        direction = true;

        // Attack-based variables.
        projectileCooldown = 0;
        meleeCooldown = 0;
        damageDealt = 0;
    }

    // == UPDATER METHOD =======================================

    public void toggleSpectating() {
        isSpectating = true;
    }

    public void toggleReady() {
        isReady = !isReady;
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isSpecator() {
        return isSpectating;
    }

    public void updatePosition() {

        // Save the latest action performed by the player (to not be suddenly changed).

        boolean[] keys_pressed = latestActionPerformed.getKeysPressed();

        // Increment X velocity based on left/right keys being pressed.

        if (keys_pressed[PlayerAction.LEFT]) {
            moveLeft();  // Or apply acceleration
        }
        if (keys_pressed[PlayerAction.RIGHT]) {
            moveRight();  // Or apply acceleration
        }

        // Register jumps (track if had jumped; counter and whatnot).

        if (keys_pressed[PlayerAction.UP]) {

            // Check that person is not holding but just jumping (check previous input).

            if (!previouslyJumped) {
                jump();
                previouslyJumped = true;
            }

        }
        else {
            previouslyJumped = false;
        }

        // Set isCrouching variable based on the player's input.

        isCrouching = keys_pressed[PlayerAction.DOWN];

        // Apply gravity based on user's movement state (crouched or standing or air).

//        if (isJumping) {

            if (isCrouching) {
                velocity[Y] += CROUCH_GRAVITY;

            } else {
                velocity[Y] += GRAVITY;
            }
//        }

        // Apply friction (again, based on movement state).

        if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {

            // Air resistance - least friction.
            if (isJumping) {
                velocity[X] *= AIR_FRICTION;
            }

            // Crouching - higher friction.
            else if (isCrouching) {
                velocity[X] *= CROUCH_FRICTION;
            }

            // Just standing - standard friction.
            else {
                velocity[X] *= FRICTION;  // Apply crouch friction
            }
        }

        // Cap the velocity to the maximum variables set for the player.

        capVelocity();

        // Update the actual position with reference to now changed velocity.

        position[X] += velocity[X];
        position[Y] += velocity[Y];

        // Check if player has landed
        // TODO: Check collisions from game (where we can pass map through).

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

    public void registerDamage(ArrayList<Player> players) {
        if (latestActionPerformed.getKeysPressed()[PlayerAction.MELEE]) {
            if (!previouslyPressedMeleeAttack) {
                meleeAttack(players);
                previouslyPressedMeleeAttack = true;

            }
//            System.out.println("called melee in updatePlayers");
        }
        else {
            previouslyPressedMeleeAttack = false;
        }
    }

    // == MOVEMENT / ATTACK METHODS ============================


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
        if(numJumpsRemaining > 0) {
            this.velocity[Y] = -JUMP_FORCE;
            this.numJumpsRemaining--;
        }
        isJumping = true;
    }

    public void takeDamage(int damage) {

        if (health - damage <= 0) {
            health = 0;
        }
        else {
            health -= damage;
        }

    }

    public boolean isDead() {
        return lives == 0;
    }

    public void reset() {
        this.position = new double[]{500, 0};
        this.velocity = new double[]{0, 0};
        this.health = 100;
        this.numJumpsRemaining = MAX_CONSECUTIVE_JUMPS;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.projectileCooldown = 0;
        this.meleeCooldown = 0;
        this.direction = true;
        this.lives = 3;
        this.damageDealt = 0;
    }

    public void respawn() {
        this.position = new double[]{500, 0};
        this.velocity = new double[]{0, 0};
        this.health = maxHealth;
        this.numJumpsRemaining = MAX_CONSECUTIVE_JUMPS;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.projectileCooldown = 0;
        this.meleeCooldown = 0;
        this.direction = true;
    }

    public void meleeAttack(ArrayList<Player> players) {
        if (!isAttacking && meleeCooldown >= 0) { //ensure the player isn't already attacking
            this.isAttacking = true;
            this.meleeCooldown = MAX_MELEE_COOLDOWN;
//            System.out.println("Melee Attack!");

            //calculate the attack area based on the player's direction
            double attackStartX = position[X]; //start of the attack hitbox(X position)
            double attackEndX = position[X] + (direction ? DEFAULT_MELEE_WIDTH : -DEFAULT_MELEE_WIDTH); //end of the attack hitbox(X position)

//            if (direction) {
//                System.out.println("movin right");
//                position[X] += ((double) DEFAULT_MELEE_WIDTH / 2); // move player forward a bit
////                velocity[X] += 20;
//            }
//            else {
//                System.out.println("movin left");
////                velocity[X] -= 20;
//                position[X] -=z ((double) DEFAULT_MELEE_WIDTH / 2); // move player forward a bit
//
//            }

            for (Player temp : players) {
                if (temp != this) { //ensure the player isn't attacking themselves
                    double[] tempPos = temp.getPosition();
                    boolean withinHorizontalRange = (direction && tempPos[X] >= attackStartX && tempPos[X] <= attackEndX) || (!direction && tempPos[X] <= attackStartX && tempPos[X] >= attackEndX); //first check for if facing right, then if facing left
                    boolean withinVerticalRange = Math.abs(tempPos[Y] - position[Y]) < DEFAULT_MELEE_HEIGHT; //vertical check

                    if (withinHorizontalRange && withinVerticalRange) {
                        temp.takeDamage(DEFAULT_MELEE_DAMAGE); //deal damage to the player if they are within the attack range
                        this.damageDealt += 10;
//                        System.out.println("Hit " + temp.getUsername() + " for 10 damage!");

                        if (temp.getHealth() == 0) {
                            temp.setLives(temp.getLives() - 1);
//                            System.out.println(temp.getUsername() + "has " + temp.getLives() + " lives");
                            if (!temp.isDead()) {
                                temp.respawn();
                            }
                            else {
                                GameServer.reportToConsole(temp.getUsername() + " HAS BEEN ELIMINATED!", GameServer.INTERESTING);
                                temp.toggleSpectating();
//                                System.out.println("this guy is spectating: " + temp.isSpectating);
                            }
                        }

                    }
                }
            }
        }
        else if (meleeCooldown == 0) {
            isAttacking = false;
        }
        else {
            meleeCooldown -= 1;
        }

    }

    public void projectileAttack(ArrayList<Projectile> projectiles) {
        if (!isAttacking && projectileCooldown <= 0) {
            this.isAttacking = true;
            this.projectileCooldown = MAX_PROJECTILE_COOLDOWN;
//            System.out.println("Default Projectile Attack!");
            Projectile projectile = new Projectile(this, 5, 20, "projectile");
            projectiles.add(projectile);
        }
    }

    public void checkMapCollisions(GameMap map) {


        // GROUND COLLISION

        if (this.position[Y] >= map.getGroundY()) { // checks if is clipping in the ground
            this.position[Y] = map.getGroundY();
            this.velocity[Y] = 0;  // Stop falling
            this.numJumpsRemaining = MAX_CONSECUTIVE_JUMPS;  // Reset jump count
            isJumping = false;  // Player is no longer in the air
        }

        if (position[X] < 0) {
            position[X] = 0;
        } else if (position[X] > 1675) {
            position[X] = 1675;
        }


        // Check for collision with islands (or any other platforms in the map)
        for (Island island : map.getIslands()) {

            // Check if the player is within the horizontal bounds of the island
            if (position[X] + 70 >= island.getTopLeftX() && position[X] <= island.getBottomRightX() && !isCrouching) {

                if (position[Y] + 70 >= island.getTopLeftY() &&  velocity[Y] > 0) {
                    // Player is falling and above the island, they should land
//                    System.out.println("Landing on the island...");

                    // Adjust Y position to the top of the island
                    position[Y] = island.getTopLeftY() - 70;  // 70 could be adjusted based on player height

                    // Stop downward velocity and prevent further falling
                    velocity[Y] = 0;

                    // Reset jump count since the player is on solid ground
                    numJumpsRemaining = MAX_CONSECUTIVE_JUMPS;
                    isJumping = false;  // Player is no longer jumping

                    // Output to confirm the player has landed on the island
//                    System.out.println("ON AN ISLAND --> " + topIslandCounter);
                    topIslandCounter++;
                } else
                {
                    if (position[Y] + 70 > island.getTopLeftY()  && position[Y] < island.getBottomRightY()) {
                        position[Y] = island.getTopLeftY() - 70;
                        velocity[Y] = 0;
                    }
                }
            }
        }
    }

    static int topIslandCounter = 0;
    static int islandCounter = 0;

    public void capVelocity() {

        // Crouching and standing/jumping have different max speeds.

        // Player is crouching
        if (isCrouching) {

            // Cap X velocity.
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
        }

        // Player is standing.
        else {

            // Cap X velocity.
            if (velocity[X] > MAX_X_VELOCITY) {
                velocity[X] = MAX_X_VELOCITY;
            }
            else if (velocity[X] < -MAX_X_VELOCITY) {
                velocity[X] = -MAX_X_VELOCITY;
            }

            // CHANGED - else if is not suitable in combining these velocities.
            // we check X velocities and Y velocities individually.

            // Cap Y velocity.
            if (velocity[Y] > MAX_Y_VELOCITY) {
                velocity[Y] = MAX_Y_VELOCITY;
            } else if (velocity[Y] < -MAX_Y_VELOCITY) {
                velocity[Y] = -MAX_Y_VELOCITY;
            }
        }
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

    public int getNumJumpsRemaining() {
        return numJumpsRemaining;
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

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean getDirection() {
        return direction;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setPosition(double[] position) {

        this.position[X] = position[X];
        this.position[Y] = position[Y];

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

    public void setNumJumpsRemaining(int numJumpsRemaining) {
        if (numJumpsRemaining < 0) {
            numJumpsRemaining = 0;
        } else if (numJumpsRemaining > 3) {
            numJumpsRemaining = 3;
        }
        this.numJumpsRemaining = numJumpsRemaining;
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

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public  void setDirection(boolean direction) {
        this.direction = direction;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public String getColour() {
        return colour;
    }

    public int getFrame(){
        return animationFrame;
    }

    public void setFrame(int frame){
        this.animationFrame = frame;
    }

}
