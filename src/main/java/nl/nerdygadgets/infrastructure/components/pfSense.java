package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class pfSense extends Component {
    public final double availability = 99.999;
    public final  int price = 2000;
    public final ComponentType componentType = ComponentType.FIREWALL;

    public pfSense(int x, int y)  {
        super(x,y);
    }
}
