import java.util.*;

/**
 * The goal of this task is to find maximum flow (bandwidth between 2 given (network) nodes.
 * Each edge as a capacity, marking how much traffic it can transfer between source and target vertex.
 * Task uses Ford-Fulkerson method and Edmonds-Karp algorithm (wihich uses breadth-first search to discover the paths).
 * <p>
 * Limitation: All the edges must be 2-directional,
 * doesn't create a reverse edge, if it doesn't exist. Although it can be implemented as well.
 * 2-directional edges can have separate capacities. (or one way can be 0).
 */
public class GraphTask {

    /**
     * Main method.
     */
    public static void main(String[] args) {
        GraphTask a = new GraphTask();
        a.run();
    }

    /**
     * Actual main method to run examples and everything.
     */
    public void run() {
        System.out.println("Result: " + testRandomGraph("v1", "v2"));

        System.out.println("Result: " + testGraph("v1", "v0"));
        System.out.println("Result: " + testGraph("v2", "v3"));
        System.out.println("Result: " + testGraph("v0", "v1"));
    }

    private int testRandomGraph(String from, String to) {
        Graph graph = new Graph("G: " + from + " -> " + to + " ");
        graph.createRandomSimpleGraph(4, 4);
        System.out.println(graph.id);
        return graph.fordFulkerson(graph.getNode(from), graph.getNode(to));
    }

    private int testGraph(String from, String to) {
        Graph graph = new Graph("G2: " + from + " -> " + to + " ");

        Vertex v3 = graph.createVertex("v3");
        Vertex v2 = graph.createVertex("v2");
        Vertex v1 = graph.createVertex("v1");
        Vertex v0 = graph.createVertex("v0");


        graph.createArc("v3_v2", v3, v2, 0);
        graph.createArc("v3_v1", v3, v1, 0);

        graph.createArc("v2_v3", v2, v3, 3);
        graph.createArc("v2_v1", v2, v1, 1);
        graph.createArc("v2_v0", v2, v0, 0);

        graph.createArc("v1_v3", v1, v3, 8);
        graph.createArc("v1_v2", v1, v2, 1);
        graph.createArc("v1_v0", v1, v0, 0);

        graph.createArc("v0_v2", v0, v2, 8);
        graph.createArc("v0_v1", v0, v1, 4);

        System.out.println(graph.id);
        return graph.fordFulkerson(graph.getNode(from), graph.getNode(to));
    }

    /**
     * Class representing a vertex, has following parameters
     */
    class Vertex {

        /**
         * Vertex id
         */
        private String id;

        /**
         * Link to next vertex
         */
        private Vertex next;

        /**
         * Link to first edge, originating from this vertex
         */
        private Arc first;

        /**
         * Additional vertex info, not used currently
         */
        private int info = 0;

        /**
         * Contains the path, which was used to get here from previous vertex
         */
        private Arc path;

        /**
         * Constructor for creating a vertex
         *
         * @param s String - Vertex Id
         * @param v Vertex - link to next vertex
         * @param e Arc - link to first edge (Arc)
         */
        Vertex(String s, Vertex v, Arc e) {
            id = s;
            next = v;
            first = e;
        }

        /**
         * Constructor for creating a vertex
         *
         * @param s String - Vertex Id
         */
        Vertex(String s) {
            this(s, null, null);
        }


        /**
         * To string method
         *
         * @return String
         */
        @Override
        public String toString() {
            return id;
        }

        /**
         * Gets all the edges, originating from this vertex
         *
         * @return Iterator<Arc>
         */
        public Iterator getNeighbors() {
            Arc nextArc = first;
            List<Arc> aList = new ArrayList<>();
            aList.add(nextArc);
            while (nextArc.next != null) {
                nextArc = nextArc.next;
                aList.add(nextArc);
            }
            return aList.iterator();
        }

        /**
         * Gets the vertex path
         *
         * @return Arc
         */
        public Arc getPath() {
            return path;
        }

        /**
         * Sets the vertex path
         *
         * @param path Arc
         */
        public void setPath(Arc path) {
            this.path = path;
        }
    }


    /**
     * Arc represents one arrow in the graph. Two-directional edges are
     * represented by two Arc objects (for both directions).
     */
    class Arc {

        /**
         * Edge id
         */
        private String id;

        /**
         * Target vertex, where the edge leads
         */
        private Vertex target;

        /**
         * Source vertex, where the edge originates
         */
        private Vertex source;

        /**
         * Link to next Arc
         */
        private Arc next;

        /**
         * Edge capacity - marks edge bandwidth -
         * how much traffic can be pushed through this edge
         */
        private int capacity;

        /**
         * Constructor for creating the edge (Arc)
         *
         * @param s String - Arc id
         * @param v Vertex - Target vertex (where it leads)
         * @param a int - edge capacity (bandwidth)
         */
        Arc(String s, Vertex v, Arc a) {
            id = s;
            target = v;
            next = a;
            capacity = 0;
        }

        /**
         * Constructor for creating the edge (Arc)
         *
         * @param s String - Arc id
         */
        Arc(String s) {
            this(s, null, null);
        }


        /**
         * To string method
         *
         * @return String
         */
        @Override
        public String toString() {
            return id + " (" + capacity + ")";
        }

        /**
         * Gets the edge capacity
         *
         * @return int
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * Sets the edge capacity
         *
         * @param capacity int
         */
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        /**
         * Increases edge capacity
         *
         * @param bottleneck int
         */
        public void increaseCapacity(int bottleneck) {
            capacity += bottleneck;
        }

        /**
         * Decreases edge capacity
         *
         * @param bottleneck int
         */
        public void decreaseCapacity(int bottleneck) {
            capacity -= bottleneck;
        }

        /**
         * Checks if there are any capacity left on this edge
         *
         * @return boolean
         */
        public Boolean isResidual() {
            return capacity > 0;
        }

        /**
         * Gets the target vertex
         *
         * @return
         */
        public Vertex getTarget() {
            return target;
        }

        /**
         * Finds the reverse edge to current edge (reverses the edge)
         *
         * @return
         */
        public Arc reverse() {
            Arc nextArc = this.target.first;
            if (nextArc.target.id.equals(this.source.id)) return nextArc;

            List<Arc> aList = new ArrayList<>();
            aList.add(nextArc);
            while (nextArc.next != null) {
                nextArc = nextArc.next;
                if (nextArc.target.id.equals(this.source.id)) return nextArc;
            }

            return null;
        }

        /**
         * Gets the edge source vertex path (how we got into this edge source vertex)
         *
         * @return Arc
         */
        public Arc getSourcePath() {
            return source.path;
        }
    }


    /**
     * Class representing the craph.
     * Contains a link to first vertex, which has subsequent links to other vertices.
     * Each vertex has also a link to first edge (Arc), which originates from this vertex.
     * Arcs originating from the same vertex are also linked to each other.
     */
    class Graph {

        /**
         * Graph id
         */
        private String id;

        /**
         * Link to first vertex of this graph
         */
        private Vertex first;

        /**
         * Graph info parameter, currently not used
         */
        private int info = 0;

        /**
         * Graph representation as adjacency matrix
         */
        private int[][] adjMatrix;


        /**
         * Constructor for creating a new graph
         *
         * @param s String - Graph Id
         * @param v Vertex - link to first vertex
         */
        Graph(String s, Vertex v) {
            id = s;
            first = v;
        }

        /**
         * Constructor for creating a new graph
         *
         * @param s String - Graph Id
         */
        Graph(String s) {
            this(s, null);
        }


        /**
         * Finds given vertex by ID
         *
         * @param n String - vertex Id
         * @return Vertex
         */
        public Vertex getNode(String n) {
            Vertex v = first;
            if (v.id.equals(n)) return v;

            while ((v = v.next) != null) {
                if (v.id.equals(n)) return v;
            }
            throw new IllegalArgumentException("Vertext not found: " + n);
        }

        /**
         * To string method
         *
         * @return String
         */
        @Override
        public String toString() {
            String nl = System.getProperty("line.separator");
            StringBuffer sb = new StringBuffer(nl);
            sb.append(id);
            sb.append(nl);
            Vertex v = first;
            while (v != null) {
                sb.append(v.toString());
                sb.append(" -->");
                Arc a = v.first;
                while (a != null) {
                    sb.append(" ");
                    sb.append(a.toString());
                    sb.append(" (");
                    sb.append(v.toString());
                    sb.append("->");
                    sb.append(a.target.toString());
                    sb.append(")");
                    a = a.next;
                }
                sb.append(nl);
                v = v.next;
            }
            return sb.toString();
        }

        /**
         * Creates a new vertex
         *
         * @param vid string - vertex Id
         * @return Vertex
         */
        public Vertex createVertex(String vid) {
            Vertex res = new Vertex(vid);
            res.next = first;
            first = res;
            return res;
        }

        /**
         * Creates a new edge (Arc)
         *
         * @param aid  string - Arc Id
         * @param from Vertex - source vertex
         * @param to   Vertex - destination vertex
         * @return Arc
         */
        public Arc createArc(String aid, Vertex from, Vertex to) {
            Arc res = new Arc(aid);
            res.next = from.first;
            from.first = res;
            res.target = to;
            res.source = from;
            return res;
        }

        /**
         * Creates a new edge (Arc) with capacity
         *
         * @param aid      string - Arc Id
         * @param from     Vertex - source vertex
         * @param to       Vertex - destination vertex
         * @param capacity int - edge capacity
         * @return
         */
        public Arc createArc(String aid, Vertex from, Vertex to, int capacity) {
            Arc a = createArc(aid, from, to);
            a.setCapacity(capacity);
            return a;
        }

        /**
         * Create a connected undirected random tree with n vertices.
         * Each new vertex is connected to some random existing vertex.
         *
         * @param n number of vertices added to this graph
         */
        public void createRandomTree(int n) {
            if (n <= 0)
                return;
            Vertex[] varray = new Vertex[n];
            for (int i = 0; i < n; i++) {
                varray[i] = createVertex("v" + (n - i));
                if (i > 0) {
                    int vnr = (int) (Math.random() * i);
                    createArc("a" + varray[vnr].toString() + "_"
                            + varray[i].toString(), varray[vnr], varray[i], 10);
                    createArc("a" + varray[i].toString() + "_"
                            + varray[vnr].toString(), varray[i], varray[vnr], 10);
                } else {
                }
            }
        }

        /**
         * Create an adjacency matrix of this graph.
         * Side effect: corrupts info fields in the graph
         *
         * @return adjacency matrix
         */
        public int[][] createAdjMatrix() {
            info = 0;
            Vertex v = first;
            while (v != null) {
                v.info = info++;
                v = v.next;
            }
            int[][] res = new int[info][info];
            v = first;
            while (v != null) {
                int i = v.info;
                Arc a = v.first;
                while (a != null) {
                    int j = a.target.info;
                    res[i][j]++;
                    a = a.next;
                }
                v = v.next;
            }
            return res;
        }

        /**
         * Prints the adj. matrix
         */
        public void printAdjMatric() {
            for (int i = 0; i < adjMatrix.length; i++) {
                for (int j = 0; j < adjMatrix[i].length; j++) {
                    System.out.print(adjMatrix[i][j] + ",");
                }
                System.out.println();
            }
        }

        /**
         * Create a connected simple (undirected, no loops, no multiple
         * arcs) random graph with n vertices and m edges.
         *
         * @param n number of vertices
         * @param m number of edges
         */
        public void createRandomSimpleGraph(int n, int m) {
            if (n <= 0)
                return;
            if (n > 2500)
                throw new IllegalArgumentException("Too many vertices: " + n);
            if (m < n - 1 || m > n * (n - 1) / 2)
                throw new IllegalArgumentException
                        ("Impossible number of edges: " + m);
            first = null;
            createRandomTree(n);       // n-1 edges created here
            Vertex[] vert = new Vertex[n];
            Vertex v = first;
            int c = 0;
            while (v != null) {
                vert[c++] = v;
                v = v.next;
            }
            int[][] connected = createAdjMatrix();
            int edgeCount = m - n + 1;  // remaining edges
            while (edgeCount > 0) {
                int i = (int) (Math.random() * n);  // random source
                int j = (int) (Math.random() * n);  // random target
                if (i == j)
                    continue;  // no loops
                if (connected[i][j] != 0 || connected[j][i] != 0)
                    continue;  // no multiple edges

                Vertex vi = vert[i];
                Vertex vj = vert[j];
                createArc("a" + vi.toString() + "_" + vj.toString(), vi, vj, 10);
                connected[i][j] = 1;


                createArc("a" + vj.toString() + "_" + vi.toString(), vj, vi, 10);
                connected[j][i] = 1;
                edgeCount--;  // a new edge happily created
            }
            adjMatrix = connected;
        }

        /**
         * Breadth-first algorithm
         * for checking if there is a path between 2 vertices, using queue
         * Only visits edges, that have residual capacity (capacity is greater than 0)
         *
         * @param source source vertex
         * @param target target vertex
         * @return boolean - is it possible to traverse between given vertices
         */
        private Boolean bfs(Vertex source, Vertex target) {

            if (source.equals(target)) return true;

            // Create a list of visited vertices - this can be also saved as a vertex property, but this is more efficient.
            List<Vertex> visited = new ArrayList();
            // Create a new queue and add source vertex to queue
            LinkedList<Vertex> queue = new LinkedList<>();

            queue.add(source);
            // Mark source vertex as visited
            visited.add(source);


            while (!queue.isEmpty()) {
                Vertex n = queue.remove();

                Iterator<Arc> nEdges = n.getNeighbors();


                while (nEdges.hasNext()) {
                    Arc e = nEdges.next();
                    Vertex w = e.getTarget();
                    // If vertex has not been visited before, and has residual capacity, add it to queue
                    if (!visited.contains(w) && e.isResidual()) {
                        // Add path to vertex.
                        w.setPath(e);
                        // Add edge to queue and mark it as visited
                        queue.add(w);
                        visited.add(w);
                    }
                }
            }

            return visited.contains(target);
        }


        /**
         * Edmonds-Karp algorithm of Ford-Fulkerson method for calculating maximum flow in a flow network.
         * Using Breadth-First search to find possible path between 2 vertices.
         * Complexity: O(|V|^2|E|) - most probably bit worse due to data structure.
         *
         * @param source Vertex - starting vertex
         * @param target Vertex - end vertex
         * @return int - maximum flow between two vertices
         */
        public int fordFulkerson(Vertex source, Vertex target) {

            int maxFlow = 0;
            while (bfs(source, target)) {
                int flow = Integer.MAX_VALUE;

                // Backtrack the path, starting from the end and calculate the bottleneck for the path
                for (Arc edge = target.getPath(); edge != null; edge = edge.getSourcePath()) {
                    flow = Math.min(flow, edge.getCapacity());
                }

                for (Arc edge = target.getPath(); edge != null; edge = edge.getSourcePath()) {
                    // Decrease the capacity on current edge
                    edge.decreaseCapacity(flow);
                    // Increase the capacity on reverse edge
                    edge.reverse().increaseCapacity(flow);
                }

                maxFlow += flow;
            }

            if (maxFlow == 0) throw new RuntimeException("There is not path between given verices");
            return maxFlow;
        }
    }
}

