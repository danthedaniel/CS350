package maze;

public class RedMazeFactory extends MazeFactory {
    RedMazeFactory() {}

    @Override
    public Room MakeRoom(int num) {
        return new RedRoom(num);
    }

    @Override
    public Wall MakeWall() {
        return new RedWall();
    }
}
