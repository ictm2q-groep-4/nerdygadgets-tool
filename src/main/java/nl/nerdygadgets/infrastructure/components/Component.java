package nl.nerdygadgets.infrastructure.components;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * This method checks via SSH if a component is up and running
     *
     * @return Returns true when online, false when offline
     */
    public boolean isOnline() {
        boolean isUp = false;

        // get ssh channel
        Channel channel = getSSHChannel("test", "louelzer.com", "kaas");

        try {
            // open channel
            channel.connect();

            isUp = channel.isConnected();

            // close channel
            channel.disconnect();

            return isUp;
        } catch (Exception e) {
            System.err.println("Something went wrong while connecting to the SSH server");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method gets a components disk usage via SSH
     *
     * @return Returns a string containing the disk usage
     */
    public String getDiskUsage() {
        try {
            // get ssh channel
            Channel channel = getSSHChannel("test", "louelzer.com", "kaas");

            // set streams
            OutputStream ops = channel.getOutputStream();
            PrintStream ps = new PrintStream(ops);

            // open channel
            channel.connect();

            // run command
            ps.println("du -h");
            ps.println("exit");

            // close printstream
            ps.flush();
            ps.close();

            // initiate inputstream and bufferedreader
            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            System.out.println("Opening...");

            // read the output
            String line;
            List<String> output = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            // close reader and channel
            reader.close();
            channel.disconnect();

            // concatenate to one string
            final StringBuilder builder = new StringBuilder();
            output.forEach((val) -> {
                builder.append(val + "\n");
            });

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method gets the processor usage from a component via SSH
     *
     * @return Returns the current processor usage in a String
     */
    public String getProcessorUsage() {
        return null;
    }

    /**
     * This method opens a SSH channel with a remote host
     *
     * @param user          The username for the connection
     * @param host          The hostname
     * @param password      The password
     * @return              Returns a channel which can be used to initiate a connection
     */
    public Channel getSSHChannel(String user, String host, String password) {
        try {
            // initiate Java Secure Channel object
            JSch jsch = new JSch();

            // initiate session
            Session session = jsch.getSession(user, host, 22);

            // disable host key verification
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            // set password for session
            session.setPassword(password);

            // connect session
            session.connect();

            // open channel
            Channel channel = session.openChannel("shell");

            return channel;
        } catch (Exception e) {
            System.err.println("Something went wrong while opening the SSH channel");
            return null;
        }
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
}
