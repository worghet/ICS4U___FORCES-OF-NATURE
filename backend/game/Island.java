package game;

public class Island {

    public static final int X = 0;
    public static final int Y = 1;

    private int midlineY;
    private int midlineX;

    private int topLeftX; // x, y
    private int topLeftY;

    private int bottomRightX; // x, y
    private int bottomRightY;

    private int width;

    public Island(int topLeftX, int topLeftY, int width) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.bottomRightX = topLeftX + width;
        this.bottomRightY = bottomRightY + 30;
        this.midlineY = (topLeftY + bottomRightY) / 2;
    }

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
