package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9001DB extends Component {
    public HAL9001DB (String hostname, int x, int y) {
        super(hostname, 90, 5100, ComponentType.DATABASESERVER, x ,y);
    }
}
