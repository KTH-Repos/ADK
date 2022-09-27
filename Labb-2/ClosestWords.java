
/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;

  static String senasteOrdetW2 = "";
  static String senasteOrdetW1 = "";

  static int partDist(String w1, String w2, int w1len, int w2len, int m[][]) {

    int p = 0;
    if (senasteOrdetW1 == w1) 
      p = sammaOrd(w2, senasteOrdetW2);


    int laggtill = 0;
    int tabort = 0;
    int bytut = 0;

    // Vi går igenom de andra elementen (algoritmen) från teorilabb 2.
    for (int i = 1; i <= w1len; i++) {

      // Notera att vi använder p + 1 här som i sista delen från teorilabb 2. Vi
      // behöver
      // inte skapa de gamla elementen som är samma.
      for (int j = p + 1; j <= w2len; j++) {

        if (w1.charAt(i - 1) == w2.charAt(j - 1)) {
          bytut = m[i - 1][j - 1];
          tabort = m[i - 1][j] + 1;
          laggtill = m[i][j - 1] + 1;
        } else {
          bytut = m[i - 1][j - 1] + 1;
          tabort = m[i - 1][j] + 1;
          laggtill = m[i][j - 1] + 1;
        }
        m[i][j] = min(laggtill, tabort, bytut);
      }
    }
    senasteOrdetW1 = w1;
    senasteOrdetW2 = w2;

    return m[w1len][w2len];
  }

  /**
   * Finds the number of first same letters in w1 and w2
   * 
   * @param w1
   * @param w2
   * @return the number of equal first letters in w1 and w2
   */
  static int sammaOrd(String w2, String senasteOrdet) {
    int i = 0;
    int minLength = min(w2.length(), senasteOrdet.length());

    // Vi går igenom längden av det minsta ordet.
    while (i < minLength) {

      // Om första elementet är samma ökar vi indexen med 1.
      if (w2.charAt(i) == senasteOrdet.charAt(i)) {
        i++;
      } 
      // Vi fortsätter att göra detta tills karaktärena i orden skiljer sig.
      else {
        break;
      }
    }
    // Slutligen returnerar vi indexen som ett värde hur många av de första bokstäverna som är lika.
    return i;
  }

  // Returnera det minsta elementet av a, b och c.
  static int min(int a, int b, int c) {
    if (a <= b && a <= c)
      return a;
    if (b <= a && b <= c)
      return b;
    else
      return c;
  }

    // Returnera det minsta elementet av a och b.
    static int min(int a, int b) {
      if (a <= b)
        return a;
      else
        return b;
    }

  int distance(String w1, String w2, int m[][]) {
    return partDist(w1, w2, w1.length(), w2.length(), m);
  }

  public ClosestWords(String w, List<String> wordList, int m[][]) {

    for (String s : wordList) {
      int dist = distance(w, s, m);

      //System.out.println("d(" + w + "," + s + ")=" + dist);

      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords = new LinkedList<String>();
        closestWords.add(s);
      }

      else if (dist == closestDistance)
        closestWords.add(s);
    }
  }

  /**
   * Returnerar den minsta distansen (closetDistance).
   */
  int getMinDistance() {
    return closestDistance;
  }

  /**
   * Returnerar en lista(string) med de närmaste orden.
   */
  List<String> getClosestWords() {
    return closestWords;
  }
}
