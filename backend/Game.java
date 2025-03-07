

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.Timer;

public class Game {
    private ArrayList<Player> players = new ArrayList<Player>();
    private boolean gameRunning = true; //whether or not game is active

    //constructor
    public Game() {
    }

    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
    }

    public void removePlayer(int playerId) {
        for (Player player : players) {
            if (player.getId() == playerId) {
                players.remove(player);
                break;
            }
        }
    }

    public void startGame() {
        Timer timer = new Timer();
        TimerTask gameLoop = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Game loop started...");
                // performLoop();
                for (Player aPlayer : players) {
                    aPlayer.updatePosition();
//                    System.out.println("moved " + aPlayer.getId() + "right 1");
                }

                // should be a condition to check gameRunning
//                System.out.println("Game loop stopped...");
            }
        };

        long delay = 0; // Start immediately
        long period = 1000; // Run every 50 milliseconds (adjust as needed)

        timer.scheduleAtFixedRate(gameLoop, delay, period);
    }


    //checks position, attack status (+implements attacks), of all players

    // ====== COMMENTED THE FOLLOWING CUZ IT ISNT PROPER YET!

//    public void performLoop(){
//        for(int i = 0; i< players.size(); i++){
//
//            Player temp = players.get(i);
//            temp.update();//checks if player has moved, updates
//            //temp.getLatestActionPerformed();
//
//            //if player is long range attacking, creates projectile
//            if(temp.getIsAttacking()){
//                //will move this in the character subclasses i think
//                Projectile shot = new Projectile(temp);//inputs player obj that attacked to get x,y, & type (ex. Welder)
//
//                //bad will stop the loop that checks for other characters movements??
//                while(shot.getActive()){
//                    shot.somethingHit(Game.this.players);
//                }
//
//            }
//        }
//    }


    public void resetGame() {
        this.gameRunning = false;
    }

    public Game toJson() {
        return this.toJson();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }


    //checks whther or not player is close enough for a close range attack
    public void playerNear(Player attacker) {
        for (int i = 0; i < players.size(); i++) {
            Player temp = players.get(i);
            if (temp != attacker) {
                int[] attackerPos = attacker.getPosition();
                int[] check = temp.getPosition();


                //checking position of attacker vs others, not done
                //if(attackerPos[0][0] + check[0][0]){

                //}

            }
        }

    }
}//end of Game Class
