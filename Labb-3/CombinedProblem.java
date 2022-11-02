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

    ////////////////////////////// Write solution to matchingproblem from maxflow
    ////////////////////////////// problem
    ////////////////////////////// //////////////////////////////////////////////////////////////////////////////////////////
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
        int edges = io.getInt();

        // Vi skapar vår graf.
        ArrayList<Edge>[] graph = (ArrayList<Edge>[]) new ArrayList[vertexs];

        graph = initGraph(graph, vertexs, edges);

        int flow = 0;
        // Vi skapar en ny int array med längden av grafen.
        int[] nodeList = new int[graph.length];
        while (true) {
            int temp = 0;
            // Vi sätter värdet av s på alla platser i q.
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

            // Om sänken inte nåddes så skriver vi bara ut maxflow.
            if (edgeList[sankan] == null) {
                break;
            }

            int pushFlow = Integer.MAX_VALUE;

            // Först tar vi fram minimala flow.
            for (int u = sankan; u != source; u = edgeList[u].s) {
                pushFlow = Math.min(pushFlow, edgeList[u].cap - edgeList[u].f);
            }

            // Sedan uppdater vi kanterna med minimala flow.
            for (int u = sankan; u != source; u = edgeList[u].s) {
                edgeList[u].f += pushFlow;
                graph[edgeList[u].t].get(edgeList[u].rev).f -= pushFlow;
            }
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

        String solution = (vertexs - 1 + "\n" + source + " " + sankan + " " +
                flow + "\n" + posEdges + "\n");
        solution += sb.toString();
        // System.err.println(solution);
        return solution;
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
