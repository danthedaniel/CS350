package maze;

public class RedMazeGameCreator extends MazeGameCreator {
    RedMazeGameCreator() {}

    @Override
    protected Room newRoom(int num) {
        return new RedRoom(num);
    }
}
