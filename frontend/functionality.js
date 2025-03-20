// This is a javascript file centered around the server-functionality of the project.
// It includes many (if not, all) of the API communicators here.

// == CONSTANTS [FOR READABILITY] ==========================


const X = 0;
const Y = 1;
const FPS = 40;
//animation
const spriteSheet = new Image();
spriteSheet.src = "./images/Unfinished Sprites.png";//wanted to use for testing purposes but need help syncing back & front end
//will finish sprite sheet, & organize it



// == IMPORTANT VARIABLES ==================================


// This holds the keyboard inputs sent to the back end.
var keys_pressed = {left: false, right: false, up: false, down: false};

// These help with user identification / messaging.
let localPlayerId;
let username;

// Other vital
let gameStarted = false;
let currentGameData = null;


// ============================================================
// ======================== PROGRAM ===========================


setInterval(sendPlayerAction, (1000 / FPS));
setInterval(updatePosition, (1000 / FPS));


// ======================== PROGRAM ===========================
// ============================================================


// == INPUT LISTENERS ======================================
window.onload = function() {
    const playerId = sessionStorage.getItem("playerId");

    if (playerId === null) {
        window.location.href = "/forces-of-nature";
    } else {
        console.log("Playing as player with id:", playerId);
        localPlayerId = playerId;
    }

   fetch("/game-map", {
       method: "GET"
   })
   .then(response => response.json())
   .then(mapData => {
     const backgroundIndex = mapData.backgroundIndex;
     console.log("Background Index: ", backgroundIndex);

     switch (backgroundIndex) {

        case 0:
            console.log("welcome to deepsea");
            document.body.style.backgroundImage = `url("/images/gameplay_background_water.gif")`;
            break;
        case 1:
            console.log("welcome to caves")
            document.body.style.backgroundImage = `url("/images/gameplay_background_earth.gif")`;
            break;
        case 2:
            console.log("welcome to industry")
            document.body.style.backgroundImage = `url("/images/gameplay_background_industrial.gif")`;
            break;

     }


   });

   // do countdown (wavy text "welcome to ...")

};

window.addEventListener("beforeunload", (event) => {

    // remove player trace

    const playerId = sessionStorage.getItem("playerId");

    if (playerID) {
        fetch("/remove-player", {
            method: "POST",
            body: playerId
        }).then(() => {

            // TODO: remove storage session tag and box

            document.getElementById("box-" + playerId).remove;

            sessionStorage.removeItem("playerId");
        });
    }
});


// Input listener for when key is PRESSED.
document.addEventListener("keydown", function (event) {

    // Check which key is pressed.
    // Using a cool "fall-through" technique to catch multiple inputs under one statement.

    switch (event.key) {

        // Left.

        case "a":
        case "A":
            keys_pressed.left = true;
            break;

        // Right.

        case "d":
        case "D":
            keys_pressed.right = true;
            break;

        // Up (JUMP).

        case "w":
        case " ":
            keys_pressed.up = true;
            break;

        // Down (CROUCH).

        case "s":
        case "Shift":
            keys_pressed.down = true;
            break;
    }
});

// Input listener for when key is RELEASED.
document.addEventListener("keyup", function (event) {

    // Check which key is pressed.
    // Using a cool "fall-through" technique to catch multiple inputs under one statement.

    switch (event.key) {

        // Left.

        case "a":
        case "A":
            keys_pressed.left = false;
            break;

        // Right.

        case "d":
        case "D":
            keys_pressed.right = false;
            break;

        // Up.

        case "w":
        case " ":
            keys_pressed.up = false;
            break;

        // Down (STAND UP).

        case "s":
        case "Shift":
        keys_pressed.down = false;
            break;
    }
});


// == FUNCTIONS [REPEATING] ================================


function sendPlayerAction() {

       console.log()

    // Build keys_pressed into a legible array.

    const currentAction =  [keys_pressed.left, keys_pressed.right, keys_pressed.down, keys_pressed.up];

    // Send data to server.

    fetch("/player-action", {

        // Select "POST" (i.e. Sending info).

        method: "POST",

        // Build a pseudo "PlayerAction" class to be deserialized.

        body: JSON.stringify( {
            "playerId": localPlayerId,
            "keys_pressed": currentAction
        })
    })
}

function updatePosition() {

    // Request the data itself from the server.

    fetch("/gamedata", {
        method: "GET"
    })
    .then(response => response.json())
    .then(json => {

        // Check that the data is not repeated and/or identical to the previous.
        // (This is done for optimization's sake.)

        if (JSON.stringify(currentGameData) === JSON.stringify(json)) {
            return;
        }

        // If something is new, update currentGameData, and update renders of the players.

        currentGameData = json;
        renderPlayers(currentGameData.players);
    })
}


// == FUNCTIONS [UTILITY] ==================================

function renderPlayers(players) {

    // Check that players array is initialized properly.
    if (!players || players.length === 0) return;

    // Iterate through each player.
    players.forEach(player => {

        // Try to locate an existing render for this user.
        let playerBox = document.getElementById("box-" + player.id);
        let playerTag = document.getElementById("tag-" + player.id);

        // If none found, create them.
        if (!playerBox && !playerTag) {

            // Create the actual box.
            playerBox = document.createElement("div");
            playerBox.id = "box-" + player.id;
            playerBox.className = "box";
            playerBox.style.backgroundColor = player.colour; 

            // Create the player tag (+ set tag contents to player username).
            playerTag = document.createElement("div");
            playerTag.id = "tag-" + player.id;
            playerTag.className = "tag";
            playerTag.innerHTML = player.username;

            // Add box and tag to game window.
            document.getElementById("game-window").appendChild(playerBox);
            document.getElementById("game-window").appendChild(playerTag);
        }



       /*
        characterSprite = new animation(spriteSheet, 0, 0, 35, 35, 3, 1, false, true);
        // playerBox.style.backgroundImage = url(...);
        */

        // show health, temp for now
        playerBox.innerHTML = player.health + " / " + player.maxHealth;


        // Update box's position based on the player's position.
        playerBox.style.left = intToPx(player.position[X]);

        // If crouching, move everything down 50 pixels.
        if (player.isCrouching) {
            playerBox.style.height = "35px";
            playerBox.style.top = intToPx(player.position[Y] + 35);

            // Maintain 40 pixel height separation when crouched.
            playerTag.style.top = intToPx(player.position[Y] - 5);
        }

        // Otherwise, keep the positioning same as the computed one.
        else {
            playerBox.style.height = "70px";
            playerBox.style.top = intToPx(player.position[Y]);

            // Have the tag hover 40 pixels above the box.
            playerTag.style.top = intToPx(player.position[Y] - 40);
        }

        // Center the tag based on the player's box position and width.
        playerTag.style.left = intToPx(playerBox.offsetLeft + (playerBox.offsetWidth / 2) - (playerTag.offsetWidth / 2));
    });
}


// == FUNCTIONS [LOCAL] ====================================


function pxToInt(str_px) {
    return parseInt(str_px.replace("px", ""));
}

function intToPx(int_px) {
    return (int_px + "px");
}