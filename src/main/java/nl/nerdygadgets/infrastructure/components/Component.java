package nl.nerdygadgets.infrastructure.components;

/**
 * The abstract class that is extended by all components
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public abstract class Component implements Statistic {
    /**
     * This is the hostname
     */
    public final String hostname;

    /**
     * This is the availability for the server, so how long the server is available
     */
    public final double availability;

    /**
     * This is the price for the server
     */
    public final int price;

    /**
     * This is the type of server
     */
    public final ComponentType componentType;

    /**
     * This is the x coordinate
     */
    private int x;

    /**
     * This is the y coordinate
     */
    private int y;

    /**
     * This is a constructor for components. It sets all the final variables in this class.
     *
     * @param hostname      String
     * @param availability  double
     * @param price         int
     * @param componentType ComponentType
     */
    public Component(String hostname, double availability, int price, ComponentType componentType) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
    }

    /**
     * This is a constructor for components. It sets all the variables in this class.
     *
     * @param hostname      String
     * @param availability  double
     * @param price         int
     * @param componentType ComponentType
     * @param x             int
     * @param y             int
     */
    public Component(String hostname, double availability, int price, ComponentType componentType, int x, int y) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
        this.x = x;
        this.y = y;
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
