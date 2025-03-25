package game;

// == IMPORTS =======
import player.Player;

// == WAITROOM_DATA =======
public class WaitroomData {


    // == INSTANCE VARIABLES ===================================


    boolean gameActive;

    Player[] playersOnline;


    // == CONSTRUCTOR ==========================================


    public WaitroomData(Game game) {
        gameActive = game.isGameRunning();
        playersOnline = game.getPlayers().toArray(new Player[0]);
    }

    
}
