package nl.nerdygadgets.infrastructure.components;

public abstract class Component implements Statistic {

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Component(int x, int y) {
        this.x = x;
        this.y =y;

    }









}
