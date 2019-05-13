package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9002DB extends Component {
    public HAL9002DB (String hostname, int x, int y) {
        super(hostname, 95, 7700, ComponentType.DATABASESERVER, x ,y);
    }
}
