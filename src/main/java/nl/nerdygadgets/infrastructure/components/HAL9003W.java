package nl.nerdygadgets.infrastructure.components;

public class HAL9003W implements Component {

    public final double availability = 95;
    public final  int price = 5100;
    public final String type = "Webserver";
    private int x;
    private int y;

    public HAL9003W   (int x, int y) {
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
