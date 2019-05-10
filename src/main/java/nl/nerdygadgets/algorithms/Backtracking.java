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
    private List<Component> webComponents;
    private List<Component> databaseComponents;

    private boolean forceStop = false;

    public Backtracking() {
        webComponents = new ArrayList<>();
        databaseComponents = new ArrayList<>();

        webComponents.add(new HAL9001W(1,1));
        webComponents.add(new HAL9002W(1,1));
        webComponents.add(new HAL9003W(1,1));

        databaseComponents.add(new HAL9001DB(1,1));
        databaseComponents.add(new HAL9002DB(1,1));
        databaseComponents.add(new HAL9003DB(1,1));
    }

    public void start() {

    }

    public void stop() {
        forceStop = true;
    }

    private void backtrackThread() {
        if (solve()) {
            if (forceStop) {

            } else {

            }
        } else {

        }
    }

    private boolean solve() {

    }

    public double calculateAvailablity(Component[] components) {
        double uptime=1;

        for (Component component : components) {
            uptime = uptime * (1 - component.getUptime()*0.01);
        }

        return (1-uptime)*100;
    }

    public int calculatePrice(Component[] components) {
        int price=0;

        for (Component component : components) {
            price += component.getPrice();
        }

        return price;
    }
}
