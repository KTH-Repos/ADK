import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Steg 3 av 3 (Kombinera steg 1 & 2)
 * Kommando:
 * java CombinedProblem < graffil.indata > matchfil.utdata
 * 
 */

public class CombinedProblem {
    Kattio io;

    public static void main(String args[]) {
        new CombinedProblem();

    }

    CombinedProblem() {
        io = new Kattio(System.in, System.out);
        long start2 = System.currentTimeMillis();

        readBipartiteGraph();

        long end2 = System.currentTimeMillis();
        System.out.println("Elapsed Time in seconds: " + (end2 - start2) / 1000);
        // Kom ihåg att stänga ner Kattio-klassen
        io.close();
    }

    /**
     * Vi läser in alla noder och kanter från input.
     */
    void readBipartiteGraph() {
        // Läs antal hörn och kanter
        // x är noder i vänstra sidan av biparGrafen
        int x = io.getInt();
        // y är noder i höger sida av biparGrafer.
        int y = io.getInt();
        // e är antalet kanter i biparGrafer.
        int e = io.getInt();

        // Läs in kanterna
        int[][] edges = new int[e][2];
        for (int i = 0; i < e; ++i) {
            int a = io.getInt();
            int b = io.getInt();
            edges[i][0] = a + 1;
            edges[i][1] = b + 1;
        }
        writeFlowGraph(x, y, e, edges);
    }

    void writeFlowGraph(int x, int y, int e, int[][] edges) {
        int vertexs = x + y + 2;
        int source = 1;
        int sanka = vertexs;
        int numEdges = x + y + e;

        // Skriv ut antal hörn och kanter samt källa och sänka
        // io.println(vertexs);
        // io.println(source + " " + sanka);
        // io.println(numEdges);

        // Kant a och b med kapacitet c.
        int a = 1;
        int b;
        int c = 1;

        StringBuilder sb = new StringBuilder();
        sb.append(vertexs + "\n").append(source + " " + sanka + "\n").append(numEdges + "\n");

        // Första loopen ger oss kanterna från källan till Xnoderna.
        for (int i = 2; i <= x + 1; i++) {
            b = i;
            // Kant från a till b med kapacitet c
            sb.append(a + " " + b + " " + c + "\n");
        }

        // Denna fick vi från BigRed(skelettkoden).
        // Andra loopen ger oss kanterna från Xnoderna till Ynoderna.
        for (int i = 0; i < e; ++i) {
            a = edges[i][0];
            b = edges[i][1];
            // Kant från a till b med kapacitet c
            sb.append(a + " " + b + " " + c + "\n");
        }

        // Sista loopen ger oss kanterna från Ynoderna till sänkan/utloppet.
        b = sanka;
        for (int i = x + 2; i < b; i++) {
            a = i;
            // Kant från a till b med kapacitet c
            sb.append(a + " " + b + " " + c + "\n");
        }

        // Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
        // io.flush();

        // Debugutskrift
        // System.err.println("Skickade iväg flödesgrafen");

        // System.err.println("writeFlowGraph: " + sb.toString());
        // System.out.println("Im here!!");
        String flowProblem = solveFlowProblem(sb.toString());

        readFlowProblem(flowProblem, x, y);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void readFlowProblem(String flowProblem, int x, int y) {
        // Läs in antal hörn, kanter, källa, sänka, och totalt flöde
        // (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
        // skickade iväg)
        InputStream is = new ByteArrayInputStream(flowProblem.getBytes());
        io = new Kattio(is);

        int vertexs = io.getInt();
        int source = io.getInt();
        int sanka = io.getInt();
        int totflow = io.getInt();
        int e = io.getInt();

        int a;
        int b;
        int flow;

        // Skriver ut antal Xnoder och Ynoder.
        System.out.println(x + " " + y);
        // System.err.println(x + " " + y);
        // Skriver ut det totala flödet.
        System.out.println(totflow);
        // System.err.println(totflow);
        for (int i = 0; i < e; ++i) {
            // Flöde f från a till b
            a = io.getInt();
            b = io.getInt();
            flow = io.getInt();

            if (b != sanka && a != source) {
                System.out.println((a - 1) + " " + (b - 1));
            }
        }
    }
    //////////////////// Steg 2 - MaxFlowProblem
    //////////////////// /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String solveFlowProblem(String input) {
        InputStream is = new ByteArrayInputStream(input.getBytes());

        io = new Kattio(is);

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

        String solution = (vertexs - 1 + "\n" + source + " " + sankan + " " +
                totalFlow + "\n" + posEdges + "\n");
        solution += sb.toString();
        // System.err.println(solution);
        return solution;
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
