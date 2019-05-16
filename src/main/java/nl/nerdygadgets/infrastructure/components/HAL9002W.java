package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9002W extends Component {
    public HAL9002W (String hostname, int x, int y) {
        super(hostname, 90, 3200, ComponentType.WEBSERVER, x ,y);
    }

    public HAL9002W (String hostname, int x, int y, String username, String password, String ipv4, String ipv6) {
        super(hostname, 90, 3200, ComponentType.WEBSERVER, x, y, username, password, ipv4, ipv6);
    }
}
