// This is a javascript file centered around the server-functionality of the project.
// It includes many (if not, all) of the API communicators here.

// == CONSTANTS [FOR READABILITY] ==========================


const X = 0;
const Y = 1;
const FPS = 30;


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


// Pretty much just wait for the game to start.

const checkInterval = setInterval(() => {
    if (!gameStarted) {
        hasTheGameStarted();
    }
}, (1000 / FPS));


// ======================== PROGRAM ===========================
// ============================================================


// == INPUT LISTENERS ======================================

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


function addPlayer() {

    // TODO: Get username.

    // Send the request to make a new player.

    fetch ("/add-player", {

        method: "POST"
        // body: { username: ... }

    })
    .then((response) => response.text())
    .then((playerId) => {

        // After receiving the unique id for this page, save it.

        localPlayerId = parseInt(playerId);
    })
}

function renderPlayers(players) {

    // Check that everything is initialized properly.

    if (!players || players.length === 0) return;

    // Iterate through each player.

    players.forEach(player => {

        // Try to locate an existing render for this user.

        let playerBox = document.getElementById("box-" + player.id);

        // If none found, make one.

        if (!playerBox) {

            // Create the div.

            playerBox = document.createElement("div");

            // Assign it an id so that next time we don't reconstruct this.

            playerBox.id = "box-" + player.id;
            playerBox.className = "box";

            // Check class (character), set sprites accordingly.

            // TODO: Do colors to register different characters (to test select screen).
            playerBox.style.backgroundColor = "red";

            // Add the div to the game window.

            document.getElementById("game-window").appendChild(playerBox);
        }

        // Update box's position to fit that of the game data.

        playerBox.style.left = intToPx(player.position[X]);

        // Vertical position depends on if the player is crouching.

        if (player.isCrouching) {
            playerBox.style.height = "50px";
            playerBox.style.top = intToPx(player.position[Y] + 50);
        } else {
            playerBox.style.height = "100px";
            playerBox.style.top = intToPx(player.position[Y]);
        }
    });
}

function startGame() {

    // Just tell the server to start the game.

    fetch("/start-game", {
        method: "POST"
    });
}

function hasTheGameStarted() {

    // Check for if the game has started yet.

    fetch("/start-game", {
        method: "GET"
    })
    .then(response => response.text())
    .then(text => {

        if (text === "true") {

            // If the game HAS started, set variable to true to stop constantly checking.

            gameStarted = true;
            clearInterval(checkInterval);

            // Start the repeating processes (sending data and updating screen).

            setInterval(sendPlayerAction, (1000 / FPS));
            setInterval(updatePosition, (1000 / FPS));
        }
    });
}


// == FUNCTIONS [LOCAL] ====================================


function pxToInt(str_px) {
    return parseInt(str_px.replace("px", ""));
}

function intToPx(int_px) {
    return (int_px + "px");
}