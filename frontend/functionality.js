// This is a javascript file centered around the server-functionality of the project.
// It includes many (if not, all) of the API communicators here.

// == CONSTANTS [FOR READABILITY] ==========================


const X = 0;
const Y = 1;
const FPS = 40;


//animation
let spriteSheetPos; //use the animation() function to return where to cut out img on sprite sheet 
//will finish sprite sheet, & organize it
let attackFrame = 31; //because isAttacking is only true for an instant, needs a counter to display sprite for longer


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
let otherPlayersSectionUI = document.getElementById("other-players-div");

let playerActionInterval;
let updatePositionInterval;


// ============================================================
// ======================== PROGRAM ===========================


// ======================== PROGRAM ===========================
// ============================================================


// == INPUT LISTENERS ======================================
window.onload = function() {
    const playerId = sessionStorage.getItem("playerId");

    if (playerId === null) {
//        console.log("invalid player id")
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
//     console.log("Background Index: ", backgroundIndex);


                            const groundDiv = document.createElement("div");
                            groundDiv.className = "ground";
                            groundDiv.style.top = intToPx(mapData.groundY + 70);
                            document.body.appendChild(groundDiv);


     switch (backgroundIndex) {

        case 0:
//            console.log("welcome to deepsea");
            document.body.style.backgroundImage = `url("/images/deepsea.gif")`;
            groundDiv.style.backgroundColor = "darkgoldenrod";


            mapData.islands.forEach((island) => {
                const islandDiv = document.createElement("div");
                islandDiv.className = "island";
                islandDiv.style.left = intToPx(island.topLeftX);
                islandDiv.style.top = intToPx(island.topLeftY);
                islandDiv.style.width = intToPx(island.width);

                islandDiv.style.backgroundColor = "darkgoldenrod";


//                islandDiv.style.backgroundImage = `url("/images/sand_island_texture.jpg")`;
//                islandDiv.style.backgroundSize = 'contain';  // Ensures the image covers the entire div
//                islandDiv.style.backgroundRepeat = 'repeat';  // Ensures the image repeats if it's smaller than the div

                document.body.appendChild(islandDiv);
            });

            break;
        case 1:
//            console.log("welcome to caves")
            document.body.style.backgroundImage = `url("/images/gameplay_background_earth.gif")`;
            document.body.style.backgroundPositionY = "-300px";


                     mapData.islands.forEach((island) => {
                            const islandDiv = document.createElement("div");
                            islandDiv.className = "island";
                            islandDiv.style.left = intToPx(island.topLeftX);
                            islandDiv.style.top = intToPx(island.topLeftY);
                            islandDiv.style.width = intToPx(island.width);


//                                        islandDiv.style.backgroundImage = `url("/images/ground_island_texture.jpg")`;
//                                        islandDiv.style.backgroundSize = 'contain';  // Ensures the image covers the entire div
//                                        islandDiv.style.backgroundRepeat = 'repeat';  // Ensures the image repeats if it's smaller than the div
                                islandDiv.style.backgroundColor = "darkslategrey";
                            document.body.appendChild(islandDiv);
                        });


                                groundDiv.style.backgroundColor = "darkslategrey";


            break;
        case 2:
//            console.log("welcome to industry")
                groundDiv.style.backgroundColor = "silver";
           document.body.style.backgroundImage = `url("/images/gameplay_background_industrial.gif")`;
           document.body.style.backgroundPositionY = "-500px";

                     mapData.islands.forEach((island) => {
                            const islandDiv = document.createElement("div");
                            islandDiv.className = "island";
                            islandDiv.style.left = intToPx(island.topLeftX);
                            islandDiv.style.top = intToPx(island.topLeftY);
                            islandDiv.style.width = intToPx(island.width);
                islandDiv.style.backgroundColor = "silver";
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

            // if dealing with this player
            if (player.id == localPlayerId) {

                if (player.isSpectating) {
                    document.getElementById("spectator-div").style.visibility = "visible";
//                    console.log("is spectator");
                }
                else {
//                    console.log("is not spectator");


                    // Start sending input to server (based on FPS).
                    playerActionInterval = setInterval(sendPlayerAction, (1000 / FPS));



                    // build player ui (hearts, health, etc)
                    const playerInfo = document.getElementById("player-info");

                     switch(player.colour) {
                                       case "aqua":
                                           playerInfo.style.backgroundImage = "url('/images/podium-angler-active.png')"
                                           playerInfo.style.backgroundColor = "steelblue"
                                           break;
                                       case "saddlebrown":
                                           playerInfo.style.backgroundImage = "url('/images/podium-golem-active.png')"
                                           playerInfo.style.backgroundColor = "saddlebrown"

                                           break;
                                       case "red":
                                           playerInfo.style.backgroundImage = "url('/images/podium-welder-active.png')"
                                           playerInfo.style.backgroundColor = "firebrick"
                                           break;
                                       default:
                                           playerInfo.style.backgroundImage = "url('/images/main_wizard.png')"
                                           playerInfo.style.backgroundColor = "purple"
                                           // wizard

                                   }

                    // change image and background

                    const thisPlayerUsernameUI = document.getElementById("this-player-username");
                    thisPlayerUsernameUI.textContent = player.username;


                    const thisPlayerHealthUI = document.getElementById("this-player-health");
                    thisPlayerHealthUI.max = player.maxHealth;
                    thisPlayerHealthUI.value = player.health;
                    // Append all elements to the player-info container

                    const thisPlayerHeartsUI = document.getElementById("this-player-lives");
                    thisPlayerHeartsUI.innerHTML = '&hearts;'.repeat(player.lives || 3);  // Default to 3 hearts if no lives

                }
            }
            else {
                // dealing with other players
              const otherPlayerDiv = document.createElement("div");
              otherPlayerDiv.className = "other-player";
              otherPlayerDiv.id = "div-" + player.id;  // Give the div a unique ID for tracking

              // Create and append the username
              const otherPlayerName = document.createElement("p");
              otherPlayerName.className = "other-player-username";
              otherPlayerName.textContent = player.username;
//              console.log("adding " + player.username + "to div " + otherPlayerDiv.id)
              otherPlayerDiv.appendChild(otherPlayerName);

              // Create and append the health bar
              const healthBar = document.createElement('progress');
              healthBar.classList.add('other-player-health-bar');
              healthBar.value = player.health || 100;  // Default to 100 if no health value
              healthBar.max = player.maxHealth || 100;  // Default to 100 if maxHealth is undefined
              healthBar.id = "health-" + player.id;
//              console.log("adding " + player.username + "health bar to div " + otherPlayerDiv.id)
              otherPlayerDiv.appendChild(healthBar);

              // Create and append the hearts (lives)
              const hearts = document.createElement('div');
              hearts.classList.add('other-player-hearts');
              hearts.innerHTML = '&hearts;'.repeat(player.lives || 3);  // Default to 3 hearts if no lives
              hearts.id = "lives-" + player.id;
//              console.log("adding " + player.username + "lives to div " + otherPlayerDiv.id);
              otherPlayerDiv.appendChild(hearts);


//                console.log(player.colour)
                switch(player.colour) {
                    case "aqua":
                        otherPlayerDiv.style.backgroundImage = "url('/images/podium-angler-active.png')"
                        otherPlayerDiv.style.backgroundColor = "steelblue"
                        break;
                    case "saddlebrown":
                        otherPlayerDiv.style.backgroundImage = "url('/images/podium-golem-active.png')"
                        otherPlayerDiv.style.backgroundColor = "saddlebrown"

                        break;
                    case "red":
                        otherPlayerDiv.style.backgroundImage = "url('/images/podium-welder-active.png')"
                        otherPlayerDiv.style.backgroundColor = "firebrick"
                        break;
                    default:
                        otherPlayerDiv.style.backgroundImage = "url('/images/main_wizard.png')"
                        otherPlayerDiv.style.backgroundColor = "purple"
                        // wizard

                }

              // Append the newly created div to the section
              otherPlayersSectionUI.appendChild(otherPlayerDiv);

                // Append the newly created div to the section
//                console.log("Appended player div for: " + player.username);
//                console.log(otherPlayersSectionUI);
            }

        });
    })

    // Interval (repeated action) timing based on FPS constant.
    updatePositionInterval = setInterval(updatePosition, (1000 / FPS));

   // do countdown (wavy text "welcome to ...")

};

window.addEventListener("beforeunload", (event) => {

    disconnect();

//    document.getElementById("box-" + localPlayerId).remove();
//    document.getElementById("tag-" + localPlayerId).remove();

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

        // If the data is the exact same (no changes), avoid redundant updates.
        if (JSON.stringify(currentGameData) == JSON.stringify(json)) return;

        // Set local variable to new game data.
        currentGameData = json;

        // If game is on, render the data.
        if (currentGameData.gameRunning) {
            const activePlayerIds = new Set(json.players.map(p => p.id)); // Store all active players
            document.querySelectorAll(".box, .tag, .other-player").forEach(element => {
                const playerId = parseInt(element.id.split("-")[1], 10); // Extract player ID and parse as an integer
                if (!activePlayerIds.has(playerId)) {
                    element.remove();
                }
            });

            // Render players who are still in the game
            renderPlayers(json.players);

        }

        // Otherwise, show the game end screen.
        else {
            document.body.style.filter = "grayscale(100%)";
            document.getElementById("game-over-popup").style.visibility = "visible";
            clearInterval(playerActionInterval);
            clearInterval(updatePositionInterval);
        }
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
                playerBox.style.backgroundColor = "rgba(0, 0, 0, 0)";


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

            //player starts facing forward
           /* playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature2.png")';
            playerBox.style.width = "70px"; // Display size (scaled)
            playerBox.style.height = "70px"; // Display size (scaled)
            playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
            playerBox.style.backgroundPosition = "0px 0px"; // Select the top-left 35x35 portion
            playerBox.style.backgroundRepeat = "no-repeat";*/


            if (player.lives == 0) {

                 if (player.id == localPlayerId) {
                    isSpectating = true;

//                    console.log("you are now dead, here is the div for you")
                     document.getElementById("spectator-div").style.visibility = "visible";

                                document.getElementById("this-player-health").value = 0;
                                document.getElementById("this-player-lives").innerHTML = "";  // Default to 3 hearts if no lives
                                document.getElementById("player-info").style.filter = "grayscale(100%)";
//                     document.getElementById("player-info").
                     clearInterval(playerActionInterval);
                 }
                 else {

                    let otherPlayerInfoSection = document.getElementById("div-" + player.id);
                    if (otherPlayerInfoSection) {
                        otherPlayerInfoSection.remove();
                    }
                 }

                playerBox.remove();
                playerTag.remove();
                return;
            }

            if (player.isAttacking) {
               // playerBox.style.backgroundColor = "black";
               attackFrame= 0; 
               console.log("attackframe: "+attackFrame);
                // Get sprite sheet position from animate function (returns [x, y])
                spriteSheetPos = animate(1, "attack", checkPlayerType(player), player.direction, attackFrame, true);

                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature3.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";
                
                
            }
            else {
                //player starts facing forward
              /*  playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature2.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = "0px 0px"; // Select the top-left 35x35 portion
                playerBox.style.backgroundRepeat = "no-repeat";*/
            }

            if (player.direction) {
                playerBox.style.transform = "scaleX(1)";
                // Get sprite sheet position from animate function (returns [x, y])
                if(attackFrame<5){
                    spriteSheetPos = animate(player.animationFrame, "run", checkPlayerType(player), player.direction, attackFrame, true);
                }else{
                    spriteSheetPos = animate(player.animationFrame, "run", checkPlayerType(player), player.direction, attackFrame, false);
                }
        

                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature3.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";

                //incrament the frame that should display
                if(player.animationFrame === 3){
                    player.animationFrame = 1;
                } else {
                    player.animationFrame +=1;
                }
                

                
            }
            else {
                playerBox.style.transform = "scaleX(-1)";
                // Get sprite sheet position from animate function (returns [x, y])
                if(attackFrame<5){
                    spriteSheetPos = animate(player.animationFrame, "run", checkPlayerType(player), player.direction, attackFrame, true);
                }else{
                    spriteSheetPos = animate(player.animationFrame, "run", checkPlayerType(player), player.direction, attackFrame, false);
                }

                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature3.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";

                //incrament the frame that should display
                if(player.animationFrame === 3){
                    player.animationFrame = 1;
                } else {
                    player.animationFrame +=1;
                }
            }

//            otherPlayerHealth.value = "player.health";  // Set the new health value
//            otherPlayerLives.innerHTML = '&hearts;'.repeat(player.lives);  // Update hearts with new lives count


            // Update box's position based on the player's position.
            playerBox.style.left = intToPx(player.position[X]);

            // If crouching, move everything down 50 pixels.
            if (player.isCrouching) {
                //playerBox.style.height = "35px";

                // Get sprite sheet position from animate function (returns [x, y])
                if(attackFrame<5){
                    spriteSheetPos = animate(2, "crouch", checkPlayerType(player), player.direction, attackFrame, true);
                }else{
                    spriteSheetPos = animate(2, "crouch", checkPlayerType(player), player.direction, attackFrame);
                }

                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                //display crouch
                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature3.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                //playerBox.style.height = "35px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";

                playerBox.style.top = intToPx(player.position[Y] + 10);

                // Maintain 40 pixel height separation when crouched.
                playerTag.style.top = intToPx(player.position[Y] - 10);
            }

            // Otherwise, keep the positioning same as the computed one.
            else {
                playerBox.style.height = "70px";
                playerBox.style.top = intToPx(player.position[Y]);
                // Have the tag hover 40 pixels above the box.
                playerTag.style.top = intToPx(player.position[Y] - 40);

                // just displaying standing --> would override the run display
                // Get sprite sheet position from animate function (returns [x, y])
               /* spriteSheetPos = animate(1, "stand", checkPlayerType(player), player.direction);

                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature2.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";*/
        
            }

            if(player.isJumping) {
                // Get sprite sheet position from animate function (returns [x, y])
                
                if(attackFrame<30){
                    spriteSheetPos = animate(1, "jump", checkPlayerType(player), player.direction, attackFrame, true);
                }else{
                    spriteSheetPos = animate(1, "jump", checkPlayerType(player), player.direction, attackFrame, false);
                }


                // Extract x and y from the returned 2D array values
                let [xPos, yPos] = spriteSheetPos;

                //display crouch
                playerBox.style.backgroundImage = 'url("/images/Finished-Sprites-Forces-of-Nature3.png")';
                playerBox.style.width = "70px"; // Display size (scaled)
                playerBox.style.height = "70px"; // Display size (scaled)
                playerBox.style.backgroundSize = "1050px 1050px"; // Scale entire sprite sheet 2x
                playerBox.style.backgroundPosition = `-${xPos}px -${yPos}px`; // Select chunk from the sprite sheet
                playerBox.style.backgroundRepeat = "no-repeat";
            }

            

            // Center the tag based on the player's box position and width.
            playerTag.style.left = intToPx(playerBox.offsetLeft + (playerBox.offsetWidth / 2) - (playerTag.offsetWidth / 2));




            // UPDATE THEIR DIV

            if (localPlayerId != player.id) {

                            document.getElementById("health-" + player.id).value = player.health;
                            document.getElementById("lives-" + player.id).innerHTML = '&hearts;'.repeat(player.lives);

            }
            else {


                                document.getElementById("this-player-health").value = player.health;
                                document.getElementById("this-player-lives").innerHTML = '&hearts;'.repeat(player.lives || 3);  // Default to 3 hearts if no lives


            }

        } else {

            // is spectating
                       let playerBox = document.getElementById("box-" + player.id);
                        let playerTag = document.getElementById("tag-" + player.id);
              if (playerBox) {
                            playerBox.remove();
                        }
                        if (playerTag) {
                            playerTag.remove();
                        }

                          let otherPlayerInfoSection = document.getElementById("div-" + player.id);
                                            if (otherPlayerInfoSection) {
                                                otherPlayerInfoSection.remove();
                                            }
                if (player.id == localPlayerId) {
//                                              console.log("you are now dead, here is the div for you")
                                                                 document.getElementById("spectator-div").style.visibility = "visible";

                                                                            document.getElementById("this-player-health").value = 0;
                                                                            document.getElementById("this-player-lives").innerHTML = "";  // Default to 3 hearts if no lives
                                                                            document.getElementById("player-info").style.filter = "grayscale(100%)";
                                            //                     document.getElementById("player-info").
                                                                 clearInterval(playerActionInterval);
                                                                 }
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

function animate(frame, state, type, direction, aFrame, attack){
    //vars for where to cut out image from sprite sheet
    var yInitial = 0;
    var yAdding = 0;
    var yFinal = 0;
    var xInitial = 0;
    var xAdding = 0;
    var xFinal = 0;

    
    switch (type){
        case "Angler": 
            yInitial = 0;
            break;
        case "Golem":
            yInitial = 280;
            break;
        case "Welder":
            yInitial = 560;
            break;
    }
    if(aFrame < 30 && attack){
        attackFrame++;
        if(type === "Angler"){
            return [210, 140];
        } else if (type === "Golem"){
            return [280, yInitial+140];
        }else{
            return [280, yInitial+140];
        }
    }


    switch (state) {
        case "crouch":
            xInitial = 70;
            yAdding = 0; 
//            console.log("crouching");
            break;
        case "jump":
            xInitial = 210;
            if(type === "Golem"){
                xInitial = 140;
            }
            yAdding = 0; 
//            console.log("jumping");
            break;
        case "attack":
            xInitial = 210;
            if(type === "Angler"){
//               console.log("angler attack");
                return [280, 140];

            } else if (type === "Golem"){
//                console.log("golem attack");
                return [350, 490];

            }else{
 //               console.log("welder attack");
                return [350, yInitial+140];
            }
           /* if(direction){
                yAdding = 280; 
//                console.log("attack right");
            }else{
                yAdding = 280;
//               console.log("attack left");
            }*/
            break;
        case "run":
            xInitial = 0; 
            if(direction){
                yAdding = 140; 
//                console.log("run right");
                xAdding = 70 * frame - 70; // Frame count starts at 1, so adjust accordingly
                console.log(frame);
            }else{
                yAdding = 140;
//                console.log("run left");
                xAdding = 70 * frame - 70; // Frame count starts at 1, so adjust accordingly
                console.log(frame);
            }
            break;
        case "stand":
            xInitial = 0;
            yAdding = 0;
//            console.log("standing");
            break;
        default:
            console.error("Unknown player type: ", type);
            return [NaN, NaN];  // Return NaN if type is unrecognized
    }


    yFinal = yInitial + yAdding;
    xFinal = xInitial + xAdding;

    // return values in a 2D array
//    console.log("xPos:", xFinal, "yPos:", yFinal);
    return [xFinal, yFinal];

}//end of animation function

//checks which hero is used
function checkPlayerType(player) {
    
    if (player.colour === "aqua") {
        return "Angler";
    } if (player.colour === "saddlebrown") {
        return "Golem";
    } else if (player.colour === "red") {
        return "Welder";
    } else {
        return "Unknown type";
    }
   
}