/*
 * SimpleMazeGame.java
 * Copyright (c) 2008, Drexel University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Drexel University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY DREXEL UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DREXEL UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package maze;

import maze.ui.MazeViewer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public class SimpleMazeGame
{
	/**
	 * Creates a small maze.
	 */
	private static Maze createMaze()
    {
		Maze maze = new Maze();
		Room room1 = new Room(0);
		Room room2 = new Room(1);
		Door door1 = new Door(room1, room2);

		room1.setSide(Direction.East, door1);
		room1.setSide(Direction.North, new Wall());
		room1.setSide(Direction.South, new Wall());
		room1.setSide(Direction.West, new Wall());

        room2.setSide(Direction.East, new Wall());
        room2.setSide(Direction.North, new Wall());
        room2.setSide(Direction.South, new Wall());
        room2.setSide(Direction.West, door1);

		maze.addRoom(room1);
		maze.addRoom(room2);
		maze.setCurrentRoom(room1);

		return maze;
	}

	private static void addSites(Maze maze, Stream fileLines) {
        ArrayList<String[]> doors = new ArrayList<>();
        HashMap<String, ArrayList<Pair<Room, Direction>>> doorConnections = new HashMap<>();
        ArrayList<Pair<Room, Pair<Integer, Direction>>> roomConnections = new ArrayList<>();

	    fileLines.forEach(line -> {
            String[] tokens = line.toString().split("\\s+");

            if (tokens[0].equals("door")) {
                // Save the line for later
                doors.add(tokens);
            } else if (tokens[0].equals("room")) {
                Integer roomNumber = Integer.parseInt(tokens[1]);
                Room room = new Room(roomNumber);

                // Check all sides in file for doors
                for (int i = 0; i < Direction.values().length; i++) {
                    String side = tokens[i + 2];
                    // Set sides to walls by default
                    Direction dir = Direction.values()[i];
                    room.setSide(dir, new Wall());

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
                Door door = new Door(connections.get(0).left, connections.get(1).left);
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
    }

	private static Maze loadMaze(final String path)
	{
		Maze maze = new Maze();

        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            addSites(maze, stream);
        } catch (java.io.IOException e) {
            System.err.println("Could not read file specified.");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Malformed file.");
        }

        // Set the current room to room 0
        if (maze.getNumberOfRooms() > 0)
            maze.setCurrentRoom(0);

		return maze;
	}

	public static void main(String[] args)
	{
        Maze maze;
	    if (args.length > 0) {
	        maze = loadMaze(args[0]);
        } else {
	        maze = createMaze();
        }

	    MazeViewer viewer = new MazeViewer(maze);
	    viewer.run();
	}
}
