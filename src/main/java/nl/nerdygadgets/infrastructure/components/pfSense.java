package nl.nerdygadgets.infrastructure.components;

/**
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class pfSense extends Component {
    public pfSense (String hostname, int x, int y) {
        super(hostname, 99.999, 2000, ComponentType.FIREWALL, x ,y);
    }
}
