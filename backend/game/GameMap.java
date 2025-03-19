package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class GameMap {

    // Constants for different map types
    static int DEEPSEA = 0;
    static int CAVE = 1;
    static int INDUSTRY = 2;

    // Instance variables for the map
    private int backgroundIndex;
    private int groundY;
    private double[][] spawnPoints; // X, Y
    private Island[] islands;

    // Load the map based on the given index
    public static GameMap loadMap(int MAP_INDEX) {

        GameMap desiredMap = new GameMap();

        switch (MAP_INDEX) {
            case 0: // DEEPSEA
                desiredMap.backgroundIndex = DEEPSEA;
                desiredMap.groundY = 500;

                desiredMap.islands = new Island[3]; // how many islands here
                desiredMap.islands[0] = new Island(1000, 400, 300);
                desiredMap.islands[1] = new Island(300, 500, 400);
                desiredMap.islands[2] = new Island(600, 200, 150);


                desiredMap.spawnPoints = new double[1][2];
                desiredMap.spawnPoints[0][0] = 1150;
                desiredMap.spawnPoints[0][1] = 400;

                desiredMap.spawnPoints = new double[2][2];
                desiredMap.spawnPoints[0][0] = 500;
                desiredMap.spawnPoints[0][1] = 600;

                desiredMap.spawnPoints = new double[3][2];
                desiredMap.spawnPoints[0][0] = 700;
                desiredMap.spawnPoints[0][1] = 300;


                System.out.println("loading deepsea");
                break;
            case 1: // CAVE
                desiredMap.backgroundIndex = CAVE;
                System.out.println("loading cave");
                desiredMap.groundY = 500;

                desiredMap.islands = new Island[3]; // how many islands here
                desiredMap.islands[0] = new Island(1000, 400, 300);
                desiredMap.islands[1] = new Island(300, 500, 400);
                desiredMap.islands[2] = new Island(600, 200, 150);


                desiredMap.spawnPoints = new double[1][2];
                desiredMap.spawnPoints[0][0] = 1150;
                desiredMap.spawnPoints[0][1] = 400;

                desiredMap.spawnPoints = new double[2][2];
                desiredMap.spawnPoints[0][0] = 500;
                desiredMap.spawnPoints[0][1] = 600;

                desiredMap.spawnPoints = new double[3][2];
                desiredMap.spawnPoints[0][0] = 700;
                desiredMap.spawnPoints[0][1] = 300;



                break;
            case 2: // INDUSTRY
                desiredMap.backgroundIndex = INDUSTRY;
                desiredMap.groundY = 500;

                desiredMap.islands = new Island[3]; // how many islands here
                desiredMap.islands[0] = new Island(1000, 400, 300);
                desiredMap.islands[1] = new Island(300, 500, 400);
                desiredMap.islands[2] = new Island(600, 200, 150);


                desiredMap.spawnPoints = new double[1][2];
                desiredMap.spawnPoints[0][0] = 1150;
                desiredMap.spawnPoints[0][1] = 400;

                desiredMap.spawnPoints = new double[2][2];
                desiredMap.spawnPoints[0][0] = 500;
                desiredMap.spawnPoints[0][1] = 600;

                desiredMap.spawnPoints = new double[3][2];
                desiredMap.spawnPoints[0][0] = 700;
                desiredMap.spawnPoints[0][1] = 300;


                System.out.println("loading industry");
                break;
            default:
                System.out.println("idk what map ts is");
        }

        // Set other properties (groundY, spawn points, etc.)
        // Assuming there is some logic here to configure each map type's unique properties

        return desiredMap;
    }

    public int getGroundY() {
        return groundY;
    }

    public Island[] getIslands() {
        return islands;
    }

    public double[] getSpawnPoint() {

        int randomSpawnPointIndex = new Random().nextInt(spawnPoints.length);
        System.out.println("got spawn points.. player gonna spwan");
        return spawnPoints[randomSpawnPointIndex];


    }

}
