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
    private static final short[] C = new short[247];

    // Teckenkodningen som vi ska använda. Det betyder att varje tecken lagras i en
    // byte, vilket är praktiskt när man ska adressera sig till en viss position i
    // filen.
    private static Charset ISO_Latin_1 = StandardCharsets.ISO_8859_1;

    /**
     * Main-funktionen
     * 
     * @param args det angivna sökordet. Enbart ett ord accepteras.
     */
    public static void main(String args[]) throws IOException {
        // Vi kollar ifall längden ifall längden är fel (alltså inte ett).
        if (args.length != 1) {
            System.out.println("Fel indata: java Konkordans <sökord>");
            System.exit(1); // Vi tilldelar 1 då ett fel inträffade.
        }

        // Vi sparar vilket ordet som ska sökas och hittas.
        String wordToFind = args[0];

        // Vi kollar ifall filerna finns. Om de inte gör det skapar vi dom i
        // createConstructionFiles-metoden.
        if (!checkFilesExist()) {
            createConstructionFiles(Mio.OpenRead(FILE_RAWINDEX));
            System.out.println("Skapade constructionFiles.");
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

    // Hashar första tre bokstäver av input strängen.
    private static int wPrefix(String word) {
        if (word.length() < 3) {
            if (word.length() == 1) {
                word = "  " + word;
            } else if (word.length() == 2) {
                word = " " + word;
            }
        }
        return C[word.charAt(0)] * (BASE * BASE) + C[word.charAt(1)] * BASE + C[word.charAt(2)];
    }

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

        // Vi försöker att läsa in fil I.
        try (BufferedOutputStream file_i = new BufferedOutputStream(new FileOutputStream(FILE_I))) {

            // Vi försöker att läsa in fil L.
            try (BufferedOutputStream file_l = new BufferedOutputStream(new FileOutputStream(FILE_L))) {

                // Mio.EOF returnerar true om filen är slutläst.
                // while (!Mio.EOF(raw)) {
                while (!(currentWord = Mio.GetWord(raw)).equals("")) {
                    // Mio.Getword läser ett ord avgränsat av blanka från tangenterna och returnera
                    // det. Vi sparar detta ord i en string index.
                    index = Mio.GetWord(raw);

                    // Vi sparar längden av indexen i int indexLength.
                    indexLength = index.length();

                    // Ifall ordet är annorlunda så sparar vi och ändrar ord.
                    if (!currentWord.equals(previousWord)) {

                        // (FIL I) - Vi skriver och sparar data i fil I. Vi tänker på teckenkodning.
                        file_i.write((previousWord + " " + previousLBytePosition + " " + wordOccurs + "\n")
                                .getBytes(ISO_Latin_1));

                        // (FIL A) - Vi använder oss av vår hash-funktion. Vi kollar ifall det redan
                        // finns ett ord med samma hash. Vi använder vår A-array som är på internminnet.
                        int hash = wPrefix(previousWord);
                        if (A[hash] == 0) {
                            A[hash] = iBytePositon;
                        }

                        // TODO: Osäker vad den här gör helt
                        // iBytePosition += prevWord.length() + getLengthOfInt(prevPBytePosition) +
                        // getLengthOfInt(wordOccurrences) + 3;

                        previousWord = currentWord;
                        previousLBytePosition = lBytePositon;
                        wordOccurs = 0;
                    }
                    lBytePositon += indexLength + 1;
                    wordOccurs++;

                    // (FIL L) - Vi skriver index stringen och konkatenerar med \n (newline),
                    file_l.write(index.concat("\n").getBytes(ISO_Latin_1));
                }

                // Extra för att spara det sista ordet.
                // (FIL I)
                file_i.write((previousWord + " " + previousLBytePosition + " " + wordOccurs + "\n")
                        .getBytes(ISO_Latin_1));
                // (FIL A)
                int hash = wPrefix(previousWord);
                if (A[hash] == 0) {
                    A[hash] = iBytePositon;
                }
            }
        }
    }
}