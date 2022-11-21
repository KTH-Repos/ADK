// Steg 1. javac ReduceTwo.java
// Steg 2. java ReduceTwo < ./testfall/in.txt > out.txt
// Steg 3. /afs/kth.se/misc/info/kurser/DD2350/adk22/labb4/verifyLab4 < out.txt

// Varför är rollbesättningsproblemet NP-svårt?
// Svar: För inputen till rollbesättningsproblemet kan reduceras från inputen till
// graffärgningsproblemet, vilket är ett känt NP-fullständigt problem. Reduktionen 
// genomförs på polynomisk tid.  

// Vad komplexiteten är för din reduktion?
// Svar: O(E) + O(V^2) + O(V) + O(E) --> Polynomisk tid? JA!

public class ReduceTwo {
    Kattio io;

    // Dessa kommer från teorilabben uppgift 4: ta fram den minsta möjliga
    // produktionen.
    // Den minsta möjliga produktionen ska ha 3 roller, 2 scener och 3
    // skådisar för att p1 och p2
    // inte ska vara i samma scener. Då ser vi till att detta problem är redan löst
    // i basfallet.
    final int BASECASE_ROLLER = 3;
    final int BASECASE_SCENER = 2;
    final int BASECASE_SKADESPELARE = 3;

    int kanter[][] = new int[25000][2];

    int V;
    int E;
    int m;

    public static void main(String[] args) {
        new ReduceTwo();
    }

    ReduceTwo() {
        io = new Kattio(System.in, System.out);

        // Vi läser in indatan från filen med hjälp av Kattio.
        V = io.getInt();
        E = io.getInt();
        m = io.getInt();

        // Vi skapar tre variabler: a,b.
        int a, b;

        // Vi går igenom alla kanter
        // --Tidskomplexitet: O(E)
        for (int i = 0; i < E; i++) {
            a = io.getInt();
            b = io.getInt();
            kanter[i][0] = a;
            kanter[i][1] = b;
        }

        int rollerOut = V + BASECASE_ROLLER;

        // För att rätta till när vi kopplar nod 1 med alla noder från nod 4 och uppåt.
        int scenerOut = E + BASECASE_SCENER + rollerOut - 4 + 1;
        int skadespelareOut;

        // Enligt labblydelsen låg m mellan 1 <= m <= 2^30 och v mellan 1 <= V <= 300
        // m >= V kommer alltid att vara en ja-instans. Ifall m skulle vara större än V
        // så kan vi ansätta m till V. (m = V om m > V) Vi får då: O(V^2) på nästa loop.
        if (m > V) {
            m = V;
        }
        skadespelareOut = m + BASECASE_SKADESPELARE;

        StringBuilder sb = new StringBuilder();

        // ----# Vi skriver ut roller, scener och skådespelare först #----

        sb.append(rollerOut).append("\n").append(scenerOut).append("\n").append(skadespelareOut).append("\n");

        // ----# Vi skriver sedan ut samtliga roller och vilka skådespelare som kan
        // spela vilken roll. #----

        sb.append("1 1\n");
        sb.append("1 2\n");
        sb.append("1 3\n");

        // Alla roller som kan tas av alla skådespelare.
        // --Tidskomplexitet: O(V^2) ty skadespelareOut = m = V.
        for (int i = 0; i < V; i++) {
            sb.append(m + " ");
            for (int j = BASECASE_ROLLER; j < skadespelareOut; j++) {
                sb.append(j + " ");
            }
            sb.append("\n");
        }

        // ----# Slutligen skriver vi ut scenerna med vilka roller som tillhör. #----
        sb.append("2 1 3\n");
        sb.append("2 2 3\n");

        // Koppla nod 1 med nod 4 och uppåt. (alla över 3)
        // --Tidskomplexitet: O(V)
        for (int i = 4; i <= rollerOut; i++) {
            sb.append("2 1 " + i).append("\n");
        }

        // Vi går igenom alla kanter och skriver ut alla scener med vilka roller som
        // tillhör.
        // --Tidskomplexitet: O(E)
        for (int j = 0; j < E; j++) {
            int rollOne = kanter[j][0] + BASECASE_ROLLER;
            int rollTwo = kanter[j][1] + BASECASE_ROLLER;
            sb.append("2 ").append(rollOne).append(" ").append(rollTwo).append("\n");
        }

        io.print(sb);
        io.flush();
        io.close();

    }
}