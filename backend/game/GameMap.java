package game;

// == IMPORTS ==========
import java.util.Random;

// == GAME MAP =======
public class GameMap {


    // == CONSTANTS =============================================

    
    static int DEEPSEA = 0;
    static int CAVE = 1;
    static int INDUSTRY = 2;


    // == INSTANCE VARIABLES [FIELDS] ==========================


    // Do not remove; used by javascript to render background image.
    private int backgroundIndex; 
    
    // Where the ground will be.
    private int groundY; 
    
    // Other stuff.
    private double[][] spawnPoints; // [X, Y]
    private Island[] islands; 


    // == LOADER METHOD ========================================


    public static GameMap loadMap(int MAP_INDEX) {

        // Create a new game map object, assign the desired map as its index (for js to render).
        GameMap desiredMap = new GameMap();
        desiredMap.backgroundIndex = MAP_INDEX;


        // *Note: we originally wanted to do 3 unique maps. 
        //        But we settled for 3 backgrounds, 1 map layout.


        // Add ground Y; where is the ground of this map.
        desiredMap.groundY = 500; 

        // Add 3 collidible islands.
        desiredMap.islands = new Island[3]; 

        // Set number of spawn points (3 in this map).
        desiredMap.spawnPoints = new double[3][2];

        // Lower-left island: island rendering + spawn point.
        desiredMap.islands[0] = new Island(125, 400, 600);
        desiredMap.spawnPoints[0][0] = 386.5; //x
        desiredMap.spawnPoints[0][1] = 55; // y

        // Lower-right island: island rendering + spawn point.
        desiredMap.islands[1] = new Island(1030, 400, 600);
        desiredMap.spawnPoints[1][0] = 845;
        desiredMap.spawnPoints[1][1] = 55;

        // Top-center island: island rendering + spawn point.
        desiredMap.islands[2] = new Island(600, 200, 550);
        desiredMap.spawnPoints[2][0] = 1303.5;
        desiredMap.spawnPoints[2][1] = 55;

        // Return the initialized map.
        return desiredMap;
    }


    // == GETTER METHODS =======================================


    public int getGroundY() {
        return groundY;
    }

    public Island[] getIslands() {
        return islands;
    }

    public double[] getSpawnPoint() {

        // Return a random spawn point in the map.
        int randomSpawnPointIndex = new Random().nextInt(spawnPoints.length);
        return spawnPoints[randomSpawnPointIndex];

    }

    
}
