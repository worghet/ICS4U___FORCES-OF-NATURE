
// == CONSTANTS ======

// INDEXES

// left/right for the moveX, moveY functions
const LEFT = 0;
const RIGHT = 1;
const UP = 1;
const DOWN = 0;

// x/y for the position[X], position[Y], velocity[X], velocity[Y]
const X = 0;
const Y = 1;

// PHYSICS CONSTANTS (can change due if different characters)

const MAX_VELOCITY = 10;  // max speed (in any direction)
const ACCELERATION = 0.8; // acceleration rate (how fast something accelerates)
const FRICTION = 0.7; // reduces velocity when no key is pressed
const GRAVITY = 0.4; // gravity pulls the player down
const JUMP_FORCE = 12; // how strong the jump is (capped by max-velocity)
const GROUND_Y = 400; // y-position where the ground is (will set accoring to maps)

// PLAYER

// essentially the moving object in the HTML(currently just a square) (later will replace with actual images)
const box = document.getElementById("box"); 

// == VARIABLES ===========================

// KEYS

// keeps track of which keys are pressed; used in the update loop
var keys_pressed = {
    left: false,
    right: false,
    up: false,
    down: false,
};

// PLAYER MOVEMENT

var velocity = [0, 0]; // rate of change in position
var position = [500, 0]; // GROUND_Y so that the user spawns on the ground
var isJumping = false; // track if the player is in the air
var numJumps = 0; // track for 3-jumps
// var animationFrame <-- for when we need to cycle between different animations

// set the player's visual to the position
box.style.left = intToPx(position[X]);
box.style.top = intToPx(position[Y]); 

// == PROGRAM ITSELF ===========================

// run the looping update position
updatePosition();

// == INPUT LISTENERS =============================

// listen for button PRESS
document.addEventListener("keydown", function (event) {

    // debugging purposes
    console.log("key pressed: " + event.key);
    
    // check which key was pressed (for optimization can ignore this if the key is the same as the previous but whatever)
    switch (event.key) {

        // left
        case "a":
            keys_pressed.left = true;
            break;

        // right    
        case "d":
            keys_pressed.right = true;
            break;

        // up (jump)    
        case "w":

            // check if the user has exceeded the 
            if (numJumps < 3) {
                velocity[Y] = -JUMP_FORCE; // apply jump force; gravity will take care of coming down.
                                           // negative because top-bottom pixel number INCREASES, since we're going up, we need to go NEGATIVE. 
                                            // gravity maxes out at -MAX_VELOCITY
                isJumping = true;
                numJumps++; // update jump tracker 
            }
            break;

        // up (jump --> some people prefer using space bar :\)    
        case " ":

            // commented this last time, same idea

            if (numJumps < 3) {
                velocity[Y] = -JUMP_FORCE;
                // isInTheAir = true; technically we dont need this (we will later when we do attacks; can't attack mid-air)
                numJumps++;
            }
            break;

        // down (crouch?)    
        case "s":
            keys_pressed.down = true;
            break;
    }
});

// listen for button RELEASE
document.addEventListener("keyup", function (event) {
    
    // switch the pressed key; pretty self-explanatory
    switch (event.key) {
        case "a":
            keys_pressed.left = false;
            break;
        case "d":
            keys_pressed.right = false;
            break;
        case "w":
            keys_pressed.up = false;
            break;
        case " ":
            keys_pressed.up = false;
            break;
        case "s":
            keys_pressed.down = false;
            break;
    }
});

// == METHODS =============================

// PIXELS TO INT AND VICE VERSA

// helps with simple conversion for css to update position 
function pxToInt(str_px) {
    return parseInt(str_px.replace("px", ""));
}

function intToPx(int_px) {
    return (int_px + "px");
}

// UPDATE POSITION (key (super important) looping method)

function updatePosition() {

    // CHECK PRESSED KEYS

    // if going left or right, change the acceleration; this acceleration will impact velocity later
    if (keys_pressed.left) {
        velocity[X] -= ACCELERATION;
    }
    if (keys_pressed.right) {
        velocity[X] += ACCELERATION;
    }

    // apply gravity to vertical velocity (cuz we wanna go down)
    velocity[Y] += GRAVITY;
    


    // CHECK THAT NO KEY IS PRESSED (friction)

    // not moving horizontally
    if (!keys_pressed.left && !keys_pressed.right) {
        velocity[X] *= FRICTION;
    }

    // we dont need to check vertical movement cuz gravity got that

    // LIMIT VELOCITIES TO MAXIMUM

    // if x velocity is out of the max range, limit it
    if (velocity[X] > MAX_VELOCITY) {
        velocity[X] = MAX_VELOCITY;
    }
    else if (velocity[X] < -MAX_VELOCITY) {
        velocity[X] = -MAX_VELOCITY;
    }

    // do the same with y velocity (so that jumps dont get OP)
    if (velocity[Y] > MAX_VELOCITY) {
        velocity[Y] = MAX_VELOCITY;
    }
    else if (velocity[Y] < -MAX_VELOCITY) {
        velocity[Y] = -MAX_VELOCITY;
    }

    // UPDATE POSITION

    // update position based on velocity
    position[X] += velocity[X];
    position[Y] += velocity[Y];

    // CHECK COLLISION WITH GROUND
    
    if (position[Y] >= GROUND_Y) { // checkts that the player is ON the ground or BELOW it
        position[Y] = GROUND_Y; // just clip the player ON the ground
        velocity[Y] = 0; // sets the vertical velocity to zero (as to not have gravity stack)
        numJumps = 0; // reset jumps
        // isInTheAir = false; again, won't need this yet
    }

    // change the css to actually move the box on the screen
    box.style.left = intToPx(position[X]);
    box.style.top = intToPx(position[Y]);

    // keep the update loop in sync with the browser's refresh rate
    requestAnimationFrame(updatePosition);
}
