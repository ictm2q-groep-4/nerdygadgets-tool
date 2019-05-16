package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9003DB extends Component {
    public HAL9003DB (String hostname, int x, int y) { super(hostname, 98, 12200, ComponentType.DATABASESERVER, x ,y); }

    public HAL9003DB (String hostname, int x, int y, String username, String password, String ipv4, String ipv6) {
        super(hostname, 98, 12200, ComponentType.DATABASESERVER, x, y, username, password, ipv4, ipv6);
    }
}
