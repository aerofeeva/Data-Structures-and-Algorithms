package mazes.generators.maze;

import java.util.Random;

import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {

        Random rand = new Random();
        ISet<Wall> walls = maze.getWalls();
        for (Wall eachWall : walls) {
            eachWall.setDistance(rand.nextDouble());
        }
        Graph<Room, Wall> map = new Graph<>(maze.getRooms(), walls);
        ISet<Wall> toRemove = map.findMinimumSpanningTree();
        for (Wall eachWall : toRemove) {
            eachWall.resetDistanceToOriginal();
        }
        return toRemove;
    }
}
