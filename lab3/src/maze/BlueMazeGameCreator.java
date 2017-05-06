package maze;

public class BlueMazeGameCreator extends MazeGameCreator {
    BlueMazeGameCreator() {}

    @Override
    protected Room newRoom(int num) {
        return new BlueRoom(num);
    }
}
