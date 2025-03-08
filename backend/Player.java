
public class Player {

    public static final int X = 0;
    public static final int Y = 1;
    public static final int MAX_ATTACK_COOLDOWN = 10;
    public static final int MAX_X_POSITION = 1000;
    public static final int MAX_VELOCITY = 10;
    public static final int MAX_CROUCH_VELOCITY = 4;
    public static final double CROUCH_FRICTION = 0.2;
    public static final int CROUCH_GRAVITY = 15;
    public static final double ACCELERATION = 0.5;
    protected static double FRICTION = 0.55;
    protected static double GRAVITY = 0.55;
    protected static int JUMP_FORCE = 12;
    protected static int GROUNDY = 400;
    protected static int MAX_CONSECUTIVE_JUMPS = 3;

    private static int numUsers = 0;

    private String username;
    private int id;

    private boolean previouslyJumped = false;
    private PlayerAction latestActionPerformed = new PlayerAction();

    private double[] position;
    private double[] velocity;

    private int health;
    private int lives;
    private int numJumps; // jumps remaining
    private boolean isCrouching;
    private boolean isJumping;
    private boolean isAttacking;
    private int attackCooldown;
    private boolean direction; // true for right, false for left

    public Player(int startX) {
        this.position = new double[]{startX, GROUNDY};
        this.velocity = new double[]{0, 0};
        this.health = 100;
        this.lives = 3;
        this.numJumps = 3;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.direction = true;

        numUsers++;
        id = numUsers;
    }

    public Player(String username) {
        this.username = username;
        this.position = new double[] {0, 0};
        this.velocity = new double[] {0, 0};
        this.health = 100;
        this.lives = 3;
        this.numJumps = 3;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.direction = true;


        numUsers++;
        id = numUsers;
    }

    public Player() {
        this.username = "unset";
        this.position = new double[] {0, 0};
        this.velocity = new double[] {0, 0};
        this.health = 100;
        this.lives = 3;
        this.numJumps = 3;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.direction = true;


        numUsers++;
        id = numUsers;

    }

    //getters and setters

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
    public int getAttackCooldown() {
        return attackCooldown;
    }
    public boolean getDirection() {
        return direction;
    }
    public void setPosition(double[] position) {
        if (position[Y] > GROUNDY) {
            position[Y] = GROUNDY;
        } else if (position[X] < 0) {
            position[X] = 0;
        } else if (position[X] > MAX_X_POSITION) {
            position[X] = MAX_X_POSITION;
        }
        this.position = position;
    }
    public void setVelocity() {
        if (isCrouching) {
            if (velocity[X] > MAX_CROUCH_VELOCITY) {
                velocity[X] = MAX_CROUCH_VELOCITY;
            } else if (velocity[X] < -MAX_CROUCH_VELOCITY) {
                velocity[X] = -MAX_CROUCH_VELOCITY;
            } else if (velocity[Y] > MAX_CROUCH_VELOCITY) {
                velocity[Y] = MAX_CROUCH_VELOCITY;
            } else if (velocity[Y] < -MAX_CROUCH_VELOCITY) {
                velocity[Y] = -MAX_CROUCH_VELOCITY;
            }
        } else {
            if (velocity[X] > MAX_VELOCITY) {
                velocity[X] = MAX_VELOCITY;
            } else if (velocity[X] < -MAX_VELOCITY) {
                velocity[X] = -MAX_VELOCITY;
            } else if (velocity[Y] > MAX_VELOCITY) {
                velocity[Y] = MAX_VELOCITY;
            } else if (velocity[Y] < -MAX_VELOCITY) {
                velocity[Y] = -MAX_VELOCITY;
            }
        }
        this.velocity = velocity;
    }
    public void setHealth(int health) {
        if (health > 100) {
            health = 100;
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
    public void setAttackCooldown(int attackCooldown) {
        if (attackCooldown < 0) {
            attackCooldown = 0;
        } else if (attackCooldown > MAX_ATTACK_COOLDOWN) {
            attackCooldown = MAX_ATTACK_COOLDOWN;
        }
        this.attackCooldown = attackCooldown;
    }
    public  void setDirection(boolean direction) {
        this.direction = direction;
    }

    // Methods
    public void moveLeft() {
        this.velocity[X] -= ACCELERATION;
        this.direction = false;
    }
    public void moveRight() {
        this.velocity[X] += ACCELERATION;
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
        if (this.position[Y] >= GROUNDY) {
            this.position[Y] = GROUNDY;
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
        this.attackCooldown = 0;
    }
    public void updatePosition() {
        boolean[] keys_pressed = latestActionPerformed.getKeys_Pressed();

        // -- VELOCITY ------------------------------
        if (keys_pressed[PlayerAction.LEFT]) {
            velocity[X] -= ACCELERATION;  // Or apply acceleration
        }
        if (keys_pressed[PlayerAction.RIGHT]) {
            velocity[X] += ACCELERATION;  // Or apply acceleration
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

        if (keys_pressed[PlayerAction.DOWN]) {
            isCrouching = true;
        } else {
            isCrouching = false;
        }

        // -- GRAVITY --------------------------------------
        if (isCrouching) {
            velocity[Y] += CROUCH_GRAVITY;  // Apply crouch gravity
            if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {
                velocity[X] *= CROUCH_FRICTION;  // Apply crouch friction
            }
        } else {
            velocity[Y] += GRAVITY;  // Apply normal gravity
            if (!keys_pressed[PlayerAction.LEFT] && !keys_pressed[PlayerAction.RIGHT]) {
                velocity[X] *= FRICTION;  // Apply normal friction
            }
        }
        // Update position based on velocity
        position[X] += velocity[X];
        position[Y] += velocity[Y];

        setVelocity();

        // Check if player has landed
        checkLanded();

        // Convert to integer values for rendering
//        int pixelX = Math.round((float) position[X]);
//        int pixelY = Math.round((float) position[Y]);

        // Update the player's render position (use pixelX, pixelY in your render function)
//        renderCharacter(pixelX, pixelY);

        // Update attack cooldown
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        // Reset attacking state after attack is complete
        if (this.isAttacking && this.attackCooldown <= 0) {
            this.isAttacking = false;
        }
    }

//    public void TESTING_ONLY_moveRight() {
//        position[X] += 2;
//    }

}
