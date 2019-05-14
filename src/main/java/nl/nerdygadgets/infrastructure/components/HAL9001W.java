package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9001W extends Component {
    public HAL9001W (String hostname, int x, int y) {
        super(hostname, 80, 2200, ComponentType.WEBSERVER, x ,y);
    }
}
