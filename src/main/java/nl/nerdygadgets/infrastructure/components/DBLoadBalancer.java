package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class DBLoadBalancer extends Component {
    public DBLoadBalancer(String hostname, int x, int y) {
        super(hostname, 99.999, 2000, ComponentType.DBLOADBALANCER, x ,y);
    }
}
