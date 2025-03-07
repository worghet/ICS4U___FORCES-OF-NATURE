public class Angler extends Player {

    public Angler(String username){
        super(username);
        Player.FRICTION = 0.40; //lower than base player

    }

    public void bite(){
        double[] pos = this.getPosition();
        

    }//close attack

    public void spit(){

    }
    
}
