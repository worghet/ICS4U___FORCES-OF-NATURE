

import java.util.ArrayList;
//import java.util.Timer;

public class Game {
    ArrayList<Player> players  = new ArrayList<Player>();
    boolean gameRunning = true; //whether or not game is active
    

    //constructor
    public Game(){

    }

    //checks position, attack status (+implements attacks), of all players
    public void performLoop(){
        for(int i = 0; i< players.size(); i++){

            Player temp = players.get(i);
            temp.update();//checks if player has moved, updates
            //temp.getLatestActionPerformed();

            //if player is long range attacking, creates projectile
            if(temp.getIsAttacking()){
                //will move this in the character subclasses i think
                Projectile shot = new Projectile(temp);//inputs player obj that attacked to get x,y, & type (ex. Welder)

                //bad will stop the loop that checks for other characters movements??
                while(shot.getActive()){
                    shot.somethingHit(Game.this.players);
                }
                
            }

         
            
        }
    }


    public void resetGame(){
        this.gameRunning = false;
    }

    public Game toJson() {
        return this.toJson();
    }

    public Player[] getPlayers(){
        return players.toArray(new Player[players.size()]);
    }



    //checks whther or not player is close enough for a close range attack
    public void playerNear(Player attacker){
        for(int i = 0; i< players.size(); i++){
            Player temp = players.get(i);
            if(temp != attacker){
                int [] attackerPos = attacker.getPosition();
                int [] check = temp.getPosition();
                

                //checking position of attacker vs others, not done
                //if(attackerPos[0][0] + check[0][0]){

                //}

            }
        }

    }
}//end of Game Class
