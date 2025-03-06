import java.util.HashMap;


/**
 * this is what i like to call a DATA CLASS --> its a class that solely exists to CONVEY DATA (i.e. no exclusive methods)
 * 
 * this class is only built from the data sent from javascript (deserialization from JSON)
 * 
 * an instance of this class will inform the game of what a player is doing at the time of the game's timed update cycle 
 * (pretty much the stuff needed to calculate physics, etc)
 * 
 * objects of this class will only be made in the GameServer class, but will be interpreted in the Game class.
 */
public class PlayerAction {
    

    /*
     * player ID will be used to determine which player's action this is.. (browser/webpage will save the players id and report keyboard inputs WITH this id) 
     * if you haven't seen yet, I've added id to player class too.
     * 
     * 
     * (NOTE TO SELF -- THIS WILL BE USED TO SET CORRECT PLAYER'S <LATEST ACTION> IN THE GAME SERVER)
     * 
     */
    private int playerId; // or username, but if 2 people have the same username it would be bad..
    


    /**
     * This can be represented by: actions_performed = {left: false, 
     *                                                  right: true, 
     *                                                  down: false, 
     *                                                  up: true}
     * 
     *                                                  The following will be added later 
     *                                                  *attack_ranged
     *                                                  *attack_melee 
     * 
     * in the example, the player is trying to jump and is moving right.
     * The game's updating cycle should reflect these actions
     * 
     */
    private HashMap<String, Boolean> actions_performed; // Boolean can be null, but it should never be received as null.


    // getters

    public int getPlayerID() {
        return playerId;
    }

    public HashMap<String, Boolean> getKeysPressed() {
        return actions_performed;
    }

}
