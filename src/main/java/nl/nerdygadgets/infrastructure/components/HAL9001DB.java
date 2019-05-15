package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9001DB extends Component {
    public HAL9001DB (String hostname, int x, int y) {
        super(hostname, 90, 5100, ComponentType.DATABASESERVER, x ,y);
    }

    public HAL9001DB (String hostname, int x, int y, String username, String password, String ipv4, String ipv6) {
        super(hostname, 90, 5100, ComponentType.DATABASESERVER, x, y, username, password, ipv4, ipv6);
    }
}
