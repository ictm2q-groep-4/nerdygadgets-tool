package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class HAL9001DB extends Component {
    public final double availability = 90 ;
    public final  int price = 5100;
    public final ComponentType componentType = ComponentType.DATABASESERVER;

    public HAL9001DB (int x, int y) {
        super(x,y);

    }
}
