package nl.nerdygadgets.infrastructure.components;

public class DBloadbalancer implements Component {

    public final double availability = 99.999;
    public final  int price = 2000;
    public final ComponentType componentType = ComponentType.DBLOADBALANCER;
    private int x;
    private int y;

    public DBloadbalancer  (int x, int y) {
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
