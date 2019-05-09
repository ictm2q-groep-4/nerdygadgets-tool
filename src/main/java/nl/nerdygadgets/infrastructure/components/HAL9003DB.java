package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class HAL9003DB extends Component {
        public final double availability = 98;
        public final  int price = 12200;
        public final ComponentType componentType = ComponentType.DATABASESERVER;

        public HAL9003DB  (int x, int y) {
            super(x,y);
        }
}
