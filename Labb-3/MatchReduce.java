/**
 * Steg 1 av 3
 * Kommando:
 * /afs/kth.se/misc/info/kurser/DD2350/adk22/labb3/combine java MatchReduce \;
 * /afs/kth.se/misc/info/kurser/DD2350/adk22/labb3/maxflow < graffil > matchfil
 * 
 */

public class MatchReduce {
    Kattio io;

    public static void main(String args[]) {
        new MatchReduce();
    }

    MatchReduce() {
        io = new Kattio(System.in, System.out);

        readBipartiteGraph();

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
        // TODO: Lägg till källa och sänka genom att skapa två nya noder
        writeFlowGraph(x, y, e, edges);
    }

    void writeFlowGraph(int x, int y, int e, int[][] edges) {
        int vertexs = x + y + 2;
        int source = 1;
        int sanka = vertexs;
        int numEdges = x + y + e;

        // Skriv ut antal hörn och kanter samt källa och sänka
        io.println(vertexs);
        io.println(source + " " + sanka);
        io.println(numEdges);

        // Kant a och b med kapacitet c.
        int a = 1;
        int b;
        int c = 1;

        // Första loopen ger oss kanterna från källan till Xnoderna.
        for (int i = 2; i <= x + 1; i++) {
            b = i;
            // Kant från a till b med kapacitet c
            io.println(a + " " + b + " " + c);
        }

        // Denna fick vi från BigRed(skelettkoden).
        // Andra loopen ger oss kanterna från Xnoderna till Ynoderna.
        for (int i = 0; i < e; ++i) {
            a = edges[i][0];
            b = edges[i][1];
            // Kant från a till b med kapacitet c
            io.println(a + " " + b + " " + c);
        }

        // Sista loopen ger oss kanterna från Ynoderna till sänkan/utloppet.
        b = sanka;
        for (int i = x + 2; i < b; i++) {
            a = i;
            // Kant från a till b med kapacitet c
            io.println(a + " " + b + " " + c);
        }

        // Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
        io.flush();

        // Debugutskrift
        // System.err.println("Skickade iväg flödesgrafen");

        readMaxFlowSolution(x, y);
    }

    void readMaxFlowSolution(int x, int y) {
        // Läs in antal hörn, kanter, källa, sänka, och totalt flöde
        // (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
        // skickade iväg)
        int vertexs = io.getInt();
        int source = io.getInt();
        int sanka = io.getInt();
        int totflow = io.getInt();
        int e = io.getInt();

        int a;
        int b;
        int flow;

        // Skriver ut antal Xnoder och Ynoder.
        io.println(x + " " + y);
        // Skriver ut det totala flödet.
        io.println(totflow);

        for (int i = 0; i < e; ++i) {
            // Flöde f från a till b
            a = io.getInt();
            b = io.getInt();
            flow = io.getInt();

            if (b != sanka && a != source) {
                io.println((a - 1) + " " + (b - 1));
            }
        }
        io.flush();
    }
}