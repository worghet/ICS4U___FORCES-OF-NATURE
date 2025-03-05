package backend;
import java.util.ArrayList;
//import java.util.Timer;

public class Game {
    ArrayList<Player> players;
    boolean gameRunning = true; //whether or not game is active
    

    //constructor
    public Game(){
        this.players = new ArrayList<Player>();
        

    }

    //checks position, attack status (+implements attacks), of all players
    public void performLoop(){
        for(int i = 0; i< players.size(); i++){

            Player temp = players.get(i);
            temp.updatePosition();//checks if player has moved, updates

            //if player is long range attacking, creates projectile
            if(temp.isLongAttack()){
                Projectile shot = new Projectile(temp);//inputs player obj that attacked to get x,y, & type (ex. Welder)
                while(shot.getIsMoving()){
                    shot.somethingHit();

                }
                
            }
         
            
        }
    }

    public void resetGame(){
        this.gameRunning = false;
    }




}//end of Game Class
