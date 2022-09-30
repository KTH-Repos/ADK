
/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;

  static String senasteOrdetW1 = "";
  static String senasteOrdetW2 = "";

  /**
   * Tar fram det minsta editeringsavståndet mellan orden w1 och w2.
   * 
   * @param w1    den första strängen.
   * @param w2    den andra strängen.
   * @param w1len längden av första strängen.
   * @param w2len längden av andra strängen.
   * @param m     matrisen.
   * @return det minsta editeringsavståndet.
   */
  static int distance(String w1, String w2, int w1len, int w2len, int m[][]) {
    int p = 0;

    // Vi kollar först om w1 är ett nytt ord eller inte. Om inte så kollar vi
    // likheten mellan den senaste w2 och den nya.
    if (senasteOrdetW1 == w1)
      p = sammaOrd(w2, senasteOrdetW2);

    int laggtill = 0;
    int tabort = 0;
    int bytut = 0;

    // Vi går igenom de andra elementen (algoritmen) från teorilabb 2.
    for (int i = 1; i <= w1len; i++) {

      // Notera att vi använder p + 1 här som i sista delen från teorilabb 2. Vi
      // behöver inte skapa de gamla elementen som är samma.
      for (int j = p + 1; j <= w2len; j++) {

        // Om samma bokstav så lägger vi inte till 1 till bytut men 1 till tabort och
        // laggtill
        if (w1.charAt(i - 1) == w2.charAt(j - 1)) {
          bytut = m[i - 1][j - 1];
          tabort = m[i - 1][j] + 1;
          laggtill = m[i][j - 1] + 1;
        }

        // Om inte samma bokstav så lägger vi till 1 till bytut, tabort, laggtill.
        // för vi vet att vi ska göra någon operation.
        else {
          bytut = m[i - 1][j - 1] + 1;
          tabort = m[i - 1][j] + 1;
          laggtill = m[i][j - 1] + 1;
        }

        // Vi väljer sedan den minsta av laggtill, tabort och bytut.
        m[i][j] = min(laggtill, tabort, bytut);
      }
    }
    // Vi sparar orden vi körde.
    senasteOrdetW1 = w1;
    senasteOrdetW2 = w2;

    // Slutligen returnerar vi den färdiga matrisen.
    return m[w1len][w2len];
  }

  /**
   * Metoden för att ta fram hur lika två ord är.
   * 
   * @param w2           ett ord som sträng.
   * @param senasteOrdet ett ord som sträng.
   * @return ett värde hur många av de första bokstäverna som är lika.
   */
  static int sammaOrd(String w2, String senasteOrdet) {
    // Vårt index.
    int i = 0;

    // Vårt minsta ords längd.
    int minOrdLength = min(w2.length(), senasteOrdet.length());

    // Vi går igenom längden av det minsta ordet.
    while (i < minOrdLength) {

      // Om första elementet är samma ökar vi indexen med 1.
      if (w2.charAt(i) == senasteOrdet.charAt(i)) {
        i++;
      }
      // Vi fortsätter att göra detta tills karaktärena i orden skiljer sig.
      else {
        break;
      }
    }
    // Slutligen returnerar vi indexen som ett värde hur många av de första
    // bokstäverna som är lika.
    return i;
  }

  /**
   * Returernar det minsta elementet av a, b och c.
   *
   * @param a element a.
   * @param b element b.
   * @param c element c.
   * @return det minsta elementet a, b eller c.
   */
  static int min(int a, int b, int c) {
    if (a <= b && a <= c)
      return a;
    if (b <= a && b <= c)
      return b;
    else
      return c;
  }

  /**
   * Returnera längden av det minsta ordet i sammaOrd().
   * 
   * @param a längden av första ord.
   * @param b längden av andra orden.
   * @return det minsta ordet a eller b.
   */
  static int min(int a, int b) {
    if (a <= b)
      return a;
    else
      return b;
  }

  // Denna fick vi från labben.
  public ClosestWords(String w, List<String> wordList, int m[][]) {
    for (String s : wordList) {
      int dist = distance(w, s, w.length(), s.length(), m);

      // System.out.println("d(" + w + "," + s + ")=" + dist);

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
   * Denna fick vi från labben.
   * Returnerar den minsta distansen (closetDistance).
   */
  int getMinDistance() {
    return closestDistance;
  }

  /**
   * Denna fick vi från labben.
   * Returnerar en lista(string) med de närmaste orden.
   */
  List<String> getClosestWords() {
    return closestWords;
  }
}
