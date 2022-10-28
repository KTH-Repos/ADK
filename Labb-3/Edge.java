class Edge {

    int u, v, flow, capacity;
    Edge reverse;

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