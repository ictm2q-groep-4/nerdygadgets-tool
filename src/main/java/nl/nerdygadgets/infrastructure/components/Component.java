package nl.nerdygadgets.infrastructure.components;

import com.jcraft.jsch.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class that is extended by all components
 *
 * @author Lucas Ouwens
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 * @author Lou Elzer
 */
public class Component implements Statistic {

    /**
     *
     */
    public final String name;

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
    public int x=0;

    /**
     * This is the y coordinate
     */
    public int y=0;

    /**
     * This is the hostname
     */
    public String hostname;

    /**
     * This is the SSH username
     */
    public String username="username";

    /**
     * This is the password used for the SSH connection
     */
    public String password="password";

    /**
     * This is the ipv4 address of the server
     */
    public InetAddress ipv4 = Inet4Address.getLoopbackAddress();

    /**
     * This is the ipv6 address of the server
     */
    public InetAddress ipv6 = Inet6Address.getLoopbackAddress();

    public Component(String name, double availability, int price, ComponentType componentType) {
        this.name = name;
        this.availability = availability;
        this.price = price;
        this.componentType = componentType;
    }

    public Component(Component copyFromComponent) {
        this.name = copyFromComponent.name;
        this.availability = copyFromComponent.availability;
        this.price = copyFromComponent.price;
        this.componentType = copyFromComponent.componentType;
    }

    public Component(Component copyFromComponent, String hostname, int x, int y) {
        this(copyFromComponent);
        this.hostname = hostname;
        this.x = x;
        this.y = y;
    }

    public Component(String name, double availability, int price, ComponentType componentType,
                     String hostname, int x, int y, String username, String password,
                     InetAddress ipv4, InetAddress ipv6) {
        this(name, availability, price, componentType);
        this.hostname = hostname;
        this.x = x;
        this.y = y;
        this.username = username;
        this.password = password;
        this.ipv4 = ipv4;
        this.ipv6 = ipv6;
    }

    /**
     * This method checks via SSH if a component is up and running
     *
     * @return Returns true when online, false when offline
     */
    public boolean isOnline() {
        boolean isUp = false;

        try {
            new Socket(ipv4.toString().split("/")[1], 22).close();
            return true;
        } catch (IOException e) {
            System.err.println("Something went wrong while connecting to the SSH server");
            //e.printStackTrace();
            return false;
        }

        /*
        // get ssh channel
        Channel channel = getSSHChannel(username, password);

        try {
            if(channel != null) {
                // open channel
                channel.connect();

                isUp = channel.isConnected();

                // close channel
                channel.disconnect();

                return isUp;
            }
        } catch (Exception e) {
            System.err.println("Something went wrong while connecting to the SSH server");
            e.printStackTrace();
        }
        return false;
         */
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
            Channel channel = getSSHChannel(username, password);

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
     * @param password      The password
     * @return              Returns a channel which can be used to initiate a connection
     */
    private Channel getSSHChannel(String user, String password) {
        try {
            // initiate Java Secure Channel object
            JSch jsch = new JSch();

            // initiate session
            Session session = jsch.getSession(user, ipv4.toString().substring(1), 22);

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

    public String getHostname() { return hostname; }
    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setUser(String username) { this.username = username; }
    public void setPass(String password) { this.password = password; }
    public void setIpv4(String ipv4) {
        try {
            this.ipv4 = InetAddress.getByName(ipv4);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public void setIpv6(String ipv6) {
        try {
            this.ipv6 = Inet6Address.getByName(ipv6);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
