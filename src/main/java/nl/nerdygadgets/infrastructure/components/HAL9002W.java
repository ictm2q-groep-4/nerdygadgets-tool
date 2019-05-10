package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9002W extends Component {
    public HAL9002W (String hostname, int x, int y) {
        super(hostname, 90, 3200, ComponentType.WEBSERVER, x ,y);
    }
}
