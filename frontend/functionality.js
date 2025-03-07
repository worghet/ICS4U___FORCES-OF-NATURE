
// == CONSTANTS ======

// INDEXES

//// left/right for the moveX, moveY functions
//const LEFT = 0;
//const RIGHT = 1;
//const UP = 1;
//const DOWN = 0;

// x/y for the position[X], position[Y], velocity[X], velocity[Y]
const X = 0;
const Y = 1;

// PHYSICS CONSTANTS (can change due if different characters)

//const MAX_VELOCITY = 10;  // max speed (in any direction)
//const MAX_CROUCH_VELOCITY = 4; // increase this to make crouch to slide
//const CROUCH_FRICTION = 0.2;
//const AIR_FRICTION = 0.975;
//const CROUCH_GRAVITY = 15;
//const ACCELERATION = 0.8; // acceleration rate (how fast something accelerates)
//const FRICTION = 0.55; // reduces velocity when no key is pressed
//const GRAVITY = 0.7; // gravity pulls the player down
//const JUMP_FORCE = 14; // how strong the jump is (capped by max-velocity)
//const GROUND_Y = 400; // y-position where the ground is (will set accoring to maps)
//const NUM_CONSECUTIVE_JUMPS = 3;

// PLAYER

// essentially the moving object in the HTML(currently just a square) (later will replace with actual images)
//const box = document.getElementById("box");

// == VARIABLES ===========================

// KEYS

// keeps track of which keys are pressed; used in the update loop
var keys_pressed = {
    left: false,
    right: false,
    up: false,
    down: false,
};

let id;

// PLAYER MOVEMENT

//var velocity = [0, 0]; // rate of change in position
//var position = [500, 0]; // GROUND_Y so that the user spawns on the ground
//var isJumping = false; // track if the player is in the air
//var isCrouching = false;
//var numJumps = 0; // track for 3-jumps
// var animationFrame <-- for when we need to cycle between different animations

// set the player's visual to the position
//box.style.left = intToPx(position[X]);
//box.style.top = intToPx(position[Y]);

// == PROGRAM ITSELF ===========================

// run the looping update position

updatePosition();
sendPlayerAction(); // Starts the loop

function sendPlayerAction() {
    const currentAction =  [keys_pressed.left, keys_pressed.right, keys_pressed.down, keys_pressed.up];

    fetch("/player-action", {
        method: "POST",
        headers: {
            "Content-Type": "text/plain"
        },
        body: JSON.stringify( {

            "playerId": 1,
            "keys_pressed": currentAction
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Server responded with status: " + response.status);
        }
        return response.text();
    })
    .then(data => {
//        console.log("Sent movement --> " + currentAction);
//        console.log("Server response:", data);
    })
    .catch(error => {
        console.error("Error sending player action:", error);
    })
    .finally(() => {
        setTimeout(sendPlayerAction, 50); // looper var; restarts the method every X milliseconds
    });
}




// == INPUT LISTENERS =============================

// listen for button PRESS
document.addEventListener("keydown", function (event) {

    // debugging purposes
    // console.log("key pressed: " + event.key);
    
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
//            if (numJumps < NUM_CONSECUTIVE_JUMPS) {
//                velocity[Y] = -JUMP_FORCE; // apply jump force; gravity will take care of coming down.
//                                           // negative because top-bottom pixel number INCREASES, since we're going up, we need to go NEGATIVE.
//                                            // gravity maxes out at -MAX_VELOCITY
//                isJumping = true;
//                numJumps++; // update jump tracker
//            }
            keys_pressed.up = true;
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

let currentGameData = null;

function getGameData() {
    return fetch("/gamedata", { method: "GET" })
        .then(response => response.json())
        .then(json => {
            currentGameData = json; // Store game data
            clearGameWindow();
            renderPlayers(currentGameData.players); // Render players after fetching data
        })

}

function updatePosition() {
    getGameData(); // Get the latest game data
    requestAnimationFrame(updatePosition); // Continue the game loop
}

function clearGameWindow() {
    document.getElementById("game-window").innerHTML = "";
}


function renderPlayers(players) {
    if (!players || players.length === 0) return; // Prevent errors on empty data

//    console.log("Rendering players:", players);

    players.forEach(player => {
//        console.log("Rendering player with id:", player.id);

        var playerBox = document.createElement("div");
        playerBox.className = "box";


        playerBox.style.backgroundColor = "red";

        // Ensure player.position exists and has valid coordinates
        console.log("id " + localID + " --> x: " + player.position[X] + " y: " + player.position[Y])
        playerBox.style.left = intToPx(player.position[X]);
        playerBox.style.top = intToPx(player.position[Y]);

        document.getElementById("game-window").appendChild(playerBox);
    });
}


let localPlayerId;

function addPlayer() {
    console.log("add player called");
    fetch ("/add-player", {

        method: "POST"

    })
    .then((response) => response.text()) // Read the response as text
    .then((playerId) => {
        localID = parseInt(playerId); // Store the received ID
        console.log("Assigned localID:", localID);
    })
}

function startGame() {

    console.log("game should start (called)");
    fetch ("/start-game", {

        method: "POST"

    })

}