package maze;

public class BlueMazeFactory extends MazeFactory {
    BlueMazeFactory() {}

    @Override
    public Room MakeRoom(int num) {
        return new GreenRoom(num);
    }

    @Override
    public Wall MakeWall() {
        return new BlueWall();
    }

    @Override
    public Door MakeDoor(Room room1, Room room2) {
        return new BrownDoor(room1, room2);
    }
}
