package player;

public class Angler extends Player {

    public Angler(String username){
        super(username);
        Player.FRICTION = 0.40; //lower than base player

    }

    // ovverride "melee" attack from Player?
    public void bite(){
        double[] pos = this.getPosition();
        

    }//close attack

    // ovverride "range" attack from Player?
    public void spit(){

    }
    
}
