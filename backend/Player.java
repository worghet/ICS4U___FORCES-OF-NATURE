package backend;

public class Player {
    public static final int X = 0;
    public static final int Y = 1;
    public static final int MAX_VELOCITY = 10;
    public static final int MAX_CROUCH_VELOCITY = 4;
    public static final double CROUCH_FRICTION = 0.2;
    public static final int CROUCH_GRAVITY = 15;
    public static final double ACCELERATION = 0.8;
    public static final double FRICTION = 0.55;
    public static final double GRAVITY = 0.55;
    public static final int JUMP_FORCE = 12;
    public static final int GROUNDY = 400;
    public static final int MAX_CONSECUTIVE_JUMPS = 3;
    public static final int MAX_ATTACK_COOLDOWN = 10;
    public static final int MAX_X_POSITION = 1000;

    private int[] position;
    private int[] velocity;
    private int health;
    private int lives;
    private int numJumps;
    private boolean isCrouching;
    private boolean isJumping;
    private boolean isAttacking;
    private int attackCooldown;
    private boolean direction; // true for right, false for left
    
    public Player(int startX, int startY) {
        this.position = new int[]{startX, startY};
        this.velocity = new int[]{0, 0};
        this.health = 100;
        this.lives = 3;
        this.numJumps = 3;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.direction = true;
    }

    //getters and setters
    public int[] getPosition() {
        return position;
    }
    public int[] getVelocity() {
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
    public void setPosition(int[] position) {
        if (position[Y] > GROUNDY) {
            position[Y] = GROUNDY;
        } else if (position[X] < 0) {
            position[X] = 0;
        } else if (position[X] > MAX_X_POSITION) {
            position[X] = MAX_X_POSITION;
        }
        this.position = position;
    }
    public void setVelocity(int[] velocity) {
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
        this.isCrouching = true;
    }
    public void jump() {
        if(this.numJumps > 0) {
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
        }
    }
    public void reset() {
        this.position = new int[]{500, 0};
        this.velocity = new int[]{0, 0};
        this.health = 100;
        this.numJumps = MAX_CONSECUTIVE_JUMPS;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
    }
    public void update() {
        if (isCrouching) {
            this.velocity[Y] += CROUCH_GRAVITY; // apply crouch gravity
            /*if (no horizontal movement(check for keyboard input)) {
                velocity[X] *= CROUCH_FRICTION; // apply crouch friction
            } */
        } else {
            this.velocity[Y] += GRAVITY; // apply normal gravity
            /*if (no horizontal movement(check for keyboard input/) {
                velocity[X] *= FRICTION; // apply normal friction
            } */
        }
    
        /*
         * here we need to update velocity based off keyboard input
         */

        // update position based on velocity
        int[] newPosition = new int[]{getPosition()[X] + getVelocity()[X], getPosition()[Y] + getVelocity()[Y]};
        setPosition(newPosition);
    
        // check ground collision
        checkLanded();
    
        // update attack cooldown
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    
        // reset attacking state after attack is complete
        if (this.isAttacking && this.attackCooldown <= 0) {
            this.isAttacking = false;
        }

        if (isCrouching) {
            //halve height of player
            //adjust position downward
            //change frame to crouching
        }
    }
}
