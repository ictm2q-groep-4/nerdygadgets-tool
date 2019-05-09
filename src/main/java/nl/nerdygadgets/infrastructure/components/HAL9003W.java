package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class HAL9003W extends Component {
    public final double availability = 95;
    public final int price = 5100;
    public final ComponentType componentType = ComponentType.WEBSERVER;

    public HAL9003W(int x, int y) {
        super(x, y);
    }
}


