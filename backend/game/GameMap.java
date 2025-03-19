package game;

import java.awt.Point;
import java.util.ArrayList;

public class GameMap {

    // Constants for different map types
    static int DEEPSEA = 0;
    static int CAVE = 1;
    static int INDUSTRY = 2;

    // Instance variables for the map
    private int backgroundIndex;
    private int groundY;
    private ArrayList<Point> spawnPoints;
    private ArrayList<Island> islands;

    // Load the map based on the given index
    public static GameMap loadMap(int MAP_INDEX) {
        GameMap desiredMap = new GameMap();

        switch (MAP_INDEX) {
            case 0: // DEEPSEA
                desiredMap.backgroundIndex = DEEPSEA;
                System.out.println("loading deepsea");
//                desiredMap.set
                break;
            case 1: // CAVE
                desiredMap.backgroundIndex = CAVE;
                System.out.println("loading cave");

                break;
            case 2: // INDUSTRY
                desiredMap.backgroundIndex = INDUSTRY;
                System.out.println("loading industry");
                break;
            default:
                System.out.println("idk what map ts is");
        }

        // Set other properties (groundY, spawn points, etc.)
        // Assuming there is some logic here to configure each map type's unique properties

        return desiredMap;
    }

    public void addIsland(Island island) {
        if (islands == null) {
            islands = new ArrayList<>();
        }
        islands.add(island);
    }

    public ArrayList<Island> getIslands() {
        return islands;
    }

}
