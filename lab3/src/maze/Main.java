package maze;

import maze.ui.MazeViewer;

public class Main {
    public static void main(String[] args)
    {
        Maze maze;
        MazeGameCreator factory;

        if (args.length > 0) {
            switch (args[0]) {
                case "red":
                    factory = new RedMazeGameCreator();
                    break;
                case "blue":
                    factory = new BlueMazeGameCreator();
                    break;
                default:
                    factory = new MazeGameCreator();
                    break;
            }
        } else {
            System.err.println("A maze color must be provided as an argument.");
            return;
        }

        if (args.length > 1) {
            maze = factory.fromFile(args[1]);
        } else {
            maze = factory.createMaze();
        }

        MazeViewer viewer = new MazeViewer(maze);
        viewer.run();
    }
}
