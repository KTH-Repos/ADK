import java.util.*;

/**
 * Steg 2 av 3
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
        int edges = io.getInt();

        // Vi skapar vår graf.
        ArrayList<Edge>[] graph = (ArrayList<Edge>[]) new ArrayList[vertexs];

        graph = initGraph(graph, vertexs, edges);

        int flow = 0;
        // Vi skapar en ny int array med längden av grafen.
        int[] nodeList = new int[graph.length];

        // BFS
        while (true) {
            int temp = 0;
            // Vi sätter värdet av SOURCE på alla platser i nodeList index temp.
            nodeList[temp++] = source;
            // Vi skapar en Edge array med längden av grafen.
            Edge[] edgeList = new Edge[graph.length];

            // Vi söker efter obesökta kanter som inte pekar mot källan och
            // flöde kan rinna igenom....
            for (int i = 0; i < temp && edgeList[sankan] == null; i++) {
                int cur = nodeList[i];
                for (Edge e : graph[cur]) {
                    if (edgeList[e.t] == null && e.cap > e.f) {
                        edgeList[e.t] = e;
                        nodeList[temp++] = e.t;
                    }
                }
            }

            // Om sänken inte nåddes så skriver vi bara ut maxflow. // breakar ut
            if (edgeList[sankan] == null) {
                break;
            }

            int pushFlow = Integer.MAX_VALUE;

            // Först tar vi fram minimala flow.
            for (int u = sankan; u != source; u = edgeList[u].s) {
                pushFlow = Math.min(pushFlow, edgeList[u].cap - edgeList[u].f);
            }

            // Sedan uppdater vi kanterna med minimala flow längst stigarna.
            for (int u = sankan; u != source; u = edgeList[u].s) {
                edgeList[u].f += pushFlow;
                // Tar bort för reverese
                graph[edgeList[u].t].get(edgeList[u].rev).f -= pushFlow;
            }
            // Maxflödet kommer vara summan av alla minimala flödet längs alla stigarna.
            flow += pushFlow;
        }

        StringBuilder sb = new StringBuilder();
        int posEdges = 0;

        // Går igenom alla kanter i graphen och plockar ut alla med positivt flöde
        for (int i = 0; i < graph.length; i++) {
            ArrayList<Edge> temp = graph[i];
            for (int j = 0; j < temp.size(); j++) {
                if (temp.get(j).f > 0) {
                    sb.append(temp.get(j).s).append(" ").append(temp.get(j).t).append(" ").append(temp.get(j).f)
                            .append("\n");
                    posEdges++;
                }
            }
        }

        // Vi skriver ut resultatet.
        System.out.println(vertexs - 1 + "\n" + source + " " + sankan + " " + flow + "\n" + posEdges);
        System.out.print(sb);

        io.close();
    }

    // Funktionen som skapar grafen.
    ArrayList<Edge>[] initGraph(ArrayList<Edge>[] graph, int vertexs, int e) {

        // För varje nod skapar vi en ny arraylista.
        for (int i = 0; i < vertexs; i++) {
            graph[i] = new ArrayList<>();
        }

        // Skapar varje kant
        for (int i = 0; i < e; i++) {
            int u = io.getInt(); // source
            int v = io.getInt(); // sink
            int c = io.getInt(); // capacity

            graph[u].add(new Edge(u, v, graph[v].size(), c));
            graph[v].add(new Edge(v, u, graph[u].size() - 1, 0));
        }

        // Slutligen returnerar vi grafen.
        return graph;
    }
}
