public class DualSorterTest {
public double dist(short [] aInd, float [] aVals, short [] bInd, float [] bVals) {


    int aCounter = 0;
    int bCounter = 0;
    
    double res = 0;
    while (aCounter < aInd.length && bCounter < bInd.length) {
      if (aInd[aCounter] == bInd[bCounter]) {
        res += (aVals[aCounter] - bVals[bCounter])*(aVals[aCounter] - bVals[bCounter]);
        aCounter++;
        bCounter++;
      } else if (aInd[aCounter] < bInd[bCounter]) {
        res += aVals[aCounter]*aVals[aCounter];
        aCounter++;
      } else {
        res += bVals[bCounter]*bVals[bCounter];
        bCounter++;
      }
    }
    while (bCounter < bInd.length) {
      res += bVals[bCounter]*bVals[bCounter];
      bCounter++;
    }
    while (aCounter < aInd.length) {
      res += aVals[bCounter]*aVals[bCounter];
      aCounter++;
    }
    return res;
  }
}
