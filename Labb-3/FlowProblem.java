import java.util.*;

public class FlowProblem {
    Kattio io;

    public static void main(String[] args) {
        new FlowProblem();
    }

    FlowProblem() {
        io = new Kattio(System.in, System.out);

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

        System.out.println(vertexs - 1 + "\n" + s + " " + t + " " + flow + "\n" + posEdges);
        System.out.print(sb);

        io.close();
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
