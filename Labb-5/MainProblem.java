import java.util.ArrayList;

// Steg 1. javac MainProblem.java
// Steg 2. java MainProblem < ./Test.in > Test.out

public class MainProblem {
    Kattio io;

    // Listor för att spara informationen.
    ArrayList<ArrayList<Integer>> roles = new ArrayList<>();
    ArrayList<ArrayList<Integer>> scenes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> actors = new ArrayList<>();

    // Listor för skådis 1 och 2 som används i assignRoles.
    ArrayList<Integer> skadis1 = new ArrayList<>();
    ArrayList<Integer> skadis2 = new ArrayList<>();

    // Används för att skapa nya listor.
    ArrayList<Integer> listToAdd;

    int roller; // Antal roller
    int scener; // Antal scener
    int skadespelare; // Antal skådespelare

    public static void main(String[] args) {
        new MainProblem();
    }

    MainProblem() {
        io = new Kattio(System.in, System.out);
        // Vi läser in (start) indatan från filen med hjälp av Kattio.
        roller = io.getInt(); // Roller
        scener = io.getInt(); // Scener
        skadespelare = io.getInt(); // Skådespelare

        // Variabler för att underlätta skapandet av roller och scener.
        int num;

        // Vi skapar tomma listor för skådespelarna. De vi ska arrangera sen i
        // assignRoles.
        for (int i = 0; i < skadespelare; i++) {
            actors.add(new ArrayList<Integer>());
        }

        // Vi lägger till alla roller i listan roles.
        for (int i = 0; i < roller; i++) {
            num = io.getInt(); // Först kommer antalet roller.
            listToAdd = new ArrayList<>(); // Vi skapar en ny listan där vi lägger in alla roller.

            for (int j = 0; j < num; j++) {
                listToAdd.add(io.getInt());
            }

            roles.add(listToAdd);
        }

        // Vi lägger in alla scener i listan scenes.
        for (int i = 0; i < scener; i++) {
            num = io.getInt();
            listToAdd = new ArrayList<>();

            for (int j = 0; j < num; j++) {
                listToAdd.add(io.getInt());
            }

            scenes.add(listToAdd);
        }

        assignRoles();
        printResult();
    }

    /**
     * Vårt första mål är att hitta roller för skådis 1 och 2.
     * Vårt andra mål är att sätta alla resterande roller till skådisar (girigt)
     * Slutligen om det fortfarande finns tomma roller lägger vi till superactors.
     */
    void assignRoles() {
        // --- Vårt första mål är att hitta roller för skådis 1 och 2.

        // Vi skapar två listor för skådas 1 och 2 med alla roller de kan spela.
        for (int i = 0; i < roles.size(); i++) {
            // Ifall roll (i) innehåller 1.
            if (roles.get(i).contains(1)) {
                int index = i + 1; // Vi lägger till 1 till (i) för att indexeringen ska bli rätt.
                skadis1.add(index);
            }

            // Ifall roll (i) innehåller 2.
            if (roles.get(i).contains(2)) {
                int index = i + 1; // Vi lägger till 1 till (i) för att indexeringen ska bli rätt.
                skadis2.add(index);
            }
        }

        // System.out.println("Before:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());
        // System.out.println("roles: " + roles.toString());
        // System.out.println("scenes: " + scenes.toString());

        // Väljer vilken roll skådis 1 och 2 ska ta.
        for (int i = 0; i < skadis1.size(); i++) {

            for (int j = 0; j < skadis2.size(); j++) {

                if (isRolesValid(skadis1.get(i), skadis2.get(j))) { // Vi håller på tills vi hittar en en giltig
                                                                    // uppsättning av skådis 1 och 2.

                    // Vi lägger skadis1 och skadis2 roll till listan "actors" och rensar sedan
                    // skådisarna i rollerna.
                    int skadis1Index = skadis1.get(i) - 1;
                    int skadis2Index = skadis2.get(j) - 1;

                    actors.get(0).add(skadis1.get(i));
                    roles.get(skadis1Index).clear();
                    actors.get(1).add(skadis2.get(j));
                    roles.get(skadis2Index).clear();
                    break;
                }

            }
            // Vi avslutar när vi har hittat en roll för både skådis 1 och för skådis 2.
            if (actors.get(0).size() != 0 && actors.get(1).size() != 0)
                break;

        }
        // System.out.println("After1-skadis1&2:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());
        // System.out.println("roles: " + roles.toString());
        // System.out.println("scenes: " + scenes.toString());

        // --- Vårt andra mål är att sätta alla resterande roller till skådisar (girigt)

        int roll;
        int skadis;

        // Vi tilldelar de resterande skådisarna de resterande rollerna.
        for (int i = 0; i < roles.size(); i++) {
            if (!roles.get(i).isEmpty()) { // Om listan på position (i) inte är tom gör... (vi vet att skådis 1 och 2 är
                                           // tomma)
                roll = i + 1; // Vi lägger till 1 för att indexeringen ska bli rätt för rollen.
                for (int j = 0; j < roles.get(i).size(); j++) { // Gå igenom listan på position (i).
                    skadis = (Integer) roles.get(i).get(j); // Vi sätter skådisen till den i listan (i) på plats (j).
                    if (isActorValidInRoll(skadis, roll)) { // Om skådisen kan spela rollen
                        int index = skadis - 1; // -1 för att få rätt index
                        actors.get(index).add(roll); // vi lägger sedan in rollen på den indexen
                        roles.get(i).clear(); // vi rensar på positionen (i) i roles.
                        break;
                    }
                }
            }
        }
        // System.out.println("After2-greedy:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());
        // System.out.println("roles: " + roles.toString());
        // System.out.println("scenes: " + scenes.toString());

        // --- Slutligen om det fortfarande finns tomma roller lägger vi till
        // superactors.

        // Lägger till superactors (om det behövs)
        for (int i = 0; i < roles.size(); i++) {
            if (!roles.get(i).isEmpty()) { // Om det fortfarande finns en roll som inte är tom så lägger vi till en
                                           // superactor för att lösa problemet.
                listToAdd = new ArrayList<>();

                int index = i + 1;
                listToAdd.add(index);

                actors.add(listToAdd);
            }
        }
        // System.out.println("After3-superactors:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());
        // System.out.println("roles: " + roles.toString());
        // System.out.println("scenes: " + scenes.toString());

        // System.out.println("end-----------------------");
    }

    /**
     * Metod för att kontrollera om rollerna är giltiga.
     * 
     * 
     * @param roll1
     * @param roll2
     * @return Vi kollar alltså om roll 1
     *         och roll 2 är i samma scen -> false. Om dom inte är i samma scen ->
     *         true.
     */
    boolean isRolesValid(int roll1, int roll2) {
        // Vi går igenom alla scener.
        for (int i = 0; i < scenes.size(); i++) {
            // Här kollar vi om roll1 och roll2 är i samma scen -> false.
            if (scenes.get(i).contains(roll1) && scenes.get(i).contains(roll2))
                return false;

        }
        // Om roll1 och roll2 inte är i samma scen -> true.
        return true;
    }

    /**
     * Metod för att kolla om en skådis kan spela en roll utan att den bryter mot
     * någon regel.
     * 
     * 
     * @param skadis
     * @param roll
     * @return True om ingen regel bryts annars false.
     */
    boolean isActorValidInRoll(int skadis, int roll) {

        // Om det är skådis 1 eller 2 kollar vi om dessa kan spela med roll (roll).
        if (skadis == 1 || skadis == 2) {
            for (int i = 0; i < actors.get(0).size(); i++) {
                if (!isRolesValid((Integer) actors.get(0).get(i), roll))
                    return false;
            }
            for (int i = 0; i < actors.get(1).size(); i++) {
                if (!isRolesValid((Integer) actors.get(1).get(i), roll))
                    return false;
            }

        }
        // Om skådisen inte är 1 eller 2 kollar vi om den specificerade skådisen kan
        // spela med roll (roll).
        else {
            int index = skadis - 1;
            for (int i = 0; i < actors.get(index).size(); i++) {
                if (!isRolesValid((Integer) actors.get(index).get(i), roll))
                    return false;
            }
        }
        return true;
    }

    /*
     * Utdataformat:
     * Rad ett: antal skådespelare som fått roller.
     * En rad för varje skådespelare (som fått roller) med skådespelarens nummer,
     * antalet roller skådespelaren tilldelats samt numren på dessa roller.
     */
    void printResult() {

        // Rad ett: antal skådespelare som fått roller.
        // Vi går igenom actors lista och där den inte är tom ökar vi numActors med 1.
        int numActors = 0;
        for (int i = 0; i < actors.size(); i++) {
            if (actors.get(i).size() != 0)
                numActors++;
        }
        System.out.println(numActors);

        // En rad för varje skådarespelare (som fått roller) med skådespelarens nummer,
        // antalet roller skådespelaren tilldelats samt numren på dessa roller.
        int skadis;
        for (int i = 0; i < actors.size(); i++) {
            skadis = i + 1;
            if (actors.get(i).size() != 0) {
                // Skådespelarens nummer, antalet roller skådespelaren tilldelats.
                System.out.print(skadis + " " + actors.get(i).size() + " ");
                for (int j = 0; j < actors.get(i).size(); j++) {
                    // Numren på rollerna skådespelaren har tilldelats.
                    System.out.print(actors.get(i).get(j) + " ");
                }
                System.out.println();
            }
        }
    }
}