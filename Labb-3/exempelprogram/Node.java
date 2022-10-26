import java.util.ArrayList;
import java.util.List;

public class Node {
    int index;
    List<Edge> edges;

    public Node(int index) {
        this.index = index;
        edges = new ArrayList<>();
    }
}