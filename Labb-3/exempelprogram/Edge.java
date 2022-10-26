public class Edge {
    int u;
    int v;
    int flow;
    int capacity;
    Edge reverse;

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
    }

    public Edge(int u, int v, int flow, int capacity) {
        this.u = u;
        this.v = v;
        this.flow = flow;
        this.capacity = capacity;
    }

    public void setReverse(Edge e) {
        reverse = e;
    }
}