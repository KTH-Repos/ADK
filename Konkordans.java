import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class Konkordans {

    // Filer
    private static final File FILE_RAWINDEX = new File("rawindex.txt");
    private static final File FILE_KORPUS = new File("korpus");
    private static final File FILE_A = new File("A.txt");
    private static final File FILE_I = new File("I.txt");
    private static final File FILE_L = new File("L.txt");

    // Basen som vi kommmer att använda till vår hash-funktion. A till ö motsvarar
    // 29 tecken och mellanslag som 1 (totalt 30).
    private static final int BASE = 30;

    // Vår int-array A.
    private static int[] A = new int[BASE * BASE * BASE];

    // TODO : Varför använder vi short för datatyp???
    // Största ascii-värdet i svenska är 246, vilket motsvarar ö
    private static short[] c = new short[247];

    // Teckenkodningen som vi ska använda. Det betyder att varje tecken lagras i en
    // byte, vilket är praktiskt när man ska adressera sig till en viss position i
    // filen.
    private static Charset ISO_Latin_1 = StandardCharsets.ISO_8859_1;

    /**
     * Mainfunktionen:
     * Kör konstruktionsprogrammet först ifall den inte är körd och sedan sökprogrammet.
     * 
     * @param args det angivna sökordet. Enbart ett ord accepteras.
     * @throws IOException om problem med filerna.
     */
    public static void main(String args[]) throws IOException {
        //TODO: ÄNDRA NAMN OCH GE EN FÖRKLARING
        createCharValueArray();

        // Vi kollar ifall längden ifall längden är fel (alltså inte ett).
        if (args.length != 1) {
            System.out.println("Fel indata: java Konkordans <sökord>");

            // Vi tilldelar 1 då ett fel inträffade.
            System.exit(1); 
        }

        // Vi sparar vilket ordet som ska sökas och hittas.
        String wordToFind = args[0];

        // Vi kollar ifall filerna finns. Om de inte gör det skapar vi dom i
        // createConstructionFiles-metoden.
        if (!checkFilesExist()) {

            // Vi läser in RAWINDEX filen och skickar den till createConstructionFiles-metoden.
            createConstructionFiles(Mio.OpenRead(FILE_RAWINDEX));
            System.out.println("Skapade filer: A.txt, I.txt och L.txt");
        }
        // Om filerna redan är skapade läser vi enbart från A.txt till internminnet.
        else {
            loadFromFileA();
        }

        System.out.println("Program slut.");
    }

    /**
     * checkFilesExist kollar ifall fil A.txt, fil I.txt och fil L.txt finns.
     * 
     * @return sant eller falskt
     */
    private static boolean checkFilesExist() {
        if (FILE_A.exists() && FILE_I.exists() && FILE_L.exists()) {
            return true;
        }
        return false;
    }
//###########################################################################################################    
//                  KONSTRUKTIONSPROGRAMMET    
//###########################################################################################################    
    /**
     * Skapar A, I, L filarna som behövs för att kunna söka snabbt sen.
     * TODO: SKRIV MER HÄR.
     * 
     * @param raw filen med innehållet som ska läsas. 
     * @throws IOException om problem med filerna.
     */
    private static void createConstructionFiles(BufferedInputStream raw) throws IOException {
        String currentWord = "";
        String previousWord = "a"; // TODO: TESTA ATT ÄNDRA SEN FRÅN "a".

        String index;
        int indexLength = 0;

        int wordOccurs = 0;

        // TODO: Våra pekare för att kunna hitta i de olika filerna.
        int iBytePositon = 0;
        int lBytePositon = 0;
        int previousLBytePosition = 0;

        // Vi försöker att läsa in fil I. (OUTPUT)
        try (BufferedOutputStream file_i = new BufferedOutputStream(new FileOutputStream(FILE_I))) {

            // Vi försöker att läsa in fil L. (OUTPUT)
            try (BufferedOutputStream file_l = new BufferedOutputStream(new FileOutputStream(FILE_L))) {

                // Mio.EOF returnerar true om filen är slutläst.
                // while (!Mio.EOF(raw)) { TODO: KOLLA OM DENNA FUNGERAR ISTÄLLET.
                while (!(currentWord = Mio.GetWord(raw)).equals("")) {

                    // Mio.GetWord läser ett ord avgränsat av blanka från tangenterna och returnera
                    // det. Vi sparar detta ord i en string index.
                    index = Mio.GetWord(raw);

                    // Vi sparar längden av indexen i int indexLength.
                    indexLength = index.length();

                    // Ifall ordet är annorlunda så sparar vi och ändrar ord.
                    if (!currentWord.equals(previousWord)) {

                        // (FIL I) - Vi skriver och sparar data i fil I. Vi tänker på teckenkodning.
                        file_i.write((previousWord + " " + previousLBytePosition + " " + wordOccurs + "\n")
                                .getBytes(ISO_Latin_1));

                        // (A Hashning) - Vi använder oss av vår hash-funktion. Vi kollar ifall det redan
                        // finns ett ord med samma hash. Vi använder vår A-array som är på internminnet.
                        int hash = wPrefix(previousWord);
                        if (A[hash] == 0) {
                            A[hash] = iBytePositon;
                        }

                        // TODO: Osäker vad den här gör helt. Vi kollar om vi kan ändra på den här sen.
                        // Jag tolkar det som att vi lägger ihop dessa så att de får en specifik placering och blir en pekare sen.
                        iBytePositon += previousWord.length() + getLengthOfInt(previousLBytePosition) + getLengthOfInt(wordOccurs) + 3;

                        previousWord = currentWord;
                        previousLBytePosition = lBytePositon;
                        wordOccurs = 0;
                    }
                    // Vi ökar lBytePositon med index-längden plus ett.
                    lBytePositon += indexLength + 1;

                    //Vi ökar wordOccurs med ett. 
                    wordOccurs++;

                    // (FIL L) - Vi skriver index stringen och gör en newline efteråt med \n (newline),
                    file_l.write((index + "\n").getBytes(ISO_Latin_1));
                }

                // Extra för att spara det sista ordet.
                // (FIL I)
                file_i.write((previousWord + " " + previousLBytePosition + " " + wordOccurs + "\n")
                        .getBytes(ISO_Latin_1));

                // (A Hashning)
                int hash = wPrefix(previousWord);
                if (A[hash] == 0) {
                    A[hash] = iBytePositon;
                }
            }
        }
        // Skriv till fil A. (sparar)
        writeToFileA();
    }
    
    /**
     * wPrefix hashar de tre första bokstäverna från en sträng (word). Funkar även på ord kortare än tre.
     * 
     * Vi använder använder oss utav en förberäknad funktion f som omvandlar dom tänkbara tecknen som kommer in till siffror,
     * t.ex “mellanslag”→0, “a”→1, “b”→2, […], “ö”→29.
     * Sen så ska vi beräkna ett hashvärde för alla möjliga 3-bokstavskombinationer och det gör vi med hjälp av funktionen:
     * h(w) = f(w[0])*900 + f(w[1])*30 + f(w[2])
     * 
     * @param word ett ord.
     * @return det hashade värdet från hash-funktionen.
     */
    private static int wPrefix(String word) {
        // Kollar om längden på ordet är mindre än tre.
        if (word.length() < 3) {

            // Om ordet har en längd av ett.
            if (word.length() == 1) {
                word = "  " + word;

            // Om ordet har en längd av två.
            } else if (word.length() == 2) {
                word = " " + word;
            }
        }
        // Motsvarar: h(w) = f(w[0])*900 + f(w[1])*30 + f(w[2])
        return c[word.charAt(0)] * (BASE * BASE) + c[word.charAt(1)] * BASE + c[word.charAt(2)];
    }   

    /**
     * Skriver innehållet av array A till filen: A.txt
     * 
     * @throws IOException om I/O fel.
     */
    private static void writeToFileA() throws IOException {
        // (Skriver till FIL A)
        // Vi försöker att läsa in fil A. (OUTPUT)
        try (BufferedOutputStream file_a = new BufferedOutputStream(new FileOutputStream(FILE_A))) {

            // (for-each loop) För varje iBytePosition i arrayen:
            for (int iBytePosition : A) {

                // Vi skriver till fil A iBytePositionen och gör ett mellanslag mellan varje steg.
                file_a.write((iBytePosition + " ").getBytes(ISO_Latin_1));
            }
        }
    }

    /** TODO: TA BORT
     * Divide and conquer for fast length of int number
     *
     * @param num to check for length
     * @return length of num
     */
    static int getLengthOfInt(int num) {
        if (num < 100000) {
            if (num < 100) {
                if (num < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                if (num < 1000) {
                    return 3;
                } else {
                    if (num < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            if (num < 10000000) {
                if (num < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                if (num < 100000000) {
                    return 8;
                } else {
                    if (num < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
                }
            }
        }
    }

    //TODO: ÄNDRA NAMN OCH GE EN FÖRKLARING
    /**
     * Create a pre-calculated array for values to use with lazyHash method.
     * The letters have values 1-29, with space having value 0.
     * This also avoid repeated use of tolower() method on input.
     */
    static void createCharValueArray() {
        c = new short[247]; // highest ascii-value in swedish alphabet is ö (246)
        c[(short) ' '] = 0; // unnecessary due to 0-initialization and no collision, but here for completion
        c[(short) 'Ä'] = c[(short) 'ä'] = 27;
        c[(short) 'Å'] = c[(short) 'å'] = 28;
        c[(short) 'Ö'] = c[(short) 'ö'] = 29;
        for (short i = 1; i <= 26; i++) {
            c['A'+i-1] = c['a'+i-1] = i;
        }
    }

//###########################################################################################################    
//                  SÖKPROGRAMMET    
//###########################################################################################################
    /**
     * Läser av innehållet i filen: A.txt
     * 
     * @throws IOException OM I/O fel.
     */
    private static void loadFromFileA() throws IOException {
        // (Läser från FIL A)
        // Vi försöker att läsa in fil A. (INPUT)
        try (BufferedInputStream file_a = new BufferedInputStream(new FileInputStream(FILE_A))) {

            // Vi vet redan hur många läsningar vi kommer att behöva göra.
            for (int i = 0; i < A.length; i++) {
                A[i] = Mio.GetInt(file_a);
            }
        }
    }
}