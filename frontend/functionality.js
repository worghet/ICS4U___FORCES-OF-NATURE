
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
const MAX_CROUCH_VELOCITY = 4; // increase this to make crouch to slide
const CROUCH_FRICTION = 0.2;
const CROUCH_GRAVITY = 15;
const ACCELERATION = 0.8; // acceleration rate (how fast something accelerates)
const FRICTION = 0.55; // reduces velocity when no key is pressed
const GRAVITY = 0.7; // gravity pulls the player down
const JUMP_FORCE = 14; // how strong the jump is (capped by max-velocity)
const GROUND_Y = 400; // y-position where the ground is (will set accoring to maps)
const NUM_CONSECUTIVE_JUMPS = 3;

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
var isCrouching = false;
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
        case "A":    
            keys_pressed.left = true;
            break;

        // right    
        case "d":
        case "D": // using a "fall-through" technique to catch multiple inputs under 1    
            keys_pressed.right = true;
            break;    

        // up (jump)    
        case "w":
        case " ":    

            // check if the user has exceeded the 
            if (numJumps < NUM_CONSECUTIVE_JUMPS) {
                velocity[Y] = -JUMP_FORCE; // apply jump force; gravity will take care of coming down.
                                           // negative because top-bottom pixel number INCREASES, since we're going up, we need to go NEGATIVE. 
                                            // gravity maxes out at -MAX_VELOCITY
                isJumping = true;
                numJumps++; // update jump tracker 
            }
            break;

        // down (crouch?)    
        case "s":
        case "Shift":
        keys_pressed.down = true;
            isCrouching = true;
            break;
    }
});

// listen for button RELEASE
document.addEventListener("keyup", function (event) {
    
    // switch the pressed key; pretty self-explanatory
    switch (event.key) {
        case "a":
        case "A":    
            keys_pressed.left = false;
            break;
        case "d":
        case "D":    
            keys_pressed.right = false;
            break;
        case "w":
        case " ":    
            keys_pressed.up = false;
            break;
        case "s":
        case "Shift":
        keys_pressed.down = false;
            isCrouching = false;
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
    // *in reality this will GET user positions, sprites, and display them

    // CHECK PRESSED KEYS (MANAGE ACCELERATION)

    // if going left or right, change the acceleration; this acceleration will impact velocity later
    if (keys_pressed.left) {
        velocity[X] -= ACCELERATION;
    }
    if (keys_pressed.right) {
        velocity[X] += ACCELERATION;
    }

    // apply gravity to vertical velocity (cuz we wanna go down)
    if (isCrouching) {
        velocity[Y] += CROUCH_GRAVITY;
    }
    else {
        velocity[Y] += GRAVITY;
    }

    // CHECK THAT NO KEY IS PRESSED (APPLY FRICTION)

    // not moving horizontally
    if (isCrouching) {

        if (!keys_pressed.left && !keys_pressed.right) {
            velocity[X] *= CROUCH_FRICTION;
        }
    }
    else {
        if (!keys_pressed.left && !keys_pressed.right) {
            velocity[X] *= FRICTION;
        }
    }
    // we dont need to check vertical movement cuz gravity got that

    // LIMIT VELOCITIES TO MAXIMUM (LIMITVELOCITIES())

    // if x velocity is out of the max range, limit it
    if (isCrouching) {
        
        // comment this (or increase max crouch velocity) for sliding;
        if (velocity[X] > MAX_CROUCH_VELOCITY) {
            velocity[X] = MAX_CROUCH_VELOCITY;
        }
        else if (velocity[X] < -MAX_CROUCH_VELOCITY) {
            velocity[X] = -MAX_CROUCH_VELOCITY;
        }

    }
    else {
        if (velocity[X] > MAX_VELOCITY) {
            velocity[X] = MAX_VELOCITY;
        }
        else if (velocity[X] < -MAX_VELOCITY) {
            velocity[X] = -MAX_VELOCITY;
        }

        // by keeping this commented, jumping height wont be limited
        // if (velocity[Y] > MAX_VELOCITY) {
        //     velocity[Y] = MAX_VELOCITY;
        // }
        // else if (velocity[Y] < -MAX_VELOCITY) {
        //     velocity[Y] = -MAX_VELOCITY;
        // }
    }

    // do the same with y velocity (so that jumps dont get OP)


    // UPDATE POSITION VARIABLE 

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
    if (position[Y] <= 0) {
        position[Y] = 0;
        velocity[Y] = 0;
    }

    if (position[X] <= 0) { // can replace 0 and 1100 with wall pixels
        velocity[X] = 0;
        position[X] = 0;
        // numJumps = 2; extra jump
    }
    else if (position[X] >= 1000) {
        velocity[X] = 0;
        position[X] = 1000;
        // velocity[X] = -10; "wall jump effect"
        // numJumps = 2; extra jump
    }

    // CHANGE CSS (THIS AND BELOW IS THE ONLY PART THAT WILL BE KEPT FROM JAVA TRANSFER) 

    // change the css to actually move the box on the screen 
    box.style.left = intToPx(position[X]);

    // CROUCHING OR NOT?
    if (keys_pressed.down) {
        box.style.height = "50px"; // Shrink height
        box.style.top = intToPx(position[Y] + 50); // Adjust position downward
    } else {
        box.style.height = "100px"; // Reset height
        box.style.top = intToPx(position[Y]); // Reset position
    }

    // keep the update loop in sync with the browser's refresh rate
    requestAnimationFrame(updatePosition);
}
