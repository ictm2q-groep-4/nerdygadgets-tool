package nl.nerdygadgets.infrastructure.components;

public class HAL9003DB {

        public final double availability = 98;
        public final  int price = 12200;
        public final String type = "DBserver";
        private int x;
        private int y;

        public HAL9003DB  (int x, int y) {
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
