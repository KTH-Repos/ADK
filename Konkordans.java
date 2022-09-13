import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/*
 * ADK-2350-LABB 1
 * Max Israelsson (maxisr@kth.se)
 * Tomas Yonas Weldetinsae (tywe@kth.se)
 */
class Konkordans {

    // Filer (lokalt)
    private static final File FILE_RAWINDEX = new File("rawindex.txt");
    private static final File FILE_KORPUS = new File("korpus");
    private static final File FILE_A = new File("A.txt");
    private static final File FILE_I = new File("I.txt");
    private static final File FILE_L = new File("L.txt");

    // Basen som vi kommmer att använda till vår hash-funktion. A till ö motsvarar
    // 29 tecken och mellanslag som 1 (totalt 30).
    private static final int BASE = 30;

    // Svaret får innehålla högst 25 rader med förekomster.
    private static final int MAXLINES = 25;

    // Vår int-array A.
    private static int[] A = new int[BASE * BASE * BASE];

    // TODO : Varför använder vi short för datatyp???
    // Största ascii-värdet i svenska är 246, vilket motsvarar ö
    private static short[] c = new short[247];

    // Teckenkodningen som vi ska använda. Det betyder att varje tecken lagras i en
    // byte, vilket är praktiskt när man ska adressera sig till en viss position i
    // filen.
    private static final Charset ISO_LATIN_1 = StandardCharsets.ISO_8859_1;

    /**
     * Mainfunktionen:
     * Kör konstruktionsprogrammet först ifall den inte är körd och sedan
     * sökprogrammet.
     * 
     * @param args det angivna sökordet. Enbart ett ord accepteras.
     * @throws IOException om problem med filerna.
     */
    public static void main(String args[]) throws IOException {
        long startTime = System.nanoTime();

        // TODO: ÄNDRA NAMN OCH GE EN FÖRKLARING
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

            // Vi läser in RAWINDEX filen och skickar den till
            // createConstructionFiles-metoden.
            createConstructionFiles(Mio.OpenRead(FILE_RAWINDEX));
            long endTime = System.nanoTime();
            System.out.println("Skapade filer: A.txt, I.txt och L.txt");
            long totalTime = endTime - startTime;
            System.out.println("KONSTRUKTIONSPROGRAMMET - Körtid: " + totalTime / 1000000000 + " s.");
            startTime = System.nanoTime();
        }
        // Om filerna redan är skapade läser vi enbart från A.txt till internminnet.
        else {
            loadFromFileA();
        }

        // Vi söker efter ordet och kollar ifall det finns.
        if (!searchWord(wordToFind.toLowerCase())) {
            System.out.println("Ordet '" + wordToFind + "' hittades inte.");
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("SÖKPROGRAMMET - Körtid: " + totalTime / 1000000 + " ms.");
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

    // ###########################################################################################################
    // KONSTRUKTIONSPROGRAMMET
    // ###########################################################################################################
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
                                .getBytes(ISO_LATIN_1));

                        // (A Hashning) - Vi använder oss av vår hash-funktion. Vi kollar ifall det
                        // redan
                        // finns ett ord med samma hash. Vi använder vår A-array som är på internminnet.
                        int hash = wPrefix(previousWord);
                        if (A[hash] == 0) {
                            A[hash] = iBytePositon;
                        }

                        // TODO: Osäker vad den här gör helt. Vi kollar om vi kan ändra på den här sen.
                        // Jag tolkar det som att vi lägger ihop dessa så att de får en specifik
                        // placering och blir en pekare sen.
                        iBytePositon += previousWord.length() + getLengthInt(previousLBytePosition)
                                + getLengthInt(wordOccurs) + 3;

                        previousWord = currentWord;
                        previousLBytePosition = lBytePositon;
                        wordOccurs = 0;
                    }
                    // Vi ökar lBytePositon med index-längden plus ett.
                    lBytePositon += indexLength + 1;

                    // Vi ökar wordOccurs med ett ifall ordet förekommer igen.
                    wordOccurs++;

                    // (FIL L) - Vi skriver index-stringen och gör en newline efteråt med \n
                    // (newline),
                    file_l.write((index + "\n").getBytes(ISO_LATIN_1));
                }

                // Extra för att spara det sista ordet.
                // (FIL I)
                file_i.write((previousWord + " " + previousLBytePosition + " " + wordOccurs + "\n")
                        .getBytes(ISO_LATIN_1));

                // (A Hashning)
                int hash = wPrefix(previousWord);
                if (A[hash] == 0) {
                    A[hash] = iBytePositon;
                }
            }
        }
        // Skriv till fil A. (SPARAR)
        writeToFileA();
    }

    /**
     * wPrefix hashar de tre första bokstäverna från en sträng (word). Funkar även
     * på ord kortare än tre.
     * 
     * Vi använder använder oss utav en förberäknad funktion f som omvandlar dom
     * tänkbara tecknen som kommer in till siffror,
     * t.ex “mellanslag”→0, “a”→1, “b”→2, […], “ö”→29.
     * Sen så ska vi beräkna ett hashvärde för alla möjliga 3-bokstavskombinationer
     * och det gör vi med hjälp av funktionen:
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

                // Vi skriver till fil A iBytePositionen och gör ett mellanslag mellan varje
                // steg.
                file_a.write((iBytePosition + " ").getBytes(ISO_LATIN_1));
            }
        }
    }

    /**
     * Divide and Conquer
     * "This is perhaps the bulkiest approach when compared to all the others
     * described here; however, it's also the fastest because we're not performing
     * any type of conversion, multiplication, addition, or object initialization.
     * We can get our answer in just three or four simple if statements:"
     * - https://www.baeldung.com/java-number-of-digits-in-int
     * 
     * @param num det nummer vi ska ta fram längden till.
     * @return längden av num.
     */
    static int getLengthInt(int num) {
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

    // TODO: ÄNDRA NAMN OCH GE EN FÖRKLARING
    /**
     * Create a pre-calculated array for values to use with lazyHash method.
     * The letters have values 1-29, with space having value 0.
     * This also avoid repeated use of tolower() method on input.
     */
    static void createCharValueArray() {
        c[(short) 'Ä'] = c[(short) 'ä'] = 27;
        c[(short) 'Å'] = c[(short) 'å'] = 28;
        c[(short) 'Ö'] = c[(short) 'ö'] = 29;
        for (short i = 1; i <= 26; i++) {
            c['A' + i - 1] = c['a' + i - 1] = i;
        }
    }

    // ###########################################################################################################
    // SÖKPROGRAMMET
    // ###########################################################################################################
    /**
     * Läser av innehållet i filen: A.txt
     * 
     * @throws IOException om filen laddas fel.
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

    private static Boolean searchWord(String wordToFind) throws IOException {
        int hash = wPrefix(wordToFind);

        // 3-bytes
        int firstBytes = A[hash];
        int nextBytes;

        if (hash == 26999) {
            nextBytes = firstBytes;
        } else {
            for (int i = 1; (nextBytes = A[hash + i]) == 0; i++)
                ;
        }

        // Vi söker i I-filen
        int[] iByteArray = binarySearch(wordToFind, firstBytes, nextBytes);
        int firstBytesPosition = iByteArray[0];

        // Vi kollar om värdet av firstBytesPosition är lika med -1.
        // Ifall det stämmer hittades inget ord i binärsökningen.
        if (firstBytesPosition == -1) {
            return false;
        }

        int numberOfWordOccurrences = iByteArray[2];
        System.out.printf("Det finns %d förekomster av ordet.\n", numberOfWordOccurrences);
        return true;
    }

    /**
     * TODO: SKRIV OM HELA DEN HÄR
     * Binary search of word in I-file. Switches to linear search at lower distance
     *
     * @param searchWord to search in file
     * @param i          lower byte position of the first three letter words
     * @param j          upper byte position of the first three letter words
     * @return [byte position in P of search word, Byte position of next word,
     *         Occurrences]. -1 if word not found
     * @throws IOException from file handling
     */
    static int[] binarySearch(String searchWord, int i, int j) throws IOException {
        RandomAccessFile I = new RandomAccessFile(FILE_I, "r");
        BufferedReader bufI = new BufferedReader(
                new InputStreamReader(new FileInputStream(I.getFD()), ISO_LATIN_1));
        // pre-compile and reuse regex for performance
        Pattern p = Pattern.compile(" ");
        int[] retArray = new int[] { -1, -1, 0 };

        // Divide and conquer search
        while (j - i > 1000) {
            int mid = i + (j - i) / 2;
            I.seek(mid);
            mid += I.readLine().length() + 1; // To adjust if seek to middle of line
            String midWord = p.split(I.readLine())[0];
            if (midWord.compareTo(searchWord) < 0) {
                i = mid;
            } else {
                j = mid;
            }
        }
        I.seek(i);

        // Linear search
        while (i <= j) {
            String line = bufI.readLine();
            String[] lineInfo = p.split(line);
            String linearWord = lineInfo[0];
            if (linearWord.equals(searchWord)) {
                // Starting byte position of word in P
                retArray[0] = Integer.parseInt(lineInfo[1]);

                // Starting byte position of next word in P
                String lineCheck = bufI.readLine();
                if (lineCheck != null) { // last line check
                    retArray[1] = Integer.parseInt(p.split(lineCheck)[1]);
                }

                // Number of word occurences
                retArray[2] = Integer.parseInt(lineInfo[2]);

                return retArray;
            }
            i += line.length() + 1;
        }
        return retArray;
    }
}