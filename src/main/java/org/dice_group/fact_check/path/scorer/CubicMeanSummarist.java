package org.dice_group.fact_check.path.scorer;


/**
 * https://github.com/dice-group/COPAAL/blob/d1/service/src/main/java/org/dice/FactCheck/Corraborative/sum/CubicMeanSummarist.java
 *
 */
public class CubicMeanSummarist implements ScoreSummarist {

    @Override
    public double summarize(double[] scores) {
        double score = 0;
        double temp;
        for (int s = scores.length - 1; s >= 0; s--) {
            if (scores[s] >= 0) {
                temp = Math.min(scores[s], 1.0);
            } else {
                temp = Math.max(scores[s], -1.0);
            }
            score += Math.pow(temp, 3.0);
        }
        score /= scores.length;
        return (score < 0 ? -1 : 1) * Math.pow(Math.abs(score), 1.0 / 3.0);
    }

}
