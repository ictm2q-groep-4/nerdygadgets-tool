package nl.nerdygadgets.infrastructure.components;

public class HAL9002W implements Component {

    public final double availability = 90;
    public final  int price = 3200;
    public final String type = "Webserver";
    private int x;
    private int y;

    public HAL9002W   (int x, int y) {
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
