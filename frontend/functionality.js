// This is a javascript file centered around the server-functionality of the project.
// It includes many (if not, all) of the API communicators here.

// == CONSTANTS [FOR READABILITY] ==========================


const X = 0;
const Y = 1;
const FPS = 15;
//animation
const spriteSheet = new Image();
spriteSheet.src = "./images/Unfinished Sprites.png";//wanted to use for testing purposes but need help syncing back & front end
//will finish sprite sheet, & organize it


// == IMPORTANT VARIABLES ==================================


// This holds the keyboard inputs sent to the back end.
var keys_pressed = {left: false, right: false, up: false, down: false, melee: false, projectile: false};

let spectating;

// These help with user identification / messaging.
let localPlayerId;
let username;

// Other vital
let gameStarted = false;
let currentGameData = null;

let spectatingUI = document.getElementById("spectator-div");

let playerActionInterval;


// ============================================================
// ======================== PROGRAM ===========================


// ======================== PROGRAM ===========================
// ============================================================


// == INPUT LISTENERS ======================================
window.onload = function() {
    const playerId = sessionStorage.getItem("playerId");

    if (playerId === null) {
        console.log("invalid player id")
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


                            const groundDiv = document.createElement("div");
                            groundDiv.className = "ground";
                            groundDiv.style.top = intToPx(mapData.groundY + 70);
                            document.body.appendChild(groundDiv);


     switch (backgroundIndex) {

        case 0:
            console.log("welcome to deepsea");
            document.body.style.backgroundImage = `url("/images/gameplay_background_water.gif")`;


            mapData.islands.forEach((island) => {
                const islandDiv = document.createElement("div");
                islandDiv.className = "island";
                islandDiv.style.left = intToPx(island.topLeftX);
                islandDiv.style.top = intToPx(island.topLeftY);
                islandDiv.style.width = intToPx(island.width);
                islandDiv.style.backgroundColor = "yellow";

//                islandDiv.style.backgroundImage = `url("/images/sand_island_texture.jpg")`;
//                islandDiv.style.backgroundSize = 'contain';  // Ensures the image covers the entire div
//                islandDiv.style.backgroundRepeat = 'repeat';  // Ensures the image repeats if it's smaller than the div

                document.body.appendChild(islandDiv);
            });

            break;
        case 1:
            console.log("welcome to caves")
            document.body.style.backgroundImage = `url("/images/gameplay_background_earth.gif")`;

                     mapData.islands.forEach((island) => {
                            const islandDiv = document.createElement("div");
                            islandDiv.className = "island";
                            islandDiv.style.left = intToPx(island.topLeftX);
                            islandDiv.style.top = intToPx(island.topLeftY);
                            islandDiv.style.width = intToPx(island.width);

//                                        islandDiv.style.backgroundImage = `url("/images/ground_island_texture.jpg")`;
//                                        islandDiv.style.backgroundSize = 'contain';  // Ensures the image covers the entire div
//                                        islandDiv.style.backgroundRepeat = 'repeat';  // Ensures the image repeats if it's smaller than the div
                            document.body.appendChild(islandDiv);
                        });



            break;
        case 2:
            console.log("welcome to industry")
           document.body.style.backgroundImage = `url("/images/gameplay_background_industrial.gif")`;

                     mapData.islands.forEach((island) => {
                            const islandDiv = document.createElement("div");
                            islandDiv.className = "island";
                            islandDiv.style.left = intToPx(island.topLeftX);
                            islandDiv.style.top = intToPx(island.topLeftY);
                            islandDiv.style.width = intToPx(island.width);

                            document.body.appendChild(islandDiv);

//                islandDiv.style.backgroundImage = `url("/images/steel_island_texture.jpg")`;
//                islandDiv.style.backgroundSize = 'contain';  // Ensures the image covers the entire div
//                islandDiv.style.backgroundRepeat = 'repeat';  // Ensures the image repeats if it's smaller than the div


                        });



            break;

     }

   });


    fetch("/gamedata", {
        method: "GET"
    })
    .then(response => response.json())
    .then(json => {
        json.players.forEach(player => {  // Corrected the forEach loop syntax

            if (player.id == localPlayerId && player.isSpectating) {
                isSpectating = true;
                document.getElementById("spectator-div").style.visibility = "visible";
                console.log("is spectator")
            }
            else {
                console.log("is NOT spectator")
                playerActionInterval = setInterval(sendPlayerAction, (1000 / FPS));
            }

            setInterval(updatePosition, (1000 / FPS));

        });
    })


   // do countdown (wavy text "welcome to ...")

};

window.addEventListener("beforeunload", (event) => {

    disconnect();

    document.getElementById("box-" + localPlayerId).remove();
    document.getElementById("tag-" + localPlayerId).remove();

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
        case "W":
            keys_pressed.up = true;
            break;

        // Down (CROUCH).

        case "s":
        case "Shift":
        case "S":
            keys_pressed.down = true;
            break;

        case "p":
        case "P":
            keys_pressed.projectile = true;
            break;

        case "o":
        case "O":
            keys_pressed.melee = true;
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
        case "W":
            keys_pressed.up = false;
            break;

        // Down (STAND UP).

        case "s":
        case "Shift":
        case "S":
        keys_pressed.down = false;
            break;


        case "p":
        case "P":
            keys_pressed.projectile = false;
            break;

        case "o":
        case "O":
            keys_pressed.melee = false;
            break;
    }
});


// == FUNCTIONS [REPEATING] ================================


function sendPlayerAction() {

    // Build keys_pressed into a legible array.

    const currentAction =  [keys_pressed.left, keys_pressed.right, keys_pressed.down, keys_pressed.up, keys_pressed.projectile, keys_pressed.melee];

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
    fetch("/gamedata", { method: "GET" })
    .then(response => response.json())
    .then(json => {
        if (JSON.stringify(currentGameData) === JSON.stringify(json)) return; // Avoid redundant updates

        currentGameData = json;
        const activePlayerIds = new Set(json.players.map(p => p.id)); // Store all active players

        // Remove any player elements that are no longer in the game
        document.querySelectorAll(".box, .tag").forEach(element => {
            const playerId = element.id.split("-")[1]; // Extract player ID
            if (!activePlayerIds.has(playerId)) {
                element.remove(); // Remove disconnected player elements
            }
        });

        renderPlayers(json.players);
    });
}



// == FUNCTIONS [UTILITY] ==================================

function renderPlayers(players) {

    // Check that players array is initialized properly.
    if (!players || players.length === 0) return;

    // Iterate through each player.
    players.forEach(player => {

        if (!player.isSpectating) {



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

                //displaying sprite
                playerBox.style.backgroundImage = 'url("/images/main_wizard.png")'
                // playerBox.style.backgroundSize = "525px 525px"; // Adjust based on sprite sheet size
                playerBox.style.backgroundPosition = "0px 0px"; // X and Y offset to select a portion*/
                playerBox.style.backgroundSize =  "contain";

                // playerBox.style.width = "35px"; // Size of cropped portion
                // playerBox.style.height = "35px"; // Size of cropped portion
                playerBox.style.backgroundRepeat = "no-repeat";
                

                // Create the player tag (+ set tag contents to player username).
                playerTag = document.createElement("div");
                playerTag.id = "tag-" + player.id;
                playerTag.className = "tag";
                playerTag.innerHTML = player.username;

                // Add box and tag to game window.
                document.getElementById("game-window").appendChild(playerBox);
                document.getElementById("game-window").appendChild(playerTag);
            }


            if (player.lives == 0) {
                 isSpectating = true;

                 if (player.id == localPlayerId) {

                    console.log("you are now dead, here is the div for you")
                     document.getElementById("spectator-div").style.visibility = "visible";
                     clearInterval(playerActionInterval);
                 }


                playerBox.remove();
                playerTag.remove();
                return;
            }

           

            // show health, temp for now
            playerBox.innerHTML = player.health + " / " + player.maxHealth + "   |   LIVES: " + player.lives;


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
        }

    });
}

   function backToMain() {
        disconnect();
        window.location.href = "/forces-of-nature";
    }

    function disconnect() {
      fetch("/remove-player", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: sessionStorage.getItem("playerId")
      });
    }

// == FUNCTIONS [LOCAL] ====================================


function pxToInt(str_px) {
    return parseInt(str_px.replace("px", ""));
}

function intToPx(int_px) {
    return (int_px + "px");
}