package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9001DB extends Component {
    /**
     * This is the availability for the server, so how long the server is available
     */
    public final double availability = 90 ;
    /**
     * This is the price for the server
     */
    public final  int price = 5100;
    /**
     * This is the type of server
     */
    public final ComponentType componentType = ComponentType.DATABASESERVER;
    /**
     * This constructor calls upon the super constructor of the Component class and sets the x and y
     */

    public HAL9001DB (String hostname, int x, int y) {
        super(hostname, x ,y);
    }
}
