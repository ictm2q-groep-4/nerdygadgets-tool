package nl.nerdygadgets.infrastructure.components;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The abstract class that is extended by all components
 *
 * @author Lucas Ouwens
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public abstract class Component implements Statistic {

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
     * This is the hostname
     */
    private String hostname;

    /**
     * This is the ipv4 address of the server
     */
    private InetAddress ipv4;

    /**
     * This is the ipv6 address of the server
     */
    private InetAddress ipv6;

    /**
     * This is the port number of the server
     */
    private int portnumber;

    /**
     * This is a constructor for components. It sets all the final variables in this class.
     *
     * @param hostname      String
     * @param availability  double
     * @param price         int
     * @param componentType ComponentType
     *
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

    /**
     * This is a constructor for components. It sets all the variables in this class.
     *
     * @param hostname      String
     * @param availability  double
     * @param price         int
     * @param componentType ComponentType
     * @param x             int
     * @param y             int
     * @param ipv4          String
     * @param ipv6          String
     * @param portnumber    int
     */
    public Component(String hostname, double availability, int price, ComponentType componentType, int x, int y,String ipv4, String ipv6, int portnumber) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
        this.x = x;
        this.y = y;
        try {
            this.ipv4 = Inet4Address.getByName(ipv4);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            this.ipv6 = Inet6Address.getByName(ipv6);
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        this.portnumber = portnumber;
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
     * @param ipv4          String
     * @param portnumber    int
     */
    public Component(String hostname, double availability, int price, ComponentType componentType, int x, int y,String ipv4, int portnumber) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
        this.x = x;
        this.y = y;
        try {
            this.ipv4 = Inet4Address.getByName(ipv4);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.portnumber = portnumber;
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
     * @param ipv6          String
     * @param portnumber    int
     */
    public Component(String hostname, double availability, int price, ComponentType componentType, int x, int y,int portnumber,String ipv6) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
        this.x = x;
        this.y = y;
        try {
            this.ipv6 = Inet6Address.getByName(ipv6);
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        this.portnumber = portnumber;
    }

    // region Getters
    public String getHostname() {
        return hostname;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    // endregion

    // region Setters
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // endregion

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
