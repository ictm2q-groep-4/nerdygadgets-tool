package nl.nerdygadgets.algorithms;

import nl.nerdygadgets.infrastructure.components.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the backtracking
 *
 * @author Joris Vos
 */
public class Backtracking {
    /**
     * This contains all the possible web components that can be used
     */
    private Component[] webComponents;

    /**
     * This contains all the possible database components that can be used
     */
    private Component[] databaseComponents;

    /**
     * This contains the most optimal configuration for web servers with at least 99.996 availability and the cheapest
     */
    private Component[] optimalWebConfiguration;

    /**
     * This contains the most optimal configuration for database servers with at least 99.996 availability and the cheapest
     */
    private Component[] optimalDatabaseConfiguration;

    /**
     * This contains all the components that are in the infrastructure configuration at all time.
     */
    private Component[] otherComponents;

    /**
     * This variable keeps track of the amount of configurations that are tested.
     */
    private int configurationsTested = 0;

    /**
     * If there goes something wrong or you want to stop the backtracking this boolean is changed to true.
     */
    private boolean forceStop = false;

    /**
     * This is the constructor for the backtracking class.
     * It initializes the necessary variables that contains all the web servers, database servers and other components.
     */
    public Backtracking() {
        webComponents = new Component[3];
        databaseComponents = new Component[3];
        otherComponents = new Component[2];

        webComponents[0] = new HAL9001W("",1,1);
        webComponents[1] = new HAL9002W("",1,1);
        webComponents[2] = new HAL9003W("",1,1);

        databaseComponents[0] = new HAL9001DB("",1,1);
        databaseComponents[1] = new HAL9002DB("",1,1);
        databaseComponents[2] = new HAL9003DB("",1,1);

        otherComponents[0] = new pfSense("",1,1);
        otherComponents[1] = new DBLoadBalancer("",1,1);
    }

    /**
     * This is the constructor for the backtracking class.
     * It initializes the necessary variables that contains all the web servers, database servers and other components.
     * It overrides the webComponents and databaseComponents variables
     *
     * @param availableWebComponents        Component[]
     * @param availableDatabaseComponents   Component[]
     */
    public Backtracking(Component[] availableWebComponents, Component[] availableDatabaseComponents) {
        this();

        if (availableWebComponents != null) {
            setAvailableWebComponents(webComponents);
        }
        if (availableDatabaseComponents != null) {
            setAvailableDatabaseComponents(databaseComponents);
        }
    }

    /**
     * This is the constructor for the backtracking class.
     * It initializes the necessary variables that contains all the web servers, database servers and other components.
     * It overrides the webComponents, databaseComponents, optimalWebConfiguration and optimalDatabaseConfiguration variables.
     *
     * @param availableWebComponents        Component[]
     * @param availableDatabaseComponents   Component[]
     * @param usedWebComponents             Component[]
     * @param usedDatabaseComponents        Component[]
     */
    public Backtracking(Component[] availableWebComponents, Component[] availableDatabaseComponents,
                        Component[] usedWebComponents, Component[] usedDatabaseComponents) {
        this(availableWebComponents, availableDatabaseComponents);

        if (usedWebComponents != null) {
            setUsedWebComponents(usedWebComponents);
        }
        if (usedDatabaseComponents != null) {
            setUsedDatabaseComponents(usedDatabaseComponents);
        }
    }

    /**
     * This sets the optimalWebComponents equal to usedWebComponents and calculates only the database components if optimalDatabaseComponents equals null
     *
     * @param usedWebComponents Component[]
     */
    public void setUsedWebComponents(Component[] usedWebComponents) {
        this.optimalWebConfiguration = usedWebComponents;
    }

    /**
     * This sets the optimalDatabaseComponents equal to usedDatabaseComponents and calculates only the web components if optimalWebComponents equals null
     *
     * @param usedDatabaseComponents    Component[]
     */
    public void setUsedDatabaseComponents(Component[] usedDatabaseComponents) {
        this.optimalDatabaseConfiguration = usedDatabaseComponents;
    }

    /**
     * Set the available web components that can be used by the backtracking algorithm.
     *
     * @param availableWebComponents    Component[]
     */
    public void setAvailableWebComponents(Component[] availableWebComponents) {
        this.webComponents = availableWebComponents;
    }

    /**
     * Set the available database components that can be used by the backtracking algorithm.
     *
     * @param availableDatabaseComponents   Component[]
     */
    public void setAvailableDatabaseComponents(Component[] availableDatabaseComponents) {
        this.databaseComponents = availableDatabaseComponents;
    }

    /**
     * This starts the backtracking algorithm. It returns true if it succeeded and false if there went something wrong or it stopped.
     *
     * @return boolean
     */
    public boolean start() {
        if (optimalDatabaseConfiguration==null) {
            solve(new ArrayList<>(), databaseComponents);
        }
        if (optimalWebConfiguration==null) {
            solve(new ArrayList<>(), webComponents);
        }

        if (forceStop) {
            return false;
        } else {
            return ((optimalDatabaseConfiguration != null) && (optimalWebConfiguration != null));
        }
    }

    /**
     * This stops the backtracking algorithm. It returns false if the backtracking algorithm already stopped, else it returns true.
     *
     * @return boolean
     */
    public boolean stop() {
        if (!forceStop) {
            forceStop = true;
            return true;
        }
        return false;
    }

    /**
     * This is a recursive method that is used as backtracking algorithm.
     *
     * @param currentComponentList  List<Component>     This is the configuration as it is at that moment
     * @param componentList         Component[]         This is a array that contains the components where the backtracking algorithm can choose from
     */
    private void solve(List<Component> currentComponentList, Component[] componentList) {
        configurationsTested++;

        if (currentComponentList == null || componentList == null) {
            return;
        }
        if (validConfiguration(currentComponentList.toArray(Component[]::new))) {
            return;
        }

        for (Component component : componentList) {
            if (forceStop) {
                return;
            }

            List<Component> _list = new ArrayList<>(currentComponentList);
            _list.add(component);

            solve(_list, componentList);
        }
    }

    /**
     * It checks if the uptime of components is 99.996 or higher, if that is true it return true, else it return false.
     * But before it returns is checks if the components price is equal or less than optimal...Configuration.
     * If so, set the optimal...Configuration equal to components
     *
     * @param components    Component[]
     * @return              boolean
     */
    private boolean validConfiguration(Component[] components) {
        if (components.length<=0) {
            return false;
        } else {
            if (getAvailability(components) >= 99.996) {
                if (components[0].componentType.equals(ComponentType.DATABASESERVER)) {
                    if (getPrice(components) == getPrice(optimalDatabaseConfiguration)) {
                        if (components.length < optimalDatabaseConfiguration.length) {
                            optimalDatabaseConfiguration=components;
                        }
                    } else if (getPrice(components) < getPrice(optimalDatabaseConfiguration)) {
                        optimalDatabaseConfiguration=components;
                    }
                } else if (components[0].componentType.equals(ComponentType.WEBSERVER)) {
                    if (getPrice(components) == getPrice(optimalWebConfiguration)) {
                        if (components.length < optimalWebConfiguration.length) {
                            optimalWebConfiguration = components;
                        }
                    } else if (getPrice(components) < getPrice(optimalWebConfiguration)) {
                        optimalWebConfiguration = components;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * This returns the availability of components, this should always be an array that contains one ComponentType
     *
     * @param components    Component[]
     * @return              double
     */
    private double getAvailability(Component[] components) {
        if (components==null) {
            return 0;
        }

        double uptime=1;

        for (Component component : components) {
            uptime = uptime * (1 - component.availability*0.01);
        }

        return (1-uptime)*100;
    }

    /**
     * This returns the price of components, this should always be an array that contains one ComponentType
     *
     * @param components    Component[] components
     * @return              int
     */
    private int getPrice(Component[] components) {
        if (components==null) {
            return 999999999;
        }

        int price=0;

        for (Component component : components) {
            price += component.price;
        }

        return price;
    }

    /**
     * This prints the optimal configuration with the availability, price, components and configurations tested.
     * If forceStop equals true or the optimal...Configurations equal to null it returns without printing anything.
     */
    public void printSolution() {
        if (forceStop || optimalWebConfiguration==null || optimalDatabaseConfiguration==null) {
            return;
        }

        StringBuilder databaseServers = new StringBuilder();

        System.out.print("Database servers("+optimalDatabaseConfiguration.length+"): [");
        for (Component component : optimalDatabaseConfiguration) {
            databaseServers.append(component.getClass().getSimpleName());
            databaseServers.append(", ");
        }
        System.out.println(databaseServers.substring(0, databaseServers.length()-2)+"]");

        StringBuilder webServers = new StringBuilder();

        System.out.print("Web servers("+optimalWebConfiguration.length+"): [");
        for (Component component : optimalWebConfiguration) {
            webServers.append(component.getClass().getSimpleName());
            webServers.append(", ");
        }
        System.out.println(webServers.substring(0, webServers.length()-2)+"]");

        StringBuilder otherComponents = new StringBuilder();

        System.out.print("Other components("+this.otherComponents.length+"): [");
        for (Component component : this.otherComponents) {
            otherComponents.append(component.getClass().getSimpleName());
            otherComponents.append(", ");
        }
        System.out.println(otherComponents.substring(0, otherComponents.length()-2)+"]");

        System.out.println();
        System.out.println("Availability: "+getTotalAvailability()+"%");
        System.out.println("Price: €"+getTotalPrice()+",-");
        System.out.println("Configurations tested: "+configurationsTested);
    }

    /**
     * This returns the amount of configurations that have been tested by the backtracking algorithm.
     *
     * @return  int
     */
    public int getConfigurationsTested() {
        return configurationsTested;
    }

    /**
     * This returns the availability calculate over all the components in the infrastructure design
     *
     * @return  double
     */
    public double getTotalAvailability() {
        if (optimalDatabaseConfiguration==null || optimalWebConfiguration==null || forceStop) {
            return -1;
        }
        double availability=1;

        for (Component component : otherComponents) {
            availability *= (component.availability*0.01);
        }

        return (availability*(getAvailability(optimalWebConfiguration)*0.01)
                *(getAvailability(optimalDatabaseConfiguration)*0.01))*100;
    }

    /**
     * This returns the price calculated over all the components in the infrastructure design.
     *
     * @return  int
     */
    public int getTotalPrice() {
        if (optimalWebConfiguration==null || optimalDatabaseConfiguration==null || forceStop) {
            return -1;
        }
        int price=0;

        for (Component component : otherComponents) {
            price += component.price;
        }

        return price
                +getPrice(optimalWebConfiguration)
                +getPrice(optimalDatabaseConfiguration);
    }

/*
     * This is just for testing purposes
     *
     * @param args  String[]

    public static void main(String[] args) {
        Backtracking backtracking = new Backtracking();

        Component[] db = new Component[2];
        Component[] web = new Component[2];

        db[0] = new HAL9001DB("",1,1);
        db[1] = new HAL9003DB("", 1, 1);
        web[0] = new HAL9001W("",1,1);
        web[1] = new HAL9003W("",1,1);

        backtracking.setAvailableDatabaseComponents(db);
        backtracking.setAvailableWebComponents(web);

        backtracking.start();
        backtracking.printSolution();
    }
*/
}
