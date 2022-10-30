import java.util.*;

/**
 * Steg 2 av 3 (Flödesproblemet)
 * Kommando:
 * java FlowProblem < graffil > matchfil
 * 
 */

public class FlowProblem {
    Kattio io;

    public static void main(String[] args) {
        new FlowProblem();
    }

    FlowProblem() {
        io = new Kattio(System.in, System.out);

        int vertexs = io.getInt() + 1;
        int source = io.getInt();
        int sankan = io.getInt();
        int e = io.getInt();

        Node[] graph = new Node[vertexs];

        graph = initGraph(graph, vertexs, e);

        int totalFlow = 0;

        while (true) {
            // Parent array used for storing path
            // (parent[i] stores edge used to get to node i)
            Edge[] parent = new Edge[vertexs];

            ArrayList<Node> list = new ArrayList<>();
            list.add(graph[source]);

            // BFS finding shortest augmenting path
            while (!list.isEmpty()) {
                Node curr = list.remove(0);

                // Checks that edge has not yet been visited, and it doesn't
                // point to the source, and it is possible to send flow through it.
                for (Edge edge : curr.edges)
                    if (parent[edge.v] == null && edge.v != source && edge.capacity > edge.flow) {
                        parent[edge.v] = edge;
                        list.add(graph[edge.v]);
                    }
            }
            // If sankan was NOT reached, no augmenting path was found.
            // Algorithm terminates and prints out max flow.
            if (parent[sankan] == null)
                break;

            // If sankan WAS reached, we will push more flow through the path
            int pushFlow = Integer.MAX_VALUE;

            // Finds maximum flow that can be pushed through given path
            // by finding the minimum residual flow of every edge in the path
            for (Edge edge = parent[sankan]; edge != null; edge = parent[edge.u])
                pushFlow = Math.min(pushFlow, edge.capacity - edge.flow);

            // Adds to flow values and subtracts from reverse flow values in path
            for (Edge edge = parent[sankan]; edge != null; edge = parent[edge.u]) {
                edge.flow += pushFlow;
                edge.reverse.flow -= pushFlow;
            }
            totalFlow += pushFlow;
        }

        // System.out.println("Max Flow: " + totalFlow);

        StringBuilder sb = new StringBuilder();
        int posEdges = 0;
        for (Node node : graph) {
            for (Edge edge : node.edges) {
                if (edge.flow > 0) {
                    sb.append(edge.u).append(" ").append(edge.v).append(" ").append(edge.flow).append("\n");
                    posEdges++;
                }
            }
        }
        System.out.println(vertexs - 1 + "\n" + source + " " + sankan + " " + totalFlow + "\n" + posEdges);
        System.out.print(sb);

        io.close();
    }

    Node[] initGraph(Node[] graph, int vertexs, int e) {
        // Initialize each node
        for (int i = 0; i < vertexs; i++)
            graph[i] = new Node();

        // Initialize each edge
        for (int i = 0; i < e; i++) {
            int u = io.getInt();
            int v = io.getInt();
            int c = io.getInt();

            // Note edge "b" is not actually in the input graph
            // It is a construct that allows us to solve the problem
            Edge a = new Edge(u, v, 0, c);
            Edge b = new Edge(v, u, 0, 0);

            // Set pointer from each edge "a" to
            // its reverse edge "b" and vice versa
            a.setReverse(b);
            b.setReverse(a);

            graph[u].edges.add(a);
            graph[v].edges.add(b);

        }
        return graph;
    }
}
