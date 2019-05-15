package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9003W extends Component {
    public HAL9003W (String hostname, int x, int y) {
        super(hostname, 95, 5100, ComponentType.WEBSERVER, x ,y);
    }

    public HAL9003W (String hostname, int x, int y, String username, String password, String ipv4, String ipv6) {
        super(hostname, 90, 5100, ComponentType.WEBSERVER, x, y, username, password, ipv4, ipv6);
    }
}


