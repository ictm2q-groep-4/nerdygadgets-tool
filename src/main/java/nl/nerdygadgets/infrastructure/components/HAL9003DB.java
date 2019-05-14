package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9003DB extends Component {
        public HAL9003DB (String hostname, int x, int y) {
                super(hostname, 98, 12200, ComponentType.DATABASESERVER, x ,y);
        }
}
