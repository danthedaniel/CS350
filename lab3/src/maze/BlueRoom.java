package maze;

import java.awt.Color;

public class BlueRoom extends Room {
    public BlueRoom(int num) {
        super(num);
    }

    @Override
    public Color getColor() {
        return Color.BLUE;
    }
}
