import java.util.ArrayList;
import java.util.List;

/**
 * Exempel på in- och utdatahantering för maxflödeslabben i kursen
 * ADK.
 *
 * Använder Kattio.java för in- och utläsning.
 * Se http://kattis.csc.kth.se/doc/javaio
 *
 * @author: Per Austrin
 */

public class BipRed {
	Kattio io;
	Node[] graph;

	void readBipartiteGraph() {
		// Läs antal hörn och kanter
		int x = io.getInt();
		int y = io.getInt();
		int e = io.getInt();

		// Skapa noder och lagra dem i en lista. Inte viktigt!!
		int nodes = x + y;
		graph = new Node[nodes];
		for (int i = 1; i < nodes; i++) {
			// graph[i] = new Node(i);
		}

		List<Node> Xnodes = new ArrayList<>();
		List<Node> Ynodes = new ArrayList<>();

		// Läs in kanterna
		Node[][] edges = new Node[e][2];
		for (int i = 0; i < e; ++i) {
			int a = io.getInt();
			int b = io.getInt();

			// edges[i][0] = new Node(a);
			Xnodes.add(edges[i][0]);
			// edges[i][1] = new Node(b);
			Ynodes.add(edges[i][1]);
		}
		// TODO: Lägg till källa och sänka genom att skapa två nya noder
		// writeFlowGraph(nodes+2, e, s, t);
	}

	void writeFlowGraph(int v, int e, int s, int t) {
		// int v = 23, e = 0, s = 1, t = 2;

		// Skriv ut antal hörn och kanter samt källa och sänka
		io.println(v);
		io.println(s + " " + t);
		io.println(e);
		for (int i = 0; i < e; ++i) {
			int a = 1, b = 2, c = 17;
			// Kant från a till b med kapacitet c
			io.println(a + " " + b + " " + c);
		}
		// Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
		io.flush();

		// Debugutskrift
		System.err.println("Skickade iväg flödesgrafen");
	}

	void readMaxFlowSolution() {
		// Läs in antal hörn, kanter, källa, sänka, och totalt flöde
		// (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
		// skickade iväg)
		int v = io.getInt();
		int s = io.getInt();
		int t = io.getInt();
		int totflow = io.getInt();
		int e = io.getInt();

		for (int i = 0; i < e; ++i) {
			// Flöde f från a till b
			int a = io.getInt();
			int b = io.getInt();
			int f = io.getInt();
		}
	}

	void writeBipMatchSolution() {
		int x = 17, y = 4711, maxMatch = 0;

		// Skriv ut antal hörn och storleken på matchningen
		io.println(x + " " + y);
		io.println(maxMatch);

		for (int i = 0; i < maxMatch; ++i) {
			int a = 5, b = 2323;
			// Kant mellan a och b ingår i vår matchningslösning
			io.println(a + " " + b);
		}

	}

	BipRed() {
		io = new Kattio(System.in, System.out);

		readBipartiteGraph();
		// writeFlowGraph();

		readMaxFlowSolution();

		writeBipMatchSolution();

		// debugutskrift
		System.err.println("Bipred avslutar\n");

		// Kom ihåg att stänga ner Kattio-klassen
		io.close();
	}

	public static void main(String args[]) {
		BipRed bipRed = new BipRed();
	}
}
