package nl.nerdygadgets.infrastructure.components;

public class HAL9002DB extends Component {
    public final double availability = 95;
    public final  int price = 7700;
    public final ComponentType componentType = ComponentType.DATABASESERVER;

    public HAL9002DB (int x, int y) {
        super(x,y);
    }
}
