package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public abstract class Component implements Statistic {
    /**
     * This is the x coordinate
     */
    private int x;

    /**
     * This is the y coordinate
     */
    private int y;

    /**
     * This is the default constructor, it sets the x and y
     *
     * @param x int
     * @param y int
     */
    public Component(int x, int y) {
        this.x = x;
        this.y =y;
    }

    // region Getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    // endregion

    // region Setters
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    // endregion
}
