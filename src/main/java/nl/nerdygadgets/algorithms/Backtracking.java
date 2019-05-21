package nl.nerdygadgets.algorithms;

import javafx.scene.control.Alert;
import nl.nerdygadgets.infrastructure.components.*;
import nl.nerdygadgets.infrastructure.components.ComponentManager;
import nl.nerdygadgets.main.NerdyGadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles the backtracking
 *
 * @author Lucas Ouwens 
 * @author Joris Vos
 */
public class Backtracking {
    /**
     * This contains all the possible web components that can be used
     */
    private List<Component> webComponents;

    /**
     * This contains all the possible database components that can be used
     */
    private List<Component> databaseComponents;

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
    private List<Component> otherComponents;

    /**
     * This variable keeps track of the amount of configurations that are tested.
     */
    private int configurationsTested = 0;

    /**
     * This is used to calculate the availabilityPerComponentType
     */
    private double availability = 99.99;

    /**
     * This is used as a condition for the backtracking algorithm
     */
    private double availabilityPerComponentType = 99.996;

    /**
     * If there goes something wrong or you want to stop the backtracking this boolean is changed to true.
     */
    private boolean forceStop = false;

    /**
     * This is the constructor for the backtracking class.
     * It initializes the necessary variables that contains all the web servers, database servers and other components.
     */
    public Backtracking() {
        webComponents = new ArrayList<>();
        databaseComponents = new ArrayList<>();
        otherComponents = new ArrayList<>();

        for (Component component : ComponentManager.getAllComponents()) {
            if (component.componentType.equals(ComponentType.DATABASESERVER)) {
                databaseComponents.add(component);
            } else if (component.componentType.equals(ComponentType.WEBSERVER)) {
                webComponents.add(component);
            } else {
                otherComponents.add(component);
            }
        }
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
            setAvailableWebComponents(availableWebComponents);
        }
        if (availableDatabaseComponents != null) {
            setAvailableDatabaseComponents(availableDatabaseComponents);
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
     * Calculate the availabilityPerComponentType from availability.
     * This is done by dividing the overall minimum availability by the otherComponents component availability and then taking the square root of that.
     */
    private void calculateAvailabilityPerComponentType() {
        double availabilityPCT = availability*0.01;

        for (Component component : otherComponents) {
            availabilityPCT = availabilityPCT / (component.availability*0.01);
        }

        if ((optimalDatabaseConfiguration != null) && (optimalWebConfiguration != null)) {
            availabilityPCT = availabilityPCT / (getAvailability(optimalDatabaseConfiguration)*0.01);
            availabilityPCT = availabilityPCT / (getAvailability(optimalWebConfiguration)*0.01);
        } else if (optimalDatabaseConfiguration != null) {
            availabilityPCT = availabilityPCT / (getAvailability(optimalDatabaseConfiguration)*0.01);
        } else if (optimalWebConfiguration != null) {
            availabilityPCT = availabilityPCT / (getAvailability(optimalWebConfiguration)*0.01);
        } else {
            availabilityPCT = Math.sqrt(availabilityPCT) * 100;
        }

        availabilityPerComponentType = availabilityPCT;
    }

    /**
     * This starts the backtracking algorithm. It returns true if it succeeded and false if there went something wrong or it stopped.
     *
     * @return boolean
     */
    public boolean start() {
        if (optimalDatabaseConfiguration==null && !forceStop) {
            solve(new ArrayList<>(), databaseComponents.toArray(Component[]::new));
        }
        if (optimalWebConfiguration==null && !forceStop) {
            solve(new ArrayList<>(), webComponents.toArray(Component[]::new));
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
        try {
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
        } catch (StackOverflowError e) {
            stop();
            NerdyGadgets.showAlert("Backtracking","Er is een 'StackOverflowError' opgetreden.\nDit is mogelijk omdat er te weinig resources beschikbaar zijn.\nDit in combinatie met een te hoog ingevoerde beschikbaarheid.", Alert.AlertType.ERROR);
        } catch (OutOfMemoryError e) {
            stop();
            NerdyGadgets.showAlert("Backtracking", "Er is een 'OutOfMemoryError' opgetreden.\nDit is mogelijk omdat er te weinig resources beschikbaar zijn.\nDit in combinatie met een te hoog ingevoerde beschikbaarheid.", Alert.AlertType.ERROR);
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
            if (getAvailability(components) >= availabilityPerComponentType) {
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
     * This prints the optimal configuration with the availability, price, components and configurations tested.
     * If forceStop equals true or the optimal...Configurations equal to null it returns without printing anything.
     */
    public void printSolution() {
        if (forceStop || optimalWebConfiguration==null || optimalDatabaseConfiguration==null) {
            return;
        }

        System.out.println(getSolution());
    }

    /**
     * This returns the availability of components, this should always be an array that contains one ComponentType
     *
     * @param components    Component[]
     * @return              double
     */
    public static double getAvailability(Component[] components) {
        if (components==null) {
            return 0;
        }

        double availability=1;

        for (Component component : components) {
            availability *= (1 - component.availability*0.01);
        }

        return (1-availability)*100;
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

    /**
     * Returns a list with all the components combined (optimal solution)
     *
     * @return List<Component>
     */
    public List<Component> getAllComponents() {
        List<Component> allComponents = new ArrayList<>();

        allComponents.addAll(Arrays.asList(optimalDatabaseConfiguration));
        allComponents.addAll(Arrays.asList(optimalWebConfiguration));
        allComponents.addAll(otherComponents);

        return allComponents;
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
     * Get the overall availability requirement
     *
     * @return  double
     */
    public double getAvailability() {
        return availability;
    }

    /**
     * Get the availability that the Database server and Web server component types should get indibidually.
     *
     * @return  double
     */
    public double getAvailabilityPerComponentType() {
        return availabilityPerComponentType;
    }

    /**
     * Get the forceStop boolean.
     *
     * @return  boolean
     */
    public boolean isForceStop() {
        return forceStop;
    }

    /**
     * Get the optimal database server configuration with the set availability
     *
     * @return  Component[]
     */
    public Component[] getOptimalDatabaseConfiguration() {
        return optimalDatabaseConfiguration;
    }

    /**
     * Get the database server components that are available to the backtracking algorithm to get to the set minimum availability
     *
     * @return  Component[]
     */
    public Component[] getAvailableDatabaseComponents() {
        return databaseComponents.toArray(Component[]::new);
    }

    /**
     * Get the optimal web server configuration with the set availability
     *
     * @return  Component[]
     */
    public Component[] getOptimalWebConfiguration() {
        return optimalWebConfiguration;
    }

    /**
     * Get the web server components that are available to the backtracking algorithm to get to the set minimum availability
     *
     * @return  Component[]
     */
    public Component[] getAvailableWebComponents() {
        return webComponents.toArray(Component[]::new);
    }

    /**
     * Get the list of components that are standard in the infrastructure configuration
     *
     * @return  Component[]
     */
    public Component[] getOtherComponents() {
        return otherComponents.toArray(Component[]::new);
    }

    /**
     * returns the optimal solution with everything that is relevant to this
     *
     * @return  String
     */
    public String getSolution() {
        if (forceStop || optimalWebConfiguration==null || optimalDatabaseConfiguration==null) {
            return null;
        }

        StringBuilder solution = new StringBuilder();

        StringBuilder databaseServers = new StringBuilder();
        for (Component component : optimalDatabaseConfiguration) {
            databaseServers.append(component.name);
            databaseServers.append(", ");
        }
        if (databaseServers.length()>0) {
            solution.append("Database servers(");
            solution.append(optimalDatabaseConfiguration.length);
            solution.append("): [");

            solution.append(databaseServers.substring(0, databaseServers.length() - 2));
            solution.append("]\n");
        } else {
            solution.append("Database servers(0): []\n");
        }

        StringBuilder webServers = new StringBuilder();
        for (Component component : optimalWebConfiguration) {
            webServers.append(component.name);
            webServers.append(", ");
        }
        if (webServers.length()>0) {
            solution.append("Web servers(");
            solution.append(optimalWebConfiguration.length);
            solution.append("): [");

            solution.append(webServers.substring(0, webServers.length() - 2));
            solution.append("]\n");
        } else {
            solution.append("Web servers(0): []\n");
        }

        StringBuilder otherComponents = new StringBuilder();
        for (Component component : this.otherComponents) {
            otherComponents.append(component.name);
            otherComponents.append(", ");
        }
        if (otherComponents.length()>0) {
            solution.append("Andere components(");
            solution.append(this.otherComponents.size());
            solution.append("): [");

            solution.append(otherComponents.substring(0, otherComponents.length() - 2));
            solution.append("]\n");
        } else {
            solution.append("Andere components(0): []\n");
        }

        solution.append("\n");

        solution.append("Beschikbaarheid: ");
        solution.append(getTotalAvailability());
        solution.append("%\n");

        solution.append("Kosten: €");
        solution.append(getTotalPrice());
        solution.append(",-\n");

        solution.append("Configuraties getest: ");
        solution.append(configurationsTested);

        return solution.toString();
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
     * This sets the otherComponents equal to the otherComponents that is a argument of the method
     *
     * @param otherComponents   Component[]
     */
    public void setUsedOtherComponents(Component[] otherComponents) {
        this.otherComponents = Arrays.asList(otherComponents);
    }

    /**
     * Set the available web components that can be used by the backtracking algorithm.
     *
     * @param availableWebComponents    Component[]
     */
    public void setAvailableWebComponents(Component[] availableWebComponents) {
        this.webComponents = Arrays.asList(availableWebComponents);
    }

    /**
     * Set the available database components that can be used by the backtracking algorithm.
     *
     * @param availableDatabaseComponents   Component[]
     */
    public void setAvailableDatabaseComponents(Component[] availableDatabaseComponents) {
        this.databaseComponents = Arrays.asList(availableDatabaseComponents);
    }

    /**
     * Set the availability variable that is used by the backtracking algorithm
     *
     * @param availability  double
     */
    public void setMinimumAvailability(double availability) {
        this.availability = availability;

        calculateAvailabilityPerComponentType();
    }
}
