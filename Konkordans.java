import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
 * ADK-2350-LABB 1
 * Max Israelsson (maxisr@kth.se)
 * Tomas Yonas Weldetinsae (tywe@kth.se)
 */
class Konkordans {

    // Filer (SSH KTH)
    /* private static final File FILE_RAWINDEX = new File("/afs/kth.se/misc/info/kurser/DD2350/adk22/labb1/rawindex.txt");
    private static final File FILE_KORPUS = new File("/afs/kth.se/misc/info/kurser/DD2350/adk22/labb1/korpus");
    private static final File FILE_A = new File("/var/tmp/Afile.txt");
    private static final File FILE_I = new File("/var/tmp/Ifile.txt");
    private static final File FILE_L = new File("/var/tmp/Lfile.txt"); */

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

    // Vår int-array A som är lokal.
    private static int[] A = new int[BASE * BASE * BASE];

    // Största ascii-värdet i svenska är 246, vilket motsvarar ö.
    // Vi skapar en array för att omvanlda stora och små bokstäver till de olika
    // värdena som används sedan i wPrefix.
    private static int[] charMap = new int[247];

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

        // Vi använder använder oss utav en förberäknad funktion f som omvandlar dom
        // tänkbara tecknen som kommer in till siffror,
        // t.ex “mellanslag”→0, “a”→1, “b”→2, […], “ö”→29.
        createCharMap();

        // Vi kollar ifall längden ifall längden är fel (alltså inte ett).
        /* if (args.length != 1) {
            System.out.println("Fel indata: java Konkordans <sökord>");

            // Vi tilldelar 1 då ett fel inträffade.
            System.exit(1);
        } */

        // Vi sparar vilket ordet som ska sökas och hittas.
        //String wordToFind = args[0];
        String wordToFind = "algoritmens";


        // Vi kollar ifall filerna finns. Om de inte gör det skapar vi dom i
        // createConstructionFiles-metoden.
        if (!checkIfFilesExist()) {

            // Vi läser in RAWINDEX filen och skickar den till
            // createConstructionFiles-metoden.
            createConstructionFiles(Mio.OpenRead(FILE_RAWINDEX));
            long endTime = System.nanoTime();
            System.out.println("Skapade filer: A.txt, I.txt och L.txt");
            long totalTime = endTime - startTime;
            System.out.printf("KONSTRUKTIONSPROGRAMMET - Körtid: %d s.\n", totalTime / 1000000000);

            startTime = System.nanoTime();
        }
        // Om filerna redan är skapade läser vi enbart från A.txt till internminnet.
        else {
            loadFromFileA();
        }

        // Vi söker efter ordet och kollar ifall det finns.
        if (!searchWord(wordToFind.toLowerCase())) {
            System.out.println("Hittade inte fler / inga: '" + wordToFind + "'.");
        }

        // Vi skriver slutligen ut sökprogrammets tid.
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.printf("SÖKPROGRAMMET - Körtid: %d ms.\n", totalTime / 1000000);
    }

    /**
     * checkFilesExist kollar ifall fil A.txt, fil I.txt och fil L.txt finns.
     * 
     * @return sant eller falskt
     */
    private static boolean checkIfFilesExist() {
        // (.exists) returns true if and only if the file or directory denoted by this
        // abstract pathname exists; false otherwise.
        if (FILE_A.exists() && FILE_I.exists() && FILE_L.exists()) {
            return true;
        }
        return false;
    }

    // ###########################################################################################################
    // KONSTRUKTIONSPROGRAMMET
    // ###########################################################################################################
    /**
     * Skapar A, I, L filarna.
     * A - har position av varje unikt ord i I.
     * I - har position av första instansen av ordet i L.
     * L - har position av varje förekomst av ord i korpus.
     * 
     * @param raw filen med innehållet som ska läsas.
     * @throws IOException om problem med filerna.
     */
    private static void createConstructionFiles(BufferedInputStream raw) throws IOException {
        String currentWord = "";
        String previousWord = "a"; // Vi vet att vi börjar på "a".

        String index;
        int indexLength = 0;

        int wordOccurs = 0;

        // Våra pekare för att kunna hitta i de olika filerna.
        int iPositon = 0;
        int lPositon = 0;
        int previousLPosition = 0;

        // Vi försöker att läsa in fil I. (OUTPUT)
        try (BufferedOutputStream file_i = new BufferedOutputStream(new FileOutputStream(FILE_I))) {

            // Vi försöker att läsa in fil L. (OUTPUT)
            try (BufferedOutputStream file_l = new BufferedOutputStream(new FileOutputStream(FILE_L))) {

                // While-loopen slutas när vi inte hittar något nytt ord från
                // "raw"-bufferedinputstream.
                while (!(currentWord = Mio.GetWord(raw)).equals("")) {

                    // Mio.GetWord läser ett ord avgränsat av blanka från tangenterna och returnera
                    // det. Vi sparar detta ord i en string index.
                    index = Mio.GetWord(raw);

                    // Vi sparar längden av indexen i int indexLength.
                    indexLength = index.length();

                    // Ifall ordet är annorlunda så sparar vi och ändrar ord.
                    if (!currentWord.equals(previousWord)) {

                        // (FIL I) - Vi skriver och sparar data i fil I. Vi tänker på teckenkodning.
                        file_i.write((previousWord + " " + previousLPosition + " " + wordOccurs + "\n")
                                .getBytes(ISO_LATIN_1));

                        // (A Hashning) - Vi använder oss av vår hash-funktion. Vi kollar ifall det
                        // redan finns ett ord med samma hash. Vi använder vår A-array som är på
                        // internminnet.
                        // Om det inte finns så tilldelar vi positionen av första instans av ordet
                        // i L.
                        int hash = wPrefix(previousWord);
                        if (A[hash] == 0) {
                            A[hash] = iPositon;
                        }

                        // Vi får fram adressen som läggs i A genom att vi lägger ihop längden av
                        // previousWord, previousLPosition och wordOccurs. Vi lägger även till 3 då
                        // vår indexering är 3. Värt att notera är att vi har int och inte bytes.
                        iPositon += previousWord.length() + getLengthInt(previousLPosition)
                                + getLengthInt(wordOccurs) + 3;

                        previousWord = currentWord;
                        previousLPosition = lPositon;
                        wordOccurs = 0;
                    }
                    // Vi ökar lPositon med index-längden plus ett.
                    lPositon += indexLength + 1;

                    // Vi ökar wordOccurs med ett ifall ordet förekommer igen.
                    wordOccurs++;

                    // (FIL L) - Vi skriver index-stringen och gör en newline efteråt med \n
                    // (newline),
                    file_l.write((index + "\n").getBytes(ISO_LATIN_1));
                }

                // Extra för att spara det sista ordet.
                // (FIL I)
                file_i.write((previousWord + " " + previousLPosition + " " + wordOccurs + "\n")
                        .getBytes(ISO_LATIN_1));

                // (A Hashning)
                int hash = wPrefix(previousWord);
                if (A[hash] == 0) {
                    A[hash] = iPositon;
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

        // Kollar om längden på ordet är mindre än tre. (Fallen där vi har ord som är
        // kortare än 3)
        if (word.length() < 3) {

            // Om ordet har en längd av ett.
            if (word.length() == 1) {
                word = "  " + word;

                // Om ordet har en längd av två.
            } else if (word.length() == 2) {
                word = " " + word;
            }
        }
        // Motsvarar: h(w) = f(w[0])*900 + f(w[1])*30 + f(w[2]) - Denna gick vi igenom
        // på föreläsning 3.
        return charMap[word.charAt(0)] * (BASE * BASE) + charMap[word.charAt(1)] * BASE + charMap[word.charAt(2)];
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

                // För varje iPosition i arrayen:
                // Vi skriver till fil A iPositionen och gör ett mellanslag mellan varje
                // steg.
                for(int i = 0; i < A.length; i++) {
                    int iPosition = A[i];
                    file_a.write((iPosition + " ").getBytes(ISO_LATIN_1));
                }
        }
    }

    /**
     * String-Based Solution
     * Vi tar fram längden för en int (num).
     * https://www.baeldung.com/java-number-of-digits-in-int
     * 
     * @param num det nummer vi ska ta fram längden till.
     * @return längden av num.
     */
    static int getLengthInt(int num) {
        return String.valueOf(num).length();
    }

    /*
     * Skapar en int-array som omvandlar bokstäverna i det svenska alfabetet till
     * deras motsvarande sifferepresentation.
     * CharMap arrayen används sedan i wPrefix.
     */
    private static void createCharMap() {
        // Vi tilldelar alla bokstäver (stora och små) dess värde i int-arrayen charMap.
        charMap[(int) ' '] = 0;
        charMap[(int) 'A'] = charMap[(int) 'a'] = 1;
        charMap[(int) 'B'] = charMap[(int) 'b'] = 2;
        charMap[(int) 'C'] = charMap[(int) 'c'] = 3;
        charMap[(int) 'D'] = charMap[(int) 'd'] = 4;
        charMap[(int) 'E'] = charMap[(int) 'e'] = 5;
        charMap[(int) 'F'] = charMap[(int) 'f'] = 6;
        charMap[(int) 'G'] = charMap[(int) 'g'] = 7;
        charMap[(int) 'H'] = charMap[(int) 'h'] = 8;
        charMap[(int) 'I'] = charMap[(int) 'i'] = 9;
        charMap[(int) 'J'] = charMap[(int) 'j'] = 10;
        charMap[(int) 'K'] = charMap[(int) 'k'] = 11;
        charMap[(int) 'L'] = charMap[(int) 'l'] = 12;
        charMap[(int) 'M'] = charMap[(int) 'm'] = 13;
        charMap[(int) 'N'] = charMap[(int) 'n'] = 14;
        charMap[(int) 'O'] = charMap[(int) 'o'] = 15;
        charMap[(int) 'P'] = charMap[(int) 'p'] = 16;
        charMap[(int) 'Q'] = charMap[(int) 'q'] = 17;
        charMap[(int) 'R'] = charMap[(int) 'r'] = 18;
        charMap[(int) 'S'] = charMap[(int) 's'] = 19;
        charMap[(int) 'T'] = charMap[(int) 't'] = 20;
        charMap[(int) 'U'] = charMap[(int) 'u'] = 21;
        charMap[(int) 'V'] = charMap[(int) 'v'] = 22;
        charMap[(int) 'W'] = charMap[(int) 'w'] = 23;
        charMap[(int) 'X'] = charMap[(int) 'x'] = 24;
        charMap[(int) 'Y'] = charMap[(int) 'y'] = 25;
        charMap[(int) 'Z'] = charMap[(int) 'z'] = 26;
        charMap[(int) 'Ä'] = charMap[(int) 'ä'] = 27;
        charMap[(int) 'Å'] = charMap[(int) 'å'] = 28;
        charMap[(int) 'Ö'] = charMap[(int) 'ö'] = 29;
    }

    // ###########################################################################################################
    // SÖKPROGRAMMET
    // ###########################################################################################################
    /**
     * Läser av innehållet i filen: A.txt och tillsätter värdena i den lokala
     * arrayen A. (för att ligga på internminnet)
     * 
     * @throws IOException om filen laddas fel.
     */
    private static void loadFromFileA() throws IOException {
        // (Läser från FIL A)
        // Vi försöker att läsa ut fil A. (INPUT)
        try (BufferedInputStream file_a = new BufferedInputStream(new FileInputStream(FILE_A))) {

            // Vi vet redan hur många läsningar vi kommer att behöva göra och därför kan vi
            // ta längden av A. (BASE * BASE * BASE)
            for (int i = 0; i < A.length; i++) {
                A[i] = Mio.GetInt(file_a);
            }
        }
    }

    /**
     * Vi söker efter ett ord med hjälp av filerna vi skapade med konkordans.
     *
     * @param wordToFind ordet som ska hittas.
     * @return om sökordet inte hittades (false), om sökordet hittades (true)
     * @throws IOException
     */
    private static Boolean searchWord(String wordToFind) throws IOException {

        // Vi börjar med att spara det hashade värdet från wPrefix.
        int hash = wPrefix(wordToFind);

        // Vi använder sedan det hashade värdet och tar fram vår första pekare.
        int first = A[hash];
        int next;

        // Kollar om det hashade värdet motsvarar hashade ööö (slut). Det sista hashade
        // ordet.
        // 26999 = (hash-ööö)
        if (hash == 26999) {
            next = first;
        } else {
            // Vi håller på tills vi hittar en position i A som inte är tom. (vi tar fram
            // nästa elementet i vår hash) Vi behöver det i binärsökningen.
            int i = 1;
            while (true) {
                if ((A[hash + i]) != 0) {
                    next = (A[hash + i]);
                    break;
                }
                i++;
            }
        }

        // Vi söker i I-filen
        // [position av första instans i L (0), position av nästa instans i L
        // (1), antal förekomster av samma ordet (2) (-1 om ordet inte finns)]
        int[] lArray = binarySearch(wordToFind, first, next);

        // Vi tar bytepositionen av första ordet i L.
        int firstL = lArray[0];

        // Vi kollar om värdet av firstPosition är lika med -1.
        // Ifall det stämmer hittades inget ord i binärsökningen.
        if (firstL == -1) {
            return false;
        }

        // Vi tar bytepositionen av nästa ord i L.
        int lastL = lArray[1];

        // Vi tar antalet förekomster av samma ord i L.
        int numOfOccur = lArray[2];

        System.out.println("Det finns " + numOfOccur + " förekomster av ordet.");

        // Vi söker nu i L och KORPUS.
        RandomAccessFile L = new RandomAccessFile(FILE_L, "r");
        RandomAccessFile KORPUS = new RandomAccessFile(FILE_KORPUS, "r");

        // Hoppar till firstL byteposition. (bytepositionen av första ordet i L)
        L.seek(firstL);

        // Vi skapar en bufferedReader.
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(L.getFD()), ISO_LATIN_1));

        // Om det är de sista ordet
        // Vi kollar först om inte lastL är -1 (finns ej från binärsökningen)
        if (lastL == -1) {
            String line;

            // Vi läser av en ny rad tills vi hittar en som är tom.
            while ((line = bufferedReader.readLine()) != null) {

                // Vi tar fram bytepositionen
                int position = Integer.parseInt(line);

                // Skriver ut "lines" från en fil med en angiven position och en längd.
                printFinalResult(KORPUS, position, wordToFind.length());
            }
        } else {
            int linesPrinted = 0;

            // Vi kollar om första instansen av ordet är mindre än
            while (firstL < lastL && linesPrinted < MAXLINES) {
                String korpusLine = bufferedReader.readLine();

                // Vi tar fram bytepositionen
                int position = Integer.parseInt(korpusLine);

                // Skriver ut "lines" från en fil med en angiven position och en längd.
                printFinalResult(KORPUS, position, wordToFind.length());

                // Vi hoppar till den raden som har sökordet i sig.
                firstL += korpusLine.length() + 1;

                // Vi ökar antalet utskrivna rader tills det blir 25
                linesPrinted++;

                // Vi kollar ifall vi ska fråga användaren om fler sökträffar.
                if (linesPrinted == (MAXLINES - 1)) {

                    // Vi frågar användaren om de vill se fler sökträffar.
                    System.out.println("Vil du se fler sökträffar (ja/nej)?: ");
                    if (Mio.GetWord().equalsIgnoreCase("ja")) {
                        linesPrinted = 0;
                    } else {
                        return true;
                    }
                }
            }
        }

        // Om sökordet inte finns returneras false.
        return false;
    }

    /**
     * Skriver ut "lines" från en fil med en angiven position och en längd.
     * 
     * @param FILE       filen som läses.
     * @param position   den angivna positionen.
     * @param wordLength den angivna längden på ordet.
     * @throws IOException
     */
    static void printFinalResult(RandomAccessFile FILE, int position, int wordLength) throws IOException {

        // 30 tecken före och 30 tecken efter.
        byte[] buffer = new byte[60 + wordLength];

        // Kollar om positionen - 30 är mindre än noll. (Det finns inte 30 tecken
        // framför).
        if ((position -= 30) < 0)
            position = 0;

        FILE.seek(position);
        FILE.read(buffer);

        // Vi ersätter newline med blanksteg.
        for (int i = 0; i < buffer.length; i++) {

            // 0xA == ISO_LATIN_1 (newline)
            // 0x20 == ISO_LATIN_1 (space)
            if (buffer[i] == 0xA) {
                buffer[i] = 0x20;
            }
        }

        // Slutligen skriver vi ut vad vi hade.
        System.out.println(new String(buffer, ISO_LATIN_1));
    }

    /**
     * Binary Search
     * Binary Search för I-filen.
     * https://www.geeksforgeeks.org/binary-search/
     * 
     * @param searchWord ordet som söks i filen I.
     * @param first      (lower) första bytepositionen av de 3 bokstäverna.
     * @param next       (upper) nästa bytepositionen av de 3 bokstäverna.
     * @return [0] start position av ordet i L (byteposition), [1] start position av
     *         nästa ord i L (byteposition) och [3] hur många gånger ordet
     *         förekommer (-1 ordet finns inte).
     * @throws IOException
     */
    static int[] binarySearch(String searchWord, int first, int next) throws IOException {

      // Vi skapar en array som vi uppdaterar.
      int[] returnArray = new int[] { -1, -1, 0 };

      RandomAccessFile I = new RandomAccessFile(FILE_I, "r");

      // Vi skapar en bufferedReader för att kunna läsa "lines".
      BufferedReader bufferedReader = new BufferedReader(
              new InputStreamReader(new FileInputStream(I.getFD()), ISO_LATIN_1));

      while (next - first > 1000) {

          // Vi ser till att mid är i mellan first och next (adress/bytePosition).
          // int mid = first + ((next - first) / 2);
          int middle = (first + next) / 2;
          I.seek(middle);

          // Vi ser till så att vi hamnar i mitten och sparar sedan det ord som ligger
          // där.
          middle += I.readLine().length() + 1;
          String middleWord = I.readLine().split(" ")[0];

          // Om midWord ligger före searchWord
          if (middleWord.compareTo(searchWord) < 0) {
              first = middle;
          }
          // Om midWord ligger efter searchWord eller om midWord är searchWord.
          else {
              next = middle;
          }
      }

      // Hoppar till first byteposition. (bytepositionen av de 3 bokstäverna)
      I.seek(first);

      // Vi får igenom sedan varje "line" för att kunna spara antalet förekomster av
      // ord, bytepositionen för ordet i L och bytepositionen för nästa ord i L.
      while (first <= next) {
          String line = bufferedReader.readLine();
          String[] checkLine = line.split(" ");
          String checkWord = checkLine[0];

          // Vi kollar om checkWord är samma som searchWord
          if (checkWord.equals(searchWord)) {

              // Vi sparar "start postion" av ordet i L.
              returnArray[0] = Integer.parseInt(checkLine[1]);

              // Vi tar "start position" av nästa ord i L.
              String lineCheck = bufferedReader.readLine();

              // Fall ifall vi är på den sista "line". (bufferedReader.readLine() ger null om
              // slut)
              if (lineCheck != null) {

                  // Ifall det är slutet så sparar vi positionen vi fick innan.
                  returnArray[1] = Integer.parseInt(lineCheck.split(" ")[1]);
              }

              // Vi sparar hur många gånger ordet förekommer.
              returnArray[2] = Integer.parseInt(checkLine[2]);

              // Vi returnerar arrayen med informationen.
              return returnArray;
          }

          // Går till nästa "line"
          first += line.length() + 1;
      }

      // Vi returnerar arrayen med informationen.
      return returnArray;
  }

    // ###########################################################################################################
    // Reflektion
    // ###########################################################################################################

    /*-----------------------------------------------------------------------------
     * Reflektera över er erfarenhetfrån parprogrammeringen vid denna labb (krävs
     * endast för labbleveranspoäng)
     * 
     * Vi tyckte att det fungerade bra. Det är inte vår första gång då vi jobbat i
     * par. Parprogrammeringen hjälpte oss att få en tydlig bild av vad vi ska göra
     * och hur vi ska göra. Man kan alltid bolla med ideer fram och tillbaka och det
     * finns mindre risk att man fastnar. 
     *
     * -----------------------------------------------------------------------------
     * Vid redovisningen ska ni kunna motivera varför internminneskomplexiteten är konstant.
     * 
     * Internminneskomplexiteten är konstant för att seek() får in en bytePosition för att direkt 
     * kunna hoppa till rätt position i filerna. 
     * 
     * Filerna vi läser (utom A) ligger på vanligt minne. Vi behöver inte all information samtidgt vilket gör att vi inte behöver ha allt sparat på internminnet. Vi behöver bara hoppa till de delarna vi behöver i Korpus med seek().
     * 
     * -----------------------------------------------------------------------------
     * Visa en uppsättning testfall som ni har tagit fram för att kolla att
     * programmet gör rätt. Ni ska också kunna motivera varför ni valt just dessa
     * testfall.
     * 
     * algoritmens (Vi kan jämföra svaret som fanns i labblydelsen.) - 12 förekomster
     * AlgoRiTmEns (Vi kan kolla ifall vi får samma svar med stora och små bokstäver.) - 12 förekomster
     * Amager (Första ordet i korpus.) - 174 förekomster
     * Mult (Sista ordet i korpus.) - 3 förekomster
     * A (Enbokstavsord.) - 81096 förekomster
     * Därefter (Specialtecken.) - 41518 förekomster
     * BLABXFriluftsfjandetXBLALA (Ett ord som inte existerar.) - 0 förekomster
     * 
     * -----------------------------------------------------------------------------
     * Visa att programmet fungerar och är tillräckligt snabbt för era testfall och labbhandledarens testfall.
     * 
     * (Visa konsolen när vi kör och testa de nya testfallen.)
     * 
     * -----------------------------------------------------------------------------
     * Visa och förklara hur lösningens datastrukturer på fil och i minnet fungerar.
     * 
     * (Visa skiss) A ligger i internminnet och de resternade i vanligt minne. Skissen visar hur de olika filerna kopplar med varandra.
     * 
     * -----------------------------------------------------------------------------
     * Visa programkoden och vara beredd att svara på frågor om den.
     * 
     * (Visa programkod och svara på frågorna.)
     * 
     */
}