package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class DBloadbalancer extends Component {
    public DBloadbalancer  (String hostname, int x, int y) {
        super(hostname, 99.999, 200, ComponentType.DBLOADBALANCER, x ,y);
    }
}
