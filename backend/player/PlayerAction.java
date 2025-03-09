package player;

/**
 * This is what I like to call a DATA CLASS --> it is a class that solely exists to CONVEY DATA (i.e. no exclusive methods)
 * this class is only built from the data sent from javascript (deserialization from JSON).
 * An instance of this class will inform the game of what a player is doing at the time of the game's timed update cycle
 * (pretty much the stuff needed to calculate physics, etc).
 * Objects of this class will only be made in the GameServer class, but will be interpreted in the Game class.
 */
public class PlayerAction {


    // == INSTANCE VARIABLES [FIELDS] ==========================


    private int playerId; // or username, but if 2 people have the same username it would be bad..

    // TODO: Add "melee attack key" and "ranged attack" key
    private boolean[] keys_pressed = new boolean[]{false, false, false, false}; // left, right, down, up


    // == CONSTANTS [FOR READABILITY] ==========================


    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;


    // == GETTERS ==============================================


    public int getPlayerID() {
        return playerId;
    }

    public boolean[] getKeys_Pressed() {
        return keys_pressed;
    }

}
