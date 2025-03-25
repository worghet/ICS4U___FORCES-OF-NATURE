package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
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
        desiredMap.backgroundIndex = MAP_INDEX;


        desiredMap.groundY = 500; // + 70 for player

        desiredMap.islands = new Island[3]; // how many islands here

        // left low
        desiredMap.islands[0] = new Island(125, 400, 600);

        // right low
        desiredMap.islands[1] = new Island(1030, 400, 600);

        // top center
        desiredMap.islands[2] = new Island(600, 200, 550);


        // set number of spawn points (3 in this map)
        desiredMap.spawnPoints = new double[3][2];

        // left low
        desiredMap.spawnPoints[0][0] = 386.5; //x
        desiredMap.spawnPoints[0][1] = 55; // y

        // right low
        desiredMap.spawnPoints[1][0] = 845;
        desiredMap.spawnPoints[1][1] = 55;
//
        desiredMap.spawnPoints[2][0] = 1303.5;
        desiredMap.spawnPoints[2][1] = 55;

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
//        System.out.println("got spawn points.. player gonna spwan -->" +  spawnPoints[randomSpawnPointIndex][0] + " "+  spawnPoints[randomSpawnPointIndex][1]);
        return spawnPoints[randomSpawnPointIndex];


    }

}
