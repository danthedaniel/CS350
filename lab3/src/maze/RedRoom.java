package maze;

import java.awt.Color;

public class RedRoom extends Room {
    public RedRoom(int num) {
        super(num);
    }

    @Override
    public Color getColor() {
        return new Color(255, 50, 50);
    }
}
