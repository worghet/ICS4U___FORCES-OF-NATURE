package game;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class Map {

    // Constants for different map types
    private final int DEEPSEA = 0;
    private final int CAVE = 1;
    private final int INDUSTRY = 2;

    // Instance variables for the map
    private Image backgroundResource;
    private int groundY;
    private ArrayList<Point> spawnPoints;
    private ArrayList<Island> islands;
    private Clip musicTrack;

    // Load the map based on the given index
    public static Map loadMap(int MAP_INDEX) {
        Map desiredMap = new Map();

        switch (MAP_INDEX) {
            case 0: // DEEPSEA
                desiredMap.setBackgroundResource("deepsea.gif");
//                desiredMap.set
                break;
            case 1: // CAVE
                desiredMap.setBackgroundResource("cave.gif");
                break;
            case 2: // INDUSTRY
                desiredMap.setBackgroundResource("industry.gif");
                break;
            default:
                throw new IllegalArgumentException("Unknown map index");
        }

        // Set other properties (groundY, spawn points, etc.)
        // Assuming there is some logic here to configure each map type's unique properties

        return desiredMap;
    }

    // Set the background image for the map (GIF or PNG)
    public void setBackgroundResource(String filename) {
        backgroundResource = new ImageIcon("/images/" + filename).getImage();
    }

    // Set the ground Y level (for islands above sea level, etc.)
    public void setGroundY(int groundY) {
        this.groundY = groundY;
    }

    public void setSpawnPoints(ArrayList<Point> points) {
        this.spawnPoints = points;
    }

    public void addIsland(Island island) {
        if (this.islands == null) {
            this.islands = new ArrayList<>();
        }
        this.islands.add(island);
    }

    public void setMusicTrack(String filename) {
        try {
            // Load the music file (using a Clip for simplicity)
            File musicFile = new File("path/to/music/" + filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            this.musicTrack = AudioSystem.getClip();
            this.musicTrack.open(audioStream);
            this.musicTrack.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
        }
    }

    public Image getBackgroundResource() {
        return this.backgroundResource;
    }

    public int getGroundY() {
        return this.groundY;
    }

    public ArrayList<Point> getSpawnPoints() {
        return this.spawnPoints;
    }

    public ArrayList<Island> getIslands() {
        return this.islands;
    }

    public void playMusic() {
        if (this.musicTrack != null) {
            this.musicTrack.start();
        }
    }

    public void stopMusic() {
        if (this.musicTrack != null && this.musicTrack.isRunning()) {
            this.musicTrack.stop();
        }
    }

}
