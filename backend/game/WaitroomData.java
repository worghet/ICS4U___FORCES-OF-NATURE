package game;

import player.Player;

// data object (like player action)
public class WaitroomData {

    boolean gameActive; // game ON or OFF?

    Player[] playersOnline; // players that are in the lobby

    public WaitroomData(Game game) {
        gameActive = game.isGameRunning();
        playersOnline = game.getPlayers().toArray(new Player[0]);

    }

}
