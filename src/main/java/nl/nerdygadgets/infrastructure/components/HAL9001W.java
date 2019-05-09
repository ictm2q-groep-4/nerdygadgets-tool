package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class HAL9001W extends Component {
    public final double availability = 80;
    public final  int price = 2200;
    public final ComponentType componentType = ComponentType.WEBSERVER;

    public HAL9001W   (int x, int y) {
       super(x,y);
    }
}
