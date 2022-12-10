import java.util.ArrayList;

// Steg 1. javac MainProblem.java
// Steg 2. java MainProblem < ./Test.in > Test.out

public class MainProblem {
    Kattio io;

    ArrayList<ArrayList<Integer>> roles = new ArrayList<>();
    ArrayList<ArrayList<Integer>> scenes = new ArrayList<>();
    ArrayList<ArrayList<Integer>> actors = new ArrayList<>();

    int roller; // Antal roller
    int scener; // Antal scener
    int skadespelare; // Antal skådespelare

    public static void main(String[] args) {
        new MainProblem();
    }

    MainProblem() {
        readInput();
        assignRoles();
        printResult();
    }

    void readInput() {
        io = new Kattio(System.in, System.out);
        // Vi läser in (start) indatan från filen med hjälp av Kattio.
        roller = io.getInt(); // Roller
        scener = io.getInt(); // Scener
        skadespelare = io.getInt(); // Skådespelare

        // Variabler för att underlätta skapandet av roller och scener.
        ArrayList<Integer> listToAdd;
        int num;

        // Vi lägger till alla roller.
        for (int i = 0; i < roller; i++) {
            num = io.getInt();
            listToAdd = new ArrayList<>();
            for (int j = 0; j < num; j++) {
                listToAdd.add(io.getInt());
            }
            roles.add(listToAdd);
        }

        // Vi lägger in alla scener.
        for (int i = 0; i < scener; i++) {
            num = io.getInt();
            listToAdd = new ArrayList<>();
            for (int j = 0; j < num; j++) {
                listToAdd.add(io.getInt());
            }
            scenes.add(listToAdd);
        }

        // Vi skapar tomma listor för skådespelarna. De vi ska arrangera sen i
        // assignRoles.
        for (int i = 0; i < skadespelare; i++) {
            actors.add(new ArrayList<Integer>());
        }
    }

    /**
     * TODO: ÄNDRA
     * Finds valid roles for actor1 and actor2.
     * Then, assigns roles to remaining actors in a greedy manner.
     * If there are still unassigned roles, use super actors to assign these roles.
     */
    void assignRoles() {
        ArrayList<Integer> skadis1 = new ArrayList<>();
        ArrayList<Integer> skadis2 = new ArrayList<>();

        // Vi skapar två listor för skådas 1 och 2 med alla roller de kan spela.
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).contains(1)) {
                skadis1.add(i + 1);
            }
            if (roles.get(i).contains(2)) {
                skadis2.add(i + 1);
            }
        }

        // System.out.println("Before:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());

        // Väljer vilken roll skådis 1 och 2 bör ta.
        for (int i = 0; i < skadis1.size(); i++) {
            for (int j = 0; j < skadis2.size(); j++) {
                if (rolesValid(skadis1.get(i), skadis2.get(j))) {
                    // Vi lägger skadis1 och skadis2 roll till listan "actors" och rensar sedan.
                    actors.get(0).add(skadis1.get(i));
                    roles.get(skadis1.get(i) - 1).clear();
                    actors.get(1).add(skadis2.get(j));
                    roles.get(skadis2.get(j) - 1).clear();
                    break;
                }
            }
            // Vi avslutar när vi har hittad en roll för skådis 1 och en för skådis 2.
            if (actors.get(0).size() != 0) {
                break;
            }
        }
        // System.out.println("After1-skadis1&2:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());

        int role;
        int actor;

        // Vi lägger till alla resterande roller till skådisar (girig)
        for (int i = 0; i < roles.size(); i++) {
            if (!roles.get(i).isEmpty()) {
                role = i + 1;
                for (int j = 0; j < roles.get(i).size(); j++) {
                    actor = (Integer) roles.get(i).get(j);
                    if (actorValid(actor, role)) {
                        actors.get(actor - 1).add(role);
                        roles.get(i).clear();
                        break;
                    }
                }
            }
        }
        // System.out.println("After2-greedy:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());

        // Vi lägger till superactors (om det behövs)
        ArrayList<Integer> superActor;
        for (int i = 0; i < roles.size(); i++) {
            if (!roles.get(i).isEmpty()) {
                superActor = new ArrayList<>();
                superActor.add(i + 1);
                actors.add(superActor);
            }
        }
        // System.out.println("After3-superactors:--------");
        // System.out.println("skadis1: " + skadis1.toString());
        // System.out.println("skadis2: " + skadis2.toString());
        // System.out.println("actors: " + actors.toString());

        // System.out.println("end-----------------------");
    }

    /**
     * TODO: ÄNDRA
     * If r1 and r2 are in the same scene return false, otherwise return true.
     * 
     * @param r1
     * @param r2
     * @return
     */
    boolean rolesValid(int r1, int r2) {
        for (int i = 0; i < scenes.size(); i++) {
            if (scenes.get(i).contains(r1) && scenes.get(i).contains(r2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: ÄNDRA
     * If actor a can play role r without breaking any rules, return true, otherwise
     * false.
     * 
     * @param a
     * @param r
     * @return
     */
    boolean actorValid(int a, int r) {
        // if the actor is 1 or 2, check that all roles played by 1 and 2 isn't in same
        // scene as r
        if (a == 1 || a == 2) {
            for (int i = 0; i < actors.get(0).size(); i++) {
                if (!rolesValid((Integer) actors.get(0).get(i), r)) {
                    return false;
                }
            }
            for (int i = 0; i < actors.get(1).size(); i++) {
                if (!rolesValid((Integer) actors.get(1).get(i), r)) {
                    return false;
                }
            }

        }
        // else, check that roles played by a isn't in the same scene as r
        else {
            for (int i = 0; i < actors.get(a - 1).size(); i++) {
                if (!rolesValid((Integer) actors.get(a - 1).get(i), r)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * TODO: skriv om
     * Utdataformat:
     * Rad ett: antal skådespelare som fått roller
     * En rad för varje skådespelare (som fått roller) med skådespelarens nummer,
     * antalet roller skådespelaren tilldelats samt numren på dessa roller
     */
    void printResult() {
        int numberOfActors = 0;
        for (int i = 0; i < actors.size(); i++) {
            if (actors.get(i).size() != 0) {
                numberOfActors++;
            }
        }
        System.out.println(numberOfActors);

        int actor;
        for (int i = 0; i < actors.size(); i++) {
            actor = i + 1;
            if (actors.get(i).size() != 0) {
                System.out.print(actor + " " + actors.get(i).size() + " ");
                for (int j = 0; j < actors.get(i).size(); j++) {
                    System.out.print(actors.get(i).get(j) + " ");
                }
                System.out.println();
            }
        }
    }
}
