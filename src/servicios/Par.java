package servicios;

import java.util.Iterator;

public class Par<ClaseX, ClaseY> implements Iterable<Object> {
    private ClaseX x;
    private ClaseY y;

    public Par(ClaseX x, ClaseY y) {
        this.x = x;
        this.y = y;
    }

    public ClaseX getX() {
        return x;
    }

    public void setX(ClaseX x) {
        this.x = x;
    }

    public ClaseY getY() {
        return y;
    }

    public void setY(ClaseY y) {
        this.y = y;
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int indice = 0;

            @Override
            public boolean hasNext() {
                return indice < 2;
            }

            @Override
            public Object next() {
                if (hasNext()) {
                    indice++;
                    return indice == 1 ? x : y;
                }
                return null;
            }

        };

    }

}
