package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 * @author Djabir Omar Mohamed
 */
public class HAL9003DB extends Component {
        /**
         * This is the availability for the server, so how long the server is available
         */
        public final double availability = 98;
        /**
         * This is the price for the server
         */
        public final  int price = 12200;
        /**
         * This is the type of server
         */
        public final ComponentType componentType = ComponentType.DATABASESERVER;
        /**
         * This constructor calls upon the super constructor of the Component class and sets the x and y
         */

        public HAL9003DB  (int x, int y) {
            super(x,y);
        }
}
