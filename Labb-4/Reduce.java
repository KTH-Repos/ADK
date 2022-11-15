import java.util.HashMap;

// Steg 1. javac Reduce.java
// Steg 2. java Reduce < ./testfall/in.txt > out.txt
// Steg 3. /afs/kth.se/misc/info/kurser/DD2350/adk22/labb4/verifyLab4 < out.txt

public class Reduce {
    Kattio io;

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

        // Denna fick vi från teorilabben uppgift 4.
        // 4) Vilken är den minsta möjliga produktion som uppfyller indatakraven för
        // rollbesättningsproblemet och som går att sätta upp? Skriv upp indata för
        // denna produktion!
        final int BASECASE_ROLLER = 3;
        final int BASECASE_SCENER = 2;
        final int BASECASE_SKADESPELARE = 3;
        final int MAX_KANTER = 25000 * 2; // Vi multiplcererar med två då vi sparar i första positionen rollen och andra
                                          // vilken skådespelare. EX. 0 -> 1 (roll) 1 -> 1 (skådis)

        int V;
        int E;
        int m;

        V = io.getInt(); // Antal roller -> antal hörn (nodes)
        E = io.getInt(); // Antal scener -> antal kanter
        m = io.getInt(); // Antal skådespelare -> antal färger

        // TODO: Skriv om
        // add vertices that are not isolated -> roles
        // store edges in edges array
        // O(e)

        // Vi går igenom alla roller som tilldelar skådespelare. Vi lägger till dom i en
        // HashMap och i en array för alla kanter.
        // Tidskomplexitet: O(E)
        HashMap<Integer, Integer> roller = new HashMap<>();
        int kanter[] = new int[MAX_KANTER];
        int roll;
        int skadis;
        int index = 0;

        for (int i = 0; i < E; i++) { // Lydelsen: Varje roll förekommer högst en gång på varje sådan rad, så antalet
                                      // roller på en rad ligger mellan 2 och n.
            roll = io.getInt();
            skadis = io.getInt();
            roller.put(roll, roll);
            roller.put(skadis, skadis);

            kanter[index] = roll;
            kanter[index + 1] = skadis;
            index += 2; // Vi ökar med två då vi använde två positioner.
        }

        // TODO: Skriv om
        // do not keep isolated vertices
        // adjust the values in map due to isolated vertices
        // O(v^2)

        // Vi tar bort alla hörn som är isolerade och ändrar värderna i vår HashMap.
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

        // Vi skapar nya variabler för det vi ska skriva ut.
        int rollerOut = V + BASECASE_ROLLER;
        int scenerOut = E + BASECASE_SCENER;
        int skadespelareOut;

        // 1 <= m <= 1 073 741 824 and 1 <= v <= 300
        // next loop will be O(v*m)
        // but m >= v -> always yes instance. So if m > v, we can limit m to m = v.
        // this will mean that at worst case loop runs for O(v^2)
        if (m > V) {
            m = V;
        }
        skadespelareOut = m + BASECASE_SKADESPELARE;

        StringBuilder sb = new StringBuilder();

        // ----# Vi skriver först ut antal roller, scener och skådespelare. #----
        sb.append(rollerOut).append("\n").append(scenerOut).append("\n").append(skadespelareOut).append("\n");

        // ----# Vi skriver sedan ut samtliga roller och vilka skådespelare som kan
        // spela vilken roll. #----
        sb.append("1 1\n");
        sb.append("1 2\n");
        sb.append("1 3\n");

        // O(V^2)
        for (int i = 0; i < V; i++) {
            sb.append(m + " ");
            for (int j = 3; j < skadespelareOut; j++) {
                sb.append(j + " ");
            }
            sb.append("\n");
        }

        // ----# Slutligen skriver vi ut scenerna med vilka roller som tillhör. #----
        sb.append("2 1 3\n");
        sb.append("2 2 3\n");

        // O(e)
        int i = 0;
        while (kanter[i] != 0) {
            sb.append("2").append(" ").append(roller.get(kanter[i]) + BASECASE_ROLLER).append(" ")
                    .append(roller.get(kanter[i + 1]) + BASECASE_ROLLER)
                    .append("\n");
            i += 2;
        }

        // Vi skriver ut det vi har sparat i vår StringBuilder med Kattio.
        io.print(sb);

        io.flush();
        io.close();
    }
}
