
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GameServer {

    // == VARIABLES =================================

    // Server info
    private int serverPort;
    private String localHostAddress;
    private HttpServer httpServer;
    private Game game;

    // api constants (for readability)
    static private final int GAMEPLAY_PAGE = 0;
    static private final int CHARACTER_SELECT_PAGE = 1;
    static private final int MAIN_MENU_PAGE = 2;

    // JSON serialization/deserialization object
    static Gson gson = new Gson();

    // == METHODS =================================

    // method to use from outside of ts class
    public static void createGameServer(int requestedServerPort) {

        // 1. CHECK PORT AVAILIBILITY

        boolean portAvailible = false;

        // (essentially trying to run a server on that port.. seeing what happens)
        try (ServerSocket socket = new ServerSocket(requestedServerPort)) {
            // System.out.println("port " + requestedServerPort + " is... AVAILIBLE!");
            portAvailible = true;
        } catch (IOException e) {
            // print port is busy
            System.out.println("port " + requestedServerPort + " is... OCCUPIED!");
        }

        // continue with gameserver creation
        if (portAvailible) {
            GameServer gameServer = new GameServer(getLocalIPAddress(), requestedServerPort);
            // gameServer.startTimer() (todo)
        }

        // GAME CREATION

    }

    // primary constructor
    public GameServer(String hostAddress, int port) {

        // set instance variables
        localHostAddress = hostAddress;
        serverPort = port;
        game = new Game();

        // use try to catch errors
        try {
            httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);

            // ADD STATIC RESOURCE API ---------------------
            httpServer.createContext("/styles.css", new StaticFileHandler("frontend/styles.css", "text/css"));
            httpServer.createContext("/functionality.js",
                    new StaticFileHandler("frontend/functionality.js", "frontend/javascript"));
            // todo make animation frame getters, etc

            // ADD PAGE APIS -------------
            httpServer.createContext("/forces-of-nature", new WebpageHandler(MAIN_MENU_PAGE));
            httpServer.createContext("/forces-of-nature/waitroom", new WebpageHandler(CHARACTER_SELECT_PAGE));
            httpServer.createContext("/forces-of-nature/gameplay", new WebpageHandler(GAMEPLAY_PAGE));

            // GAME DATA APIS --------------
            httpServer.createContext("/gamedata", new GameHandler(game));
            httpServer.createContext("/player-action", new PlayerActionHandler(game));
            httpServer.createContext("/add-player", new AddPlayerHandler(game));
            httpServer.createContext("/start-game", new GameStartupHandler(game));

            // GAME RELATED APIS --------------------------------

            // idk what this is
            httpServer.setExecutor(null);
            System.out.print("Starting server..");
            httpServer.start();
            System.out.println("STARTED!");
            System.out.println("http://" + hostAddress + ":" + serverPort + "/forces-of-nature/gameplay" +
                    "\n(IF FROM CHROMEBOOK, USE IP ADDRESS)\n----------------------------------------");

            // SERVER STARTED, BEGIN GAME
//            game.addPlayer(new Player("playtester"));

        } catch (Exception exception) {
            System.out.println("Server setup went wrong... " + exception.toString());
        }
    }

    // private startTimer {
    // // create runnable
    // }

    public static String getLocalIPAddress() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Get all IP addresses for each network interface
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Ignore loopback addresses like 127.0.0.1
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress(); // Return the first valid IP address found
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --------------------------------

    static class WebpageHandler implements HttpHandler {

        private int requestedPage;

        public WebpageHandler(int requestedPage) {
            this.requestedPage = requestedPage;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            // technically shouldnt get post anytime, but did if just in case
            if ("GET".equals(httpExchange.getRequestMethod())) {

                // if message list not empty; load it (
                // serve html

                // get byt file path
                System.out.println("giving page..");

                String path = "";

                switch (requestedPage) {
                    case GAMEPLAY_PAGE:
                        path = "frontend/game-screen.html";
                        break;

                    case MAIN_MENU_PAGE:
                        path = "frontend/main-screen.html";
                        break;
                    case CHARACTER_SELECT_PAGE:
                        path = "frontend/character-select-screen.html";
                        break;
                    default:
                        break;
                }

                System.out.println(path + ".. GIVEN!");
                byte[] htmlBytes = Files.readAllBytes(Paths.get(path));
                httpExchange.getResponseHeaders().set("Content-Type", "text/html");

                // send it
                httpExchange.sendResponseHeaders(200, htmlBytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(htmlBytes);
                os.close();
            }

        }

    }

    static class StaticFileHandler implements HttpHandler {
        private String filePath;
        private String contentType;

        public StaticFileHandler(String filePath, String contentType) {
            this.filePath = filePath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            httpExchange.getResponseHeaders().set("Content-Type", contentType);
            httpExchange.sendResponseHeaders(200, fileBytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        }
    }

    static class GameHandler implements HttpHandler {

        private Gson gson = new Gson();
        private Game game;

        public GameHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                String jsonResponse = gson.toJson(game);
                byte[] responseBytes = jsonResponse.getBytes();

                httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = httpExchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }

    }

    static class PlayerActionHandler implements HttpHandler {

//        static int counter = 0;
        private Game game;

        PlayerActionHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                // deserialize contents, update player's last action performed

                InputStream inputStream = httpExchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//                System.out.println("got data");

                // Deserialize JSON to an object
                PlayerAction playerAction = gson.fromJson(requestBody, PlayerAction.class);
//                System.out.println(playerAction.toString());

                //
                int requestedPlayerId = playerAction.getPlayerID();
//                System.out.println(game.getPlayers().size());
                for (Player player: game.getPlayers()) {
                    if (player.getId() == requestedPlayerId) {
                        player.setLatestPlayerActionPerformed(playerAction);

//                        System.out.println(player.getUsername() + ": " + Arrays.toString(playerAction.getKeys_Pressed()));
                    }
                }

                String response = "OK";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
//                System.out.println(response);
            }
        }

    }

    static class AddPlayerHandler implements HttpHandler {

        private Game game;

        public AddPlayerHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {


                Player newPlayer = new Player();
                game.addPlayer(newPlayer);
                int newPlayerId = newPlayer.getId();

                System.out.println("Player with id " + newPlayerId + " created.");

                String response = String.valueOf(newPlayerId);

                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();

            }
        }
    }

    static class GameStartupHandler implements HttpHandler {

        private Game game;

        public GameStartupHandler(Game game) {
            this.game = game;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("POST".equals(httpExchange.getRequestMethod())) {

                game.startGame();
                System.out.println("GAME STARTED");

                String response = "OK";

                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();
            }
            else if ("GET".equals(httpExchange.getRequestMethod())) {

                String response = String.valueOf(game.isGameRunning());
//                System.out.println(response);

                httpExchange.sendResponseHeaders(200, response.length()); // Correct length
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8)); // Send as UTF-8 string
                os.close();
            }
        }
    }
}
