package nl.nerdygadgets.infrastructure.components;

public class HAL9001DB implements Component {


    public final double availability = 90 ;
    public final  int price = 5100;
    public final ComponentType componentType = ComponentType.DATABASESERVER;
    private int x;
    private int y;

    public HAL9001DB (int x, int y) {
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
