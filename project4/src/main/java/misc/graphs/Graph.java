package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
        private IList<V> vertices;
        private IList<E> edges;
        private IDictionary<V, ISet<E>> theList;
        
    public Graph(IList<V> vertices, IList<E> edges) {
        this.vertices = vertices;
        this.edges = edges;
        this.theList = new ChainedHashDictionary<>(); // or should it be ChainedHashDictionary?
        for (E edge : edges) {
            if (edge.getWeight() < 0 || !vertices.contains(edge.getVertex1()) || 
                                        !vertices.contains(edge.getVertex2())) {
                throw new IllegalArgumentException();
            }
            if (!theList.containsKey(edge.getVertex1())) {
                ISet<E> theSet = new ChainedHashSet<>();
                theSet.add(edge);
                theList.put(edge.getVertex1(), theSet);
            } else {
                ISet<E> addSet = theList.get(edge.getVertex1());
                addSet.add(edge);
                theList.put(edge.getVertex1(), addSet);
            }
            if (!theList.containsKey(edge.getVertex2())) { 
                ISet<E> theSet = new ChainedHashSet<>();
                theSet.add(edge);
                theList.put(edge.getVertex2(), theSet);
            } else {
                ISet<E> addSet = theList.get(edge.getVertex2());
                addSet.add(edge);
                theList.put(edge.getVertex2(), addSet);
            }
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mst = new ChainedHashSet<>();
        IDisjointSet<V> setOfVertices = new ArrayDisjointSet<>();
        IPriorityQueue<E> heap = new ArrayHeap<>();
        IList<E> sortedEdges = new DoubleLinkedList<>();
        for (V vertex : this.vertices) {
           setOfVertices.makeSet(vertex);
        }
        // sort edges
        for (E edge : edges) {
            heap.insert(edge);
        }
        for (int i = 0; i < edges.size(); i++) {
            sortedEdges.add(heap.removeMin());
        }
        for (E edge : sortedEdges) {
            if (setOfVertices.findSet(edge.getVertex1()) != setOfVertices.findSet(edge.getVertex2())) {
                setOfVertices.union(edge.getVertex1(), edge.getVertex2());
                mst.add(edge);
            }
        }
        return mst;
    }
    
    /**
     * def kruskal():
           mst = new SomeSet<Edge>()

            for (v : vertices):
                makeMST(v)
            
            sort edges in ascending order by their weight
            
            for (edge : edges):
                if findMST(edge.src) != findMST(edge.dst):
                    union(edge.src, edge.dst)
                    mst.add(edge)
            return mst
     */
    

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    
    /**
    Dijkstra's algorithm simplified pseudocode from lecture
    
    def dijkstra(start):
        for (v : vertices):
            set cost(v) to infinity
        set cost(start) to 0

        while (we still have unvisited nodes):
            current = get next smallest node

        for (edge : current.getOutEdges()):
            newCost = min(cost(current) + edge.cost, cost(edge.dest))
            update cost(edge.dest) to newCost, update backpointers, etc

        return backpointers dictionary
        
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        // throw exceptions (taken care of by adjacency list
        if (!theList.containsKey(start) || !theList.containsKey(end)) {
            throw new NoPathExistsException();
        }
        // double linked list output
        IList<E> shortestPath = new DoubleLinkedList<>();
        // stores vertex to the subclass object as hash dictionary
        IDictionary<V, MyCoolSubclass<V>> allVertices = new ChainedHashDictionary<>();
        
        // puts costs of all vertices to positive infinity
        for (V vertex : this.vertices) {
            allVertices.put(vertex, new MyCoolSubclass<>(vertex, Double.POSITIVE_INFINITY));
        }
        // creates new min heap 
        ArrayHeap<MyCoolSubclass<V>> minHeap = new ArrayHeap<>();
        // redeclares the start vertex cost of object to 0.0
        allVertices.put(start, new MyCoolSubclass<>(start, 0.0));
        // puts everything in the dictionary (all vertices as subclass) in arrayheap
        for (KVPair<V, Graph<V, E>.MyCoolSubclass<V>> vertex : allVertices) {
            minHeap.insert(vertex.getValue()); 
        }
        // sets starting index / vertex
        V currentVertex = start;
        // declares visited set
        ISet<V> visited = new ChainedHashSet<>();
        
        // while loop condition: if current vertex is not at the end (aka didnt find the vertex yet)
        while (!currentVertex.equals(end)) {
            // add current vertex to visited (visit the vertex)
            visited.add(currentVertex);
            // get all of its neighbors (all E's that current vertex is connected to)
            ISet<E> neighbors = theList.get(currentVertex);
            // get current cost of current index
            double currentCost = allVertices.get(currentVertex).getCost();
            // for all edges that current vertex is leading to 
            for (E edge : neighbors) {
                // get the vertex the current vertex is leading to (one of them)
                V neighbor = edge.getOtherVertex(currentVertex);
                // determine the cost of the neighboring vertex (current cost)
                double costNeighbor = allVertices.get(neighbor).getCost();
                // compare new cost with the current cost of the vertex
                if (currentCost + edge.getWeight() < costNeighbor && !visited.contains(neighbor)) {
                    // update the cost to a lower cost if applicable
                    allVertices.get(neighbor).setCost(currentCost + edge.getWeight());
                    // decrease priority with percolate up and (cost is already resetted previously)
                    minHeap.remove(allVertices.get(neighbor));
                    // set the previous vertex for the neighbor vertex to the current vertex
                    allVertices.get(neighbor).setPreVertex(currentVertex);
                }   
            } 
            // call removemin on the array heap
            MyCoolSubclass<V> min = minHeap.removeMin();
            // checks if returned vertex has already been visited (continues to call removemin if so)
            while (visited.contains(min.getVertex()) && min.getCost() < Double.POSITIVE_INFINITY) {
                min = minHeap.removeMin();
            }
            if (min.getCost() == Double.POSITIVE_INFINITY) {
                throw new NoPathExistsException();
            }
            // gets the vertex of the min returned by array heap
            currentVertex = min.getVertex();
        } // reaches end of previous while loop (completes dijkstras
        // starts from the target vertex (end)
        V current = end;
        // while the current in the while loop is not at the start yet
        while (!current.equals(start)) {
            // finds the previous target vertex found by dijkstra's
            V prev = allVertices.get(current).getPreVertex();   
            // get the set of all edges that the current vertex links to (with adjacency list)
            ISet<E> allEdges = theList.get(current);
            // for all edges that it links to 
            IPriorityQueue<E> parallelCheck = new ArrayHeap<>();
            for (E edge : allEdges) {
                // check if it leads to the target previous edge
                if (edge.getOtherVertex(current).equals(prev)) {
                    // adds the edge to the shortest path (if found
                    parallelCheck.insert(edge); // 
                }
            }
            shortestPath.add(parallelCheck.removeMin());
            // sets the current to the target previous node and START AGAIN
            current = prev; 
        }
        IList<E> rightShortestPath = new DoubleLinkedList<>();
        int size = shortestPath.size();
        for (int i = size - 1; i >= 0; i--) {
            rightShortestPath.add(shortestPath.get(i));
        }
        // return the right answer thanks
        return rightShortestPath;
    }
    
    private class MyCoolSubclass<T> implements Comparable<MyCoolSubclass<T>> {
        private T vertex;
        private Double cost;
        private T previousVertex;
        
        public MyCoolSubclass(T vertex, Double cost) {
            this.vertex = vertex;
            this.cost = cost;
        }
        
        public Double getCost() {
            return this.cost;
        }
        
        public void setPreVertex(T item) {
            this.previousVertex = item;
        }
        
        public T getPreVertex() {
            return this.previousVertex;
        }
        
        public T getVertex() {
            return this.vertex; 
        }
        public void setCost(Double newCost) {
            this.cost = newCost;
        }
        
        public int compareTo(MyCoolSubclass<T> other) {
            if (this.cost > other.getCost()) {
                return 1;
            } else if (this.cost < other.getCost()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}