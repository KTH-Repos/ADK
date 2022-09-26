
/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;

  static int partDist(String w1, String w2, int w1len, int w2len, int m[][]) {

    // Fyller i matrisen längs första kolumnet och första raden.
    for (int i = 0; i <= w1len; i++) {
      m[i][0] = i;
    }
    for (int j = 0; j <= w2len; j++) {
      m[0][j] = j;
    }

    int laggtill = 0;
    int tabort = 0;
    int bytut = 0;

    // Vi går igenom de andra elementen (algoritmen) från teorilabb 2.
    for (int i = 1; i <= w1len; i++) {

      for (int j = 1; j <= w2len; j++) {

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
    return m[w1len][w2len];

    // if (w1len == 0)
    // return w2len;

    // if (w2len == 0)
    // return w1len;

    // int res = partDist(w1, w2, w1len - 1, w2len - 1) +
    // (w1.charAt(w1len - 1) == w2.charAt(w2len - 1) ? 0 : 1);

    // int addLetter = partDist(w1, w2, w1len - 1, w2len) + 1;

    // if (addLetter < res)
    // res = addLetter;

    // int deleteLetter = partDist(w1, w2, w1len, w2len - 1) + 1;

    // if (deleteLetter < res)
    // res = deleteLetter;
    // return res;
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

  int distance(String w1, String w2, int m[][]) {
    return partDist(w1, w2, w1.length(), w2.length(), m);
  }

  public ClosestWords(String w, List<String> wordList, int m[][]) {

    for (String s : wordList) {
      int dist = distance(w, s, m);

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
