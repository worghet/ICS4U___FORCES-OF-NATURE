package backend;
import java.util.ArrayList;
//import java.util.Timer;

public class Game {
    ArrayList<Player> players;
    //Timer timer;
    boolean gameRunning = true; //whether or not game is active
    

    //constructor
    public Game(){
        this.players = new ArrayList<Player>();
        //this.timer = new Timer();

    }

    public void performLoop(){
        for(int i = 0; i< players.size(); i++){
            players.get(i).updatePosition();//checks if player has moved, updates
         
            
        }

    }

    public void resetGame(){
        this.gameRunning = false;
    }




}//end of Game Class
