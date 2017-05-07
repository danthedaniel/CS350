package maze;

import maze.ui.MazeViewer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class MazeGame {
    private static Maze CreateMaze(MazeFactory factory)
    {
        Maze maze = factory.MakeMaze();
        Room room1 = factory.MakeRoom(0);
        Room room2 = factory.MakeRoom(1);
        Door door1 = factory.MakeDoor(room1, room2);

        room1.setSide(Direction.East, door1);
        room1.setSide(Direction.North, factory.MakeWall());
        room1.setSide(Direction.South, factory.MakeWall());
        room1.setSide(Direction.West, factory.MakeWall());

        room2.setSide(Direction.East, factory.MakeWall());
        room2.setSide(Direction.North, factory.MakeWall());
        room2.setSide(Direction.South, factory.MakeWall());
        room2.setSide(Direction.West, door1);

        maze.addRoom(room1);
        maze.addRoom(room2);
        maze.setCurrentRoom(room1);

        return maze;
    }

    private static Maze CreateMazeFromFile(MazeFactory factory, String path) throws java.io.IOException {
        Maze maze = factory.MakeMaze();
        Stream<String> fileLines = Files.lines(Paths.get(path));

        ArrayList<String[]> doors = new ArrayList<>();
        HashMap<String, ArrayList<Pair<Room, Direction>>> doorConnections = new HashMap<>();
        ArrayList<Pair<Room, Pair<Integer, Direction>>> roomConnections = new ArrayList<>();

        fileLines.forEach(line -> {
            String[] tokens = line.split("\\s+");

            if (tokens[0].equals("door")) {
                // Save the line for later
                doors.add(tokens);
            } else if (tokens[0].equals("room")) {
                Integer roomNumber = Integer.parseInt(tokens[1]);
                Room room = factory.MakeRoom(roomNumber);

                // Check all sides in file for doors
                for (int i = 0; i < Direction.values().length; i++) {
                    String side = tokens[i + 2];
                    // Set sides to walls by default
                    Direction dir = Direction.values()[i];
                    room.setSide(dir, factory.MakeWall());

                    switch (side.toCharArray()[0]) {
                        case 'd': // Door
                            ArrayList<Pair<Room, Direction>> connections = doorConnections.getOrDefault(side, new ArrayList<>());
                            Pair<Room, Direction> pair = new Pair<>(room, dir);
                            connections.add(pair);
                            doorConnections.put(side, connections);
                            break;
                        case 'w': // Wall
                            break;
                        default: // Room
                            Pair<Integer, Direction> connection = new Pair<>(Integer.parseInt(side), dir);
                            roomConnections.add(new Pair<>(room, connection));
                            break;
                    }
                }

                maze.addRoom(room);
            }
        });

        // Add doors and connect to rooms
        doors.forEach(doorLine -> {
            String doorName = doorLine[1];
            ArrayList<Pair<Room, Direction>> connections = doorConnections.getOrDefault(doorName, new ArrayList<>());

            if (connections.size() == 2) {
                Door door = factory.MakeDoor(connections.get(0).left, connections.get(1).left);
                if (doorLine[4].equals("open"))
                    door.setOpen(true);

                // Add door to rooms
                connections.forEach(connection -> connection.left.setSide(connection.right, door));
            }
        });

        // Connect rooms
        roomConnections.forEach(pair -> {
            Direction dir = pair.right.right;
            Integer otherRoomNum = pair.right.left;
            pair.left.setSide(dir, maze.getRoom(otherRoomNum));
        });

        if (maze.iterator().hasNext())
            maze.setCurrentRoom(maze.iterator().next());

        return maze;
    }

    public static void main(String[] args)
    {
        MazeFactory factory;
        Maze maze;

        if (args.length > 0) {
            switch (args[0]) {
                case "red":
                    factory = new RedMazeFactory();
                    break;
                case "blue":
                    factory = new BlueMazeFactory();
                    break;
                default:
                    factory = new BasicMazeFactory();
                    break;
            }
        } else {
            System.err.println("A maze color must be provided as the first argument. Use \"basic\" for the default maze.");
            return;
        }

        if (args.length > 1) {
            try {
                maze = CreateMazeFromFile(factory, args[1]);
            } catch (java.io.IOException e) {
                System.err.println("Could not open specified file.");
                return;
            }
        } else {
            maze = CreateMaze(factory);
        }

        MazeViewer viewer = new MazeViewer(maze);
        viewer.run();
    }
}
