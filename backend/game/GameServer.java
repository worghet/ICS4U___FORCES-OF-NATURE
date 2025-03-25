package game;

// == IMPORTS =============================
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.net.*;
import java.io.*;
import player.*;

// == GAME_SERVER =====
public class GameServer {


    // == INSTANCE VARIABLES [FIELDS] ==========================


    // Local IP address.
    private final String localHostAddress; 
    
    // Server object (used for setting up APIs, etc).
    private HttpServer httpServer; 

    // What port the server is hosted on.
    private final int serverPort; 
    
    // Game for communication and updating.
    private Game game; 


    // == API CONSTANTS [FOR READABILITY] ======================


    // Used for the multipurpose nature of page handler.
    static private final int MAIN_MENU_PAGE = 0;
    static private final int INFO_PAGE = 1;
    static private final int WAITROOM_PAGE = 2;
    static private final int GAMEPLAY_PAGE = 3;

    // Used for the multipurpose nature of character select handler.
    static private final int ANGLER_INDEX = 0;
    static private final int GOLEM_INDEX = 1;
    static private final int WELDER_INDEX = 2;

    // APIs which return menu pages.
    static private final String MAIN_MENU_API = "/forces-of-nature";
    static private final String INFO_MENU_API = "/forces-of-nature/info";
    static private final String WAITROOM_MENU_API = "/forces-of-nature/waitroom";
    static private final String GAMEPLAY_SCREEN_API = "/forces-of-nature/gameplay";

    // APIs which get / send game data.
    static private final String GAMEDATA_API = "/gamedata";
    static private final String GAMEMAP_API = "/game-map";
    static private final String PLAYER_ACTION_API = "/player-action";
    static private final String ADD_PLAYER_API = "/add-player";
    static private final String TOGGLE_READY_API = "/toggle-ready";
    static private final String REMOVE_PLAYER_API = "/remove-player";
    static private final String CHARACTER_SELECT_API = "/character-select";
    static private final String START_GAME_API = "/start-game";


    // == CONSOLE CONSTANTS [FOR READABILITY] ==================


    static public final int ERROR = 0;
    static public final int OKAY = 1;
    static public final int MESSAGE = 2;
    static public final int INTERESTING = 3;


    // == STATIC GSON [SERIALIZATION / DESERIALIZATION] ========


    static Gson gson = new Gson();


    // == CONSTRUCTOR [ + CREATION METHOD ] ====================


    public static void createGameServer(int requestedServerPort) {

        // Check the port availability (so that we don't open on a busy port).

        boolean portAvailible = false;

        // Essentially trying to run a server on requested port... seeing what happens.

        // (This is fancy "try-with-resources" syntax; auto closes objects when complete.)
        try (ServerSocket socket = new ServerSocket(requestedServerPort)) {

            // If there was no error binding this socket, then it is available.
            portAvailible = true;

        } catch (Exception e) {

            // Exception was thrown - likely because requested port was busy.
            System.out.println("port " + requestedServerPort + " is... OCCUPIED!");

        }

        // Continue with server creation.

        if (portAvailible) {

            // If the port is open, open the server on that port.
            GameServer gameServer = new GameServer(getLocalIPAddress(), requestedServerPort);

            // Note: if a program is run twice on the SAME machine, ports CAN be conflicting.

        }

    }

    public GameServer(String hostAddress, int port) {

        // Initialize fields.

        localHostAddress = hostAddress;
        serverPort = port;
        game = new Game();

        // Use try/catch in case something goes wrong.

        try {

            // Create the actual server object (this is a more "custom" server).

            httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);


            // -------------------------------------------------
            // -- APIs ("web access point" / "end point") ------
            // -------------------------------------------------


            // Assign static resource APIs (give resources like images, fonts, and other files).

            httpServer.createContext("/styles.css", new StaticFileHandler("frontend", "text/css"));
            httpServer.createContext("/functionality.js", new StaticFileHandler("frontend", "frontend/javascript"));
            httpServer.createContext("/images", new StaticFileHandler("images", "image/gif"));
            httpServer.createContext("/libraries", new StaticFileHandler("libraries", "font/otf"));

            // Assign page APIs (give webpages; technically can be combined with previous as html is a static file).

            httpServer.createContext(MAIN_MENU_API, new WebpageHandler(MAIN_MENU_PAGE));
            httpServer.createContext(INFO_MENU_API, new WebpageHandler(INFO_PAGE));
            httpServer.createContext(WAITROOM_MENU_API, new WebpageHandler(WAITROOM_PAGE));
            httpServer.createContext(GAMEPLAY_SCREEN_API, new WebpageHandler(GAMEPLAY_PAGE));

            // Assign game data APIs (different actions associated with game logic).

            httpServer.createContext(GAMEDATA_API, new GameHandler(game));
            httpServer.createContext(GAMEMAP_API, new GameMapHandler(game));
            httpServer.createContext(PLAYER_ACTION_API, new PlayerActionHandler(game));
            httpServer.createContext(ADD_PLAYER_API, new AddPlayerHandler(game));
            httpServer.createContext(TOGGLE_READY_API, new TogglePlayerReady(game));
            httpServer.createContext(REMOVE_PLAYER_API, new RemovePlayerHandler(game));
            httpServer.createContext(START_GAME_API, new GameStartupHandler(game));
            httpServer.createContext(CHARACTER_SELECT_API, new CharacterSelectHandler(game));

            // Not exactly sure what this is; guy on stack overflow had it here.

            httpServer.setExecutor(null);

            // Start the server; add message for success.

            System.out.println("╔══ SERVER CREATION =========================================");
            System.out.print("║ SERVER STATUS | ");
            httpServer.start();
            System.out.println("\u001B[32mSTARTED\u001B[0m");
            System.out.println("║ http://" + hostAddress + ":" + serverPort + "/forces-of-nature");

            // Add some more information on how to use (given different problems encountered during development).

            System.out.println("╠══ QUICK TIPS ==============================================");
            System.out.println("║");
            System.out.println("║ \u001B[37m> CHROME OS (W/ VIRTUAL MACHINE): TOGGLE PORT FORWARDING.\u001B[0m");
            System.out.println("║ \u001B[37m> AVOID USING FIREFOX OR SAFARI.. STICK TO CHROME!\u001B[0m");
            System.out.println("║ \u001B[37m> MAKE SURE TO DELETE OLD TABS.. THEY STILL HAUNT THE SERVER..\u001B[0m");
            System.out.println("║");
            System.out.println("╚══ SERVER OUTPUT BEGINS ====================================");

        } 
        catch (Exception e) {
            System.out.println("Server setup went wrong... " + e.getMessage());
        }
    }


    // == HELPER METHODS =======================================


    public static String getLocalIPAddress() {

        // NOTE: ChatGPT wrote this; I've commented what I understood, though explanations may be unclear.

        try {

            // Get all network interfaces (ip addresses, names, etc... found in the computer).
            
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Get all IP addresses for each network interface (grab only the IP addresses).
            
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {

                    // Iterate through each address of the computer.
            
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Ignore loop-back addresses like 127.0.0.1 (and ensure that its an IPv4 address).
            
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
            
                        // This first one should be the one we want.
                        // (Starts with 10.x.x.x, or 172.x.x.x, etc;)
                        //  This is the private IP address; only 
                        //  people within the same network see it.)
            
                    }
                }
            }
        } 
        catch (SocketException e) {
            System.out.println("Error while obtaining local host address.");
        }

        // In case sum wrong happens, return null.
        return null;
    }

    public static void reportToConsole(String message, int STATUS) {

        String colour;

        // Determine color based on message status.

        if (ERROR == STATUS) {
            colour = "\u001B[31m";
        } else if (OKAY == STATUS) { // OKAY
            colour = "\u001B[32m";
        } else if (INTERESTING == STATUS) {
            colour = "\u001B[34m";
        } else {
            colour = "\u001B[37m";
        }

        // Print formatted message (reset code at the end).

        System.out.println("║ " + colour + message + "\u001B[0m");
    }


    // == API [WEBPAGE - RETURN HTML] ==========================


    static class WebpageHandler implements HttpHandler {

        // Note: this class is technically unnecissary; could have just used static file handler instead (html is static).

        private final int requestedPage;

        public WebpageHandler(int requestedPage) {
            this.requestedPage = requestedPage;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            // Check request method (technically should
            // only get "GET", but better safe than sorry).

            if ("GET".equals(httpExchange.getRequestMethod())) {

                // Understand what page the user is going to, and give them that page.

                String path = "";

                switch (requestedPage) {

                    case MAIN_MENU_PAGE:
                        path = "frontend/main.html";
                        break;

                    case INFO_PAGE:
                        path = "frontend/info.html";
                        break;

                    case WAITROOM_PAGE:
                        path = "frontend/waitroom.html";
                        break;

                    case GAMEPLAY_PAGE:
                        path = "frontend/gameplay.html";
                        break;
                    default:
                        break;
                }

                // Convert to bytes, and send it to the client.

                byte[] htmlBytes = Files.readAllBytes(Paths.get(path));
                httpExchange.getResponseHeaders().set("Content-Type", "text/html");
                httpExchange.sendResponseHeaders(200, htmlBytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(htmlBytes);
                os.close();
            }
        }
    }


    // == API [STATIC FILE - RETURN PROJECT FILES] =============


    static class StaticFileHandler implements HttpHandler {

        private final String rootPath;
        private final String contentType;

        public StaticFileHandler(String rootPath, String contentType) {
            this.rootPath = rootPath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {

                // Get the requested file path (relative to the rootPath).
                String requestedPath = httpExchange.getRequestURI().getPath();
                requestedPath = requestedPath.replace("/images", "");
                requestedPath = requestedPath.replace("/libraries", "");
                Path filePath = Paths.get(rootPath, requestedPath);

                // Convert and send the file over to the client if it exists.
                if (Files.exists(filePath)) {
                    byte[] fileBytes = Files.readAllBytes(filePath);
                    httpExchange.getResponseHeaders().set("Content-Type", contentType);
                    httpExchange.sendResponseHeaders(200, fileBytes.length);
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(fileBytes);
                    os.close();
                } 
                
                // If the file doesn't exist, return a 404.
                else {
                    httpExchange.sendResponseHeaders(404, 0);
                    httpExchange.getResponseBody().close();
                }
            }
        }
    }


    // == API [GAME - RETURNS GAME OBJECT] =====================


    static class GameHandler implements HttpHandler {

        private final Game game;

        public GameHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {

                // Serialize game data into JSON.

                String jsonResponse = gson.toJson(game);

                // Convert to bytes and send it off!

                byte[] responseBytes = jsonResponse.getBytes();
                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }
    }

    static class GameMapHandler implements HttpHandler {

        private Game game;

        public GameMapHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {

                // Serialize the game map.

                String response = gson.toJson(game.getCurrentMap());

                // Send it over to the client.

                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();

            }
        }
    }


    // == API [PLAYER ACTION - GETS THE KEYBOARD INPUT] ========


    static class PlayerActionHandler implements HttpHandler {

        private final Game game;

        PlayerActionHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Get the content which was sent:
                // - (int) playerId
                // - (boolean[]) keys_pressed

                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Deserialize JSON to the pre-made PlayerAction object.

                PlayerAction playerAction = gson.fromJson(requestBody, PlayerAction.class);

                // Extract playerId from the object.

                int requestedPlayerId = playerAction.getPlayerID();

                // Locate the player with said id, and update their latestActionPerformed.

                game.getPlayerById(requestedPlayerId).setLatestPlayerActionPerformed(playerAction);

                // Let client know all is OK!

                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().close();

            }
        }
    }


    // == API [ADD PLAYER - MAKES PLAYER + RETURNS ID] =========


    static class AddPlayerHandler implements HttpHandler {

        private final Game game;

        public AddPlayerHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Read from the request body and make player.

                Player newPlayer = getPlayer(httpExchange);

                // Add player to the game object.

                game.addPlayer(newPlayer);

                // Get the new player id, report that the player was made.

                int newPlayerId = newPlayer.getId();

                // Set new player to spectator if the game is already running.

                if (game.isGameRunning()) {
                    newPlayer.toggleSpectating();
                    reportToConsole("NEW SPECTATOR (ID: " + newPlayerId + " | USERNAME: " + newPlayer.getUsername() + ").", INTERESTING);
                }
                else {
                    reportToConsole("CREATED PLAYER (ID: " + newPlayerId + " | USERNAME: " + newPlayer.getUsername() + ").", INTERESTING);
                }

                // Send back the id so that the browser can save it for when it sends the actions.

                String response = "{\"playerId\":" + newPlayerId + ", \"username\":\"" + newPlayer.getUsername() + "\"}";
                httpExchange.sendResponseHeaders(200, response.length()); 
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }

        private Player getPlayer(HttpExchange httpExchange) throws IOException {

            // Read content from the request; should read:
            // - (String) requested username.

            InputStream inputStream = httpExchange.getRequestBody();
            String requestedUsername = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Check that the requested username isn't blank; give "player-X" if anything.
            // This can still be cheesed though...

            if (requestedUsername.isEmpty()) {
                requestedUsername = "player-" + (Player.getPlayerCount() + 1);
            }

            // Check if the name already exists.

            for (Player player : game.getPlayers()) {

                // If someone is already using this name,
                // just put "fake-" in front of it to differentiate users.

                if (player.getUsername().equals(requestedUsername)) {
                    requestedUsername = "fake-" + requestedUsername;
                    // will repeat the "fake-" until the end.
                }
            }

            // Create the new player, add them to the game.

            return new Player(requestedUsername);
        }
    }

    static class TogglePlayerReady implements HttpHandler {

        private Game game;

        public TogglePlayerReady(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Read the request (get the playerId).
                
                InputStream inputStream = httpExchange.getRequestBody();
                int playerId = Integer.parseInt(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

                // Set the specific player to ready.

                game.getPlayerById(playerId).toggleReady();

                // Let client know all is OK!

                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().close();

            }
        }
    }


    // == API [REMOVE PLAYER - REMOVES PLAYER ==================


    static class RemovePlayerHandler implements HttpHandler {

        private Game game;

        public RemovePlayerHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            // Should technically only be post; but just to be safe we check.

            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Read the ID of the player who is going to be removed.

                InputStream inputStream = httpExchange.getRequestBody();
                String playerId = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Cast the input to an int, then remove them from the game.
                // * Keeping the player object locally to report data after.

                Player removedPlayer = game.removePlayer(Integer.parseInt(playerId));

                // Report player removal to console.

                reportToConsole("REMOVED PLAYER (ID: " + removedPlayer.getId() + " | USERNAME: " + removedPlayer.getUsername() + ").", INTERESTING);

                // Let client know all is OK!

                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().close();
            }

        }
    }


    // == API [CHARACTER SELECT - CHANGES CHARACTER] ===========

    static class CharacterSelectHandler implements HttpHandler {

        private Game game;

        public CharacterSelectHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Read the request body.

                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                // Get an object (was too lazy to make custom).

                JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);

                // Get the important content (playerId - which player to change. | character - which character to change them to).

                int playerId = Integer.parseInt(jsonObject.get("id").getAsString());
                int desiredCharacterIndex = Integer.parseInt(jsonObject.get("character").getAsString());

                // Get the specific player that were "casting".

                Player player = game.getPlayerById(playerId);

                // Understand which character is the desired switch.
                // Note: if we really wanted to optimize, we could save as string and do it when the game loads but whatever.

                switch (desiredCharacterIndex) {

                    case ANGLER_INDEX:
                        game.setPlayerTo(playerId, Angler.castTo(player));
                        reportToConsole("CHARACTER SELECTED (ID: " + playerId + " | " + player.getUsername() + " changed to ANGLER).", INTERESTING);
                        break;

                    case GOLEM_INDEX:
                        game.setPlayerTo(playerId, Golem.castTo(player));
                        reportToConsole("CHARACTER SELECTED (ID: " + playerId + " | " + player.getUsername() + " changed to GOLEM).", INTERESTING);
                        break;

                    case WELDER_INDEX:
                        game.setPlayerTo(playerId, Welder.castTo(player));
                        reportToConsole("CHARACTER SELECTED (ID: " + playerId + " | " + player.getUsername() + " changed to WELDER).", INTERESTING);
                        break;

                }

            }

            // Tell client we all good!

            httpExchange.sendResponseHeaders(200, 0);
            httpExchange.getResponseBody().close();
        }

    }


    // == API [GAME STARTUP - HELPS WITH GAME STARTING] ========


    static class GameStartupHandler implements HttpHandler {

        private final Game game;

        public GameStartupHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            // If a player requested to start the game... start it!

            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Check if the game is already running (prevent multi-clicking).

                if (!game.isGameRunning()) {

                    // If not, setup the game variables.

                    game.generateMap();
                    game.setSpawnPoints();

                    // Then start the game (and report it).

                    game.startGame();
                    reportToConsole("GAME HAS BEGUN", OKAY);
                } else {

                    // If the game is running already, just log it.

                    reportToConsole("GAME ALREADY RUNNING", ERROR);
                }

                // Let client know all is OK!

                httpExchange.sendResponseHeaders(200, 0);
                httpExchange.getResponseBody().close();

            }

            // If the player wants to check whether the game has started or not.

            else if ("GET".equals(httpExchange.getRequestMethod())) {

                // Just get the boolean "isRunning" & "players" from the game object.
                // Then package it into custom data class "WaitroomData".

                WaitroomData currentWaitroomData = new WaitroomData(game);
                String response = gson.toJson(currentWaitroomData);

                // Send the data to the client.

                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();
            }
        }
    }


}