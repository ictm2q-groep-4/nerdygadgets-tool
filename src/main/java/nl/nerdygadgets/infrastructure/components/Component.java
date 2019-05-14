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
     * This is the SSH username
     */
    private String user;

    /**
     * This is the SSH host url/ip
     */
    private String host;

    /**
     * This is the password used for the SSH connection
     */
    private String pass;

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
     * This is a constructor for components which includes SSH credentials
     *
     * @param hostname      String
     * @param availability  double
     * @param price         int
     * @param componentType ComponentType
     * @param x             int
     * @param y             int
     * @param user          String
     * @param host          String
     * @param pass          String
     */
    public Component(String hostname, double availability, int price, ComponentType componentType, int x, int y, String user, String host, String pass) {
        this.hostname = hostname;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
        this.x = x;
        this.y = y;
        this.user = user;
        this.host = host;
        this.pass = pass;
    }

    /**
     * This method checks via SSH if a component is up and running
     *
     * @return Returns true when online, false when offline
     */
    public boolean isOnline() {
        boolean isUp = false;

        // get ssh channel
        Channel channel = getSSHChannel(user, host, pass);

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
        // Initiate output list
        List<String> output;

        // Run the command on the remote machine
        if ((output = runCommand("fsutil volume diskfree c:")) != null) {
            return output.get(1).split("\\s+")[6];
        } else {
            return null;
        }
    }

    /**
     * This method gets the processor usage from a component via SSH
     *
     * @return Returns the current processor usage in a String
     */
    public String getProcessorUsage() {
        // Initiate output list
        List<String> output;

        // Run the command on the remote machine
        if ((output = runCommand("wmic cpu get loadpercentage")) != null) {
            return output.get(1) + "%";
        } else {
            return null;
        }
    }

    /**
     * This method runs a command on the remote server and captures the output in an ArrayList
     *
     * @param command       The command to run
     * @return              Returns the entire output per line in a List
     */
    private List<String> runCommand(String command) {
        try {
            // get ssh channel
            Channel channel = getSSHChannel(user, host, pass);

            // set streams
            OutputStream ops = channel.getOutputStream();
            PrintStream ps = new PrintStream(ops);

            // open channel
            channel.connect();

            // run command
            ps.println(command);

            // close printstream
            ps.flush();
            ps.close();

            // initiate inputstream and bufferedreader
            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // read the output
            String line;
            List<String> output = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            // close reader and channel
            reader.close();
            channel.disconnect();

            return output;

        } catch (Exception e) {
            e.printStackTrace();
        }
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
