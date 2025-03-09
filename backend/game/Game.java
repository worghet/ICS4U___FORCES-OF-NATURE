package game;

// == IMPORTS =============
import java.util.ArrayList;
import java.util.TimerTask;
import player.Projectile;
import java.util.Timer;
import player.Player;

// == GAME ========
public class Game {


    // == INSTANCE VARIABLES [FIELDS] ==========================


    private ArrayList<Player> players = new ArrayList<Player>();
    private boolean gameRunning;


    // == CONSTRUCTOR ==========================================


    public Game() {}


    // == GENERAL METHODS ======================================


    public void startGame() {

        // Set game true (since game should be running now).
        gameRunning = true;

        // Use a timer to assign a timed task (game loop).
        Timer timer = new Timer();

        // Set timer schedule variables (These are in milliseconds).
        int delay = 0; // Since delay is 0, task will immediately begin.
        int period = 12; // How often should the task be done? In this case, every 12ms (can be changed to 10 or 15).

        // Set the code to run in the task.
        TimerTask gameLoop = new TimerTask() {
            @Override
            public void run() {

                // -- REPLACE WITH MATT'S UPDATE METHOD -----

                for (Player aPlayer : players) {
                    aPlayer.updatePosition();
                }

                // -- REPLACE WITH MATT'S UPDATE METHOD -----

            }
        };

        // After having defined the task, start it.
        timer.scheduleAtFixedRate(gameLoop, delay, period);
    }

    // * Matt's method; not commenting / touching it. *
    public void performLoop(){
        for(int i = 0; i< players.size(); i++){

            Player temp = players.get(i);
//            temp.update();//checks if player has moved, updates
            //temp.getLatestActionPerformed();

            //if player is long range attacking, creates projectile
            if(temp.getIsAttacking()){
                //will move this in the character subclasses i think
                Projectile shot = new Projectile(temp);//inputs player obj that attacked to get x,y, & type (ex. Player.Welder)

                //bad will stop the loop that checks for other characters movements??
//                while(shot.getActive()){
//                    shot.somethingHit(game.Game.this.players);
//                }

            }
        }
    }

    // * Matt's method; not commenting / touching it. *
    public void playerNear(Player attacker) {

        //checks whther or not player is close enough for a close range attack

        for (int i = 0; i < players.size(); i++) {
            Player temp = players.get(i);
            if (temp != attacker) {
                double[] attackerPos = attacker.getPosition();
                double[] check = temp.getPosition();


                //checking position of attacker vs others, not done
                //if(attackerPos[0][0] + check[0][0]){

                //}

            }
        }

    }


    // == MUTATOR METHODS ======================================


    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
    }

    public void removePlayer(int playerId) {

        // Use id to remove the player (as indexes
        // are unreliable when people can come and
        // leave in a single server instance).

        for (Player player : players) {
            if (player.getId() == playerId) {
                players.remove(player);
                break;
            }
        }
    }

    public void resetGame() {
        this.gameRunning = false;
        // clear all players / reset ID status --> set static thing to 0.
    }


    // == GETTER METHODS =======================================


    public boolean isGameRunning() {
        return gameRunning;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }


}
