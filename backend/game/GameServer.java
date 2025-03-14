package game;

// == IMPORTS =============================

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Paths;

import player.PlayerAction;
import player.Player;

import java.util.*;
import java.net.*;
import java.io.*;

// == GAME_SERVER =====
public class GameServer {


    // == INSTANCE VARIABLES [FIELDS] ==========================


    private final String localHostAddress; // IP address.
    private HttpServer httpServer; // Server object.
    private final int serverPort; // What port server hosted on.
    private Game game; // Game for communication and updating.


    // == API CONSTANTS [FOR READABILITY] ======================


    // TODO: add "info-page" / reconfigure indexes.
    static private final int MAIN_MENU_PAGE = 0;
    static private final int INFO_PAGE = 1;
    static private final int WAITROOM_PAGE = 2;
    static private final int GAMEPLAY_PAGE = 3;

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

        // (This is fancy "try-with-resources" syntax; auto closes objects when complete.
        try (ServerSocket socket = new ServerSocket(requestedServerPort)) {

            // If there was no error binding this socket, then it is available.
            portAvailible = true;

        } catch (IOException e) {

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

            // Create the actual server object.

            httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);

            // -------------------------------------------------
            // -- APIs ("web access point" / "end point") ------
            // -------------------------------------------------

            // Assign static resource APIs (give resources like images, and other files).

            httpServer.createContext("/styles.css", new StaticFileHandler("frontend/styles.css", "text/css"));
            httpServer.createContext("/functionality.js", new StaticFileHandler("frontend/functionality.js", "frontend/javascript"));
            // TODO: Add "animation-frame" getters, etc. (Unless that will be in the player object to load from.)

            // Assign page APIs (give webpages; technically can be combined with previous as html is a static file).

            httpServer.createContext("/forces-of-nature", new WebpageHandler(MAIN_MENU_PAGE));
            httpServer.createContext("/forces-of-nature/info", new WebpageHandler(INFO_PAGE));
            httpServer.createContext("/forces-of-nature/waitroom", new WebpageHandler(WAITROOM_PAGE));
            httpServer.createContext("/forces-of-nature/gameplay", new WebpageHandler(GAMEPLAY_PAGE));

            // Assign game data APIs (different actions associated with game logic).

            httpServer.createContext("/gamedata", new GameHandler(game));
            httpServer.createContext("/player-action", new PlayerActionHandler(game));
            httpServer.createContext("/add-player", new AddPlayerHandler(game));
            httpServer.createContext("/remove-player", new RemovePlayerHandler(game));
            httpServer.createContext("/start-game", new GameStartupHandler(game));

            // Not exactly sure what this is; guy on stack overflow had it here.

            httpServer.setExecutor(null);

            // Start the server.

            System.out.println("╔══ SERVER CREATION =========================================");
            System.out.print("║ SERVER STATUS | ");
            httpServer.start();
            System.out.println("\u001B[32mSTARTED\u001B[0m");

            // TODO: Change the link to connect to the primary page (main menu).
            System.out.println("║ http://" + hostAddress + ":" + serverPort + "/forces-of-nature");

            // Add some more information on how to use (given different problems encountered.

            System.out.println("╠══ QUICK TIPS ==============================================");
            System.out.println("║");
            System.out.println("║ \u001B[37m> CHROME OS (W/ VIRTUAL MACHINE): TOGGLE PORT FORWARDING.\u001B[0m");
            System.out.println("║ \u001B[37m> AVOID USING FIREFOX OR SAFARI.. STICK TO CHROME!\u001B[0m");
            System.out.println("║ \u001B[37m> DIFFERENT IDES RESULT MAY REDUCE SPEEDS; INTELLIJ IS THE BEST.\u001B[0m");
            System.out.println("║");
            System.out.println("╚══ SERVER OUTPUT BEGINS ====================================");

        } catch (Exception exception) {
            System.out.println("Server setup went wrong... " + exception);
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

                    // Ignore loop-back addresses like 127.0.0.1 (those who know).
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                        // This first one should be the one we want.
                        // (Starts with 10.x.x.x, or 172.x.x.x, etc;
                        //  really depends on where this is being run.)
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("Error while obtaining local host address.");
        }

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


                // Let console know that someone is accessing the server.
                reportToConsole("A user just requested: " + path, MESSAGE);


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

        private final String filePath;
        private final String contentType;

        public StaticFileHandler(String filePath, String contentType) {
            this.filePath = filePath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {

                // Convert and send the file over to the client.

                byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                httpExchange.getResponseHeaders().set("Content-Type", contentType);
                httpExchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(fileBytes);
                os.close();
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

                // Deserialize JSON to the pre-made PlayerAction object;

                PlayerAction playerAction = gson.fromJson(requestBody, PlayerAction.class);

                // Extract playerId from the object.

                int requestedPlayerId = playerAction.getPlayerID();

                // Locate the player with said id, and update their latestActionPerformed.

                for (Player player : game.getPlayers()) {
                    if (player.getId() == requestedPlayerId) {
                        player.setLatestPlayerActionPerformed(playerAction);
                    }
                }

                // Tell the client that we (the back end) got the information.

                String response = "OK";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        }
    }


    // == API [ADD PLAYER - MAKES PLAYER + RETURNS ID ==========


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
                game.addPlayer(newPlayer);

                // Get the new player id, print that the player was made.

                int newPlayerId = newPlayer.getId();
                reportToConsole("CREATED PLAYER (ID: " + newPlayerId + " | USERNAME: " + newPlayer.getUsername() + ").", INTERESTING);

                // Send back the id so that the browser can save it for when it sends the actions.

                String response = String.valueOf(newPlayerId);
                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();

            }
        }

        private Player getPlayer(HttpExchange httpExchange) throws IOException {
            InputStream inputStream = httpExchange.getRequestBody();
            String requestedUsername = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // check that username dont already exist

            if (requestedUsername.isEmpty()) {
                // if user though they were slick putting in nothing
                requestedUsername = "nameless";
            }

            // check if already exists
            for (Player player : game.getPlayers()) {
                if (player.getUsername().equals(requestedUsername)) {
                    requestedUsername = "fake-" + requestedUsername;
                    // will repeat the "fake-" until the end.
                }
            }

            // Create the new player, add them to the game.

            return new Player(requestedUsername);
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

            if ("POST".equals(httpExchange.getRequestMethod())) {

                InputStream inputStream = httpExchange.getRequestBody();
                String playerId = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                Player removedPlayer = game.removePlayer(Integer.parseInt(playerId));

                reportToConsole("REMOVED PLAYER (ID: " + removedPlayer.getId() + " | USERNAME: " + removedPlayer.getUsername() + ").", INTERESTING);

                String response = "OK";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }

        }
    }


    // == API [GAME STARTUP - HELPS WITH GAME STARTING =========


    static class GameStartupHandler implements HttpHandler {

        private final Game game;

        public GameStartupHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            // If a player requested to start the game... start it!
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // Start the game (loop) in the game object.

                if (!game.isGameRunning()) {
                    game.startGame();
                    reportToConsole("GAME HAS BEGUN", OKAY);
                } else {
                    reportToConsole("GAME ALREADY RUNNING", ERROR);
                }

                // Send message back to client that all is good now.

                String response = "OK";
                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();
            }

            // If the player wants to check whether the game has started or not.
            else if ("GET".equals(httpExchange.getRequestMethod())) {

                // Just get the boolean "isRunning" from the game object.
                WaitroomData currentWaitroomData = new WaitroomData(game);
                String response = gson.toJson(currentWaitroomData);

                // Send that to the client.

                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();
            }
        }
    }


}