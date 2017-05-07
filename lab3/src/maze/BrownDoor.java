package maze;

import java.awt.Color;

public class BrownDoor extends Door {
    public BrownDoor(Room room1, Room room2) {
        super(room1, room2);
    }

    @Override
    public Color getColor() {
        return new Color(170, 100, 30);
    }
}
