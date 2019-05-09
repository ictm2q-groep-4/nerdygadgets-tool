package nl.nerdygadgets.infrastructure.components;

/**
 *
 *
 * @author Joris Vos
 */
public class DBloadbalancer extends Component {
    public final double availability = 99.999;
    public final  int price = 2000;
    public final ComponentType componentType = ComponentType.DBLOADBALANCER;

    public DBloadbalancer  (int x, int y) {
        super(x,y);
    }
}
