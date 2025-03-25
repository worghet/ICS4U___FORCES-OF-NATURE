package game;

// == ISLAND ========
public class Island {


    // == CONSTANTS =============================================


    public static final int X = 0;
    public static final int Y = 1;


    // == INSTANCE VARIABLES [FIELDS] ==========================


    // Midlines.
    private int midlineY;
    private int midlineX;

    // Top left point.
    private int topLeftX;
    private int topLeftY;

    // Bottom right point.
    private int bottomRightX;
    private int bottomRightY;

    // Island width (horizontal length in pixels).
    private int width;

    
    // == CONSTRUCTOR ==========================================


    public Island(int topLeftX, int topLeftY, int width) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.bottomRightX = topLeftX + width;
        this.bottomRightY = bottomRightY + 30;
        this.midlineY = (topLeftY + bottomRightY) / 2;
    }


    // == GETTER METHODS =======================================


    public int getMidlineY() {
        return midlineY;
    }

    public int getMidlineX() {
        return midlineX;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }

    public int getWidth() {
        return width;
    }

    
}
