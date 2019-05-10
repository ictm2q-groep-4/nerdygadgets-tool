package nl.nerdygadgets.algorithms;

import nl.nerdygadgets.infrastructure.components.Component;

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

        //TODO fill the lists webComponents and databaseComponents
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

//    private boolean solve() {
//
//    }
}
