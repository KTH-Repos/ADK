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
        int s = io.getInt();
        int t = io.getInt();
        int edges = io.getInt();

        ArrayList<Edge>[] graph = (ArrayList<Edge>[]) new ArrayList[vertexs];

        graph = initGraph(graph, vertexs, edges);

        int flow = 0;
        int[] q = new int[graph.length];
        while (true) {
            int qt = 0;
            q[qt++] = s;
            Edge[] pred = new Edge[graph.length];
            for (int qh = 0; qh < qt && pred[t] == null; qh++) {
                int cur = q[qh];
                for (Edge e : graph[cur]) {
                    if (pred[e.t] == null && e.cap > e.f) {
                        pred[e.t] = e;
                        q[qt++] = e.t;
                    }
                }
            }
            if (pred[t] == null)
                break;
            int df = Integer.MAX_VALUE;
            for (int u = t; u != s; u = pred[u].s)
                df = Math.min(df, pred[u].cap - pred[u].f);
            for (int u = t; u != s; u = pred[u].s) {
                pred[u].f += df;
                graph[pred[u].t].get(pred[u].rev).f -= df;
            }
            flow += df;
        }
        // System.out.println("Max Flow: " + totalFlow);

        StringBuilder sb = new StringBuilder();
        int posEdges = 0;

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

        String solution = (vertexs - 1 + "\n" + s + " " + t + " " +
                flow + "\n" + posEdges + "\n");
        solution += sb.toString();
        // System.err.println(solution);
        return solution;
    }

    ArrayList<Edge>[] initGraph(ArrayList<Edge>[] graph, int vertexs, int e) {
        for (int i = 0; i < vertexs; i++) {
            graph[i] = new ArrayList<>();
        }

        // Initialize each edge
        for (int i = 0; i < e; i++) {
            int u = io.getInt(); // source
            int v = io.getInt(); // sink
            int c = io.getInt();

            graph[u].add(new Edge(u, v, graph[v].size(), c));
            graph[v].add(new Edge(v, u, graph[u].size() - 1, 0));
        }

        return graph;

    }

}
