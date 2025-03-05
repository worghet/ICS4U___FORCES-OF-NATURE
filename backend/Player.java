package backend;

public class Player {
    private static final int MAX_VELOCITY = 10;
    private static final int MAX_CROUCH_VELOCITY = 4;
    private static final double CROUCH_FRICTION = 0.2;
    private static final int CROUCH_GRAVITY = 15;
    private static final double ACCELERATION = 0.8;
    private static final double FRICTION = 0.55;
    private static final double GRAVITY = 0.55;
    private static final int JUMP_FORCE = 12;
    private static final int GROUNDY = 400;
    private static final int MAX_CONSECUTIVE_JUMPS = 3;

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
    
    public Player(int[] position, int[] velocity) {
        this.position = position;
        this.velocity = velocity;
        this.health = 100;
        this.lives = 3;
        this.numJumps = 3;
        this.isCrouching = false;
        this.isJumping = false;
        this.isAttacking = false;
        this.attackCooldown = 0;
        this.direction = true;
    }

    
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
    public int numJumps() {
        return numJumps;
    }
    public boolean isCrouching() {
        return isCrouching;
    }
    public boolean isJumping() {
        return isJumping;
    }
    public boolean isAttacking() {
        return isAttacking;
    }
    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if(this.health <= 0) {
            this.health = 0;
            this.lives--;
        }

    }
    public boolean checkDead(int lives) {
        return lives <= 0;
    }
}
