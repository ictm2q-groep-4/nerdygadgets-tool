package nl.nerdygadgets.infrastructure.components;

public class HAL9001W implements Component {

    public final double availability = 80;
    public final  int price = 2200;
    public final ComponentType componentType = ComponentType.WEBSERVER;
    private int x;
    private int y;

    public HAL9001W   (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;

    }

    public int getX() {
        return this.x;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getY() {
        return this.y;
    }
}
