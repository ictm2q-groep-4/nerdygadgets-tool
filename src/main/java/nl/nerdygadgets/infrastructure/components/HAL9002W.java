package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class HAL9002W extends Component {
    public final double availability = 90;
    public final int price = 3200;
    public final ComponentType componentType = ComponentType.WEBSERVER;

    public HAL9002W(int x, int y) {
        super(x,y);
    }
}
