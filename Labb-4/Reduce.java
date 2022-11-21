import java.util.HashMap;

// Steg 1. javac Reduce.java
// Steg 2. java Reduce < ./testfall/in.txt > out.txt
// Steg 3. /afs/kth.se/misc/info/kurser/DD2350/adk22/labb4/verifyLab4 < out.txt

// Varför är rollbesättningsproblemet NP-svårt?
// Svar: För inputen till rollbesättningsproblemet kan reduceras från inputen till
// graffärgningsproblemet, vilket är ett känt NP-fullständigt problem. Reduktionen 
// genomförs på polynomisk tid.  

// Vad komplexiteten är för din reduktion?
// Svar: O(E + V^2 + V^2 + E) = O(V^2 + E) --> Polynomisk tid? JA!

public class Reduce {
    Kattio io;

    // Denna fick vi från teorilabben uppgift 4.
    // 4) Vilken är den minsta möjliga produktion som uppfyller indatakraven för
    // rollbesättningsproblemet och som går att sätta upp? Skriv upp indata för
    // denna produktion!
    // Svar: Den minsta möjliga produktionen ska ha 3 roller, 2 scener och 3
    // skådisar för att p1 och p2
    // inte ska vara i samma scener. Då ser vi till att detta problem är redan löst
    // i basfallet.
    final int BASECASE_ROLLER = 3;
    final int BASECASE_SCENER = 2;
    final int BASECASE_SKADESPELARE = 3;
    final int MAX_KANTER = 25000 * 2; // Vi multiplcererar med två då vi sparar i första positionen rollen och andra
                                      // vilken skådespelare. EX. 0 -> 1 (roll) 1 -> 1 (skådis)

    // Vi skapar variabler för V, E och m.
    int V;
    int E;
    int m;

    HashMap<Integer, Integer> roller = new HashMap<>();
    int kanter[] = new int[MAX_KANTER];

    public static void main(String[] args) {
        new Reduce();
    }

    Reduce() {
        io = new Kattio(System.in, System.out);

        /*
         * Graffärning
         * Indata: En oriktad graf och ett antal färger m. Isolerade hörn och
         * dubbelkanter kan förekomma, inte öglor.
         * Fråga: Kan hörnen i grafen färgas med högst m färger så att inga grannar har
         * samma färg?
         * 
         * Indataformat:
         * Rad ett: tal V (antal hörn, 1 <= V <= 300)
         * Rad två: tal E (antal kanter, 0 <= E <= 25000)
         * Rad tre: mål m (max antal färger, 1 <= m 2^{30}
         * En rad för varje kant (E stycken) med kantens ändpunkter (hörnen numreras
         * från 1 till V)
         */

        // Vi läser in dessa från filen.
        V = io.getInt(); // Antal roller -> antal hörn (nodes)
        E = io.getInt(); // Antal scener -> antal kanter
        m = io.getInt(); // Antal skådespelare -> antal färger

        // Vi skapar grafen.
        createGraph();

        // Vi skriver ut resultatet.
        writeOutput();

        io.flush();
        io.close();
    }

    void createGraph() {
        // Vi läser in grafen.
        int a, b, index = 0;

        // Vi går igenom alla roller som tilldelar skådespelare. Vi lägger till dom i en
        // HashMap och i en array för alla kanter. Vi använder en HashMap för att enkelt
        // kunna ta fram och ändra.
        // Tidskomplexitet: O(E)
        for (int i = 0; i < E; i++) { // Lydelsen: Varje roll förekommer högst en gång på varje sådan rad, så antalet
                                      // roller på en rad ligger mellan 2 och n.
            a = io.getInt();
            b = io.getInt();
            roller.put(a, a);
            roller.put(b, b);
            kanter[index] = a;
            kanter[index + 1] = b;
            index += 2; // Vi ökar med två då vi använde två positioner.
        }

        // Det kan förekomma isolerade hörn i inputen till graffärgningsproblemet
        // Vi ska inte ha isolerade hörn för de representerar roller som inte ingår i
        // någon scen.
        // Vi vill spara hörn som är isolerade genom att ändrar värderna i vår
        // HashMap som vi skapade tidigare.
        // Tidskomplexitet: O(V^2)
        int isolated = 0;
        for (int i = 1; i <= V; i++) {
            if (!roller.containsKey(i)) { // Vi hittade ett hörn som är isolerande.
                isolated++;
                for (int j = i; j <= V; j++) {
                    if (roller.containsKey(j)) {
                        roller.put(j, roller.get(j) - 1);
                    }
                }
            }
        }
        V -= isolated;
    }

    void writeOutput() {
        // Vi skapar nya variabler för det vi ska skriva ut.
        int rollerOut = V + BASECASE_ROLLER;
        int scenerOut = E + BASECASE_SCENER;
        int skadespelareOut;

        // Enligt labblydelsen låg m mellan 1 <= m <= 2^30 och v mellan 1 <= V <= 300
        // m >= V kommer alltid att vara en ja-instans. Ifall m skulle vara större än V
        // så kan vi ansätta m till V. (m = V om m > V) Vi får då: O(V^2) på nästa loop.
        if (m > V) {
            m = V;
        }
        skadespelareOut = m + BASECASE_SKADESPELARE;

        // Vi skapar en StringBuilder sb.
        StringBuilder sb = new StringBuilder();

        // ----# Vi skriver först ut antal roller, scener och skådespelare. #----
        sb.append(rollerOut).append("\n").append(scenerOut).append("\n").append(skadespelareOut).append("\n");

        // ----# Vi skriver sedan ut samtliga roller och vilka skådespelare som kan
        // spela vilken roll. #----
        sb.append("1 1\n");
        sb.append("1 2\n");
        sb.append("1 3\n");

        // Alla roller som kan tas av alla skådespelare.
        // Tidskomplexitet: O(V^2) ty skadespelareOut = m = V.
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

        // Vi skriver ut alla resterande scener och de roller som används.
        // Tidskomplexitet: O(E)
        int i = 0;
        while (kanter[i] != 0) {
            int rollOne = roller.get(kanter[i]) + BASECASE_ROLLER;
            int rollTwo = roller.get(kanter[i + 1]) + BASECASE_ROLLER;
            sb.append("2 ").append(rollOne).append(" ")
                    .append(rollTwo)
                    .append("\n");
            i += 2;
        }

        // Skriver ut det vi har sparat i StringBuilder:n.
        io.print(sb);
    }
}
