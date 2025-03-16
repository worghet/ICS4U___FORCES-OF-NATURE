package player;

// == PLAYER ACTION =======
public class PlayerAction {


    // == INSTANCE VARIABLES [FIELDS] ==========================


    private int playerId;

    // TODO: Add "melee attack key" and "ranged attack" key
    private boolean[] keys_pressed = new boolean[] {false, false, false, false}; // left, right, down, up


    // == CONSTANTS [FOR READABILITY] ==========================


    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;


    // == GETTERS ==============================================


    public int getPlayerID() {
        return playerId;
    }

    public boolean[] getKeysPressed() {
        return keys_pressed;
    }

}
