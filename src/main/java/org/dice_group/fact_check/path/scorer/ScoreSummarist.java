package org.dice_group.fact_check.path.scorer;

/**
 * https://github.com/dice-group/COPAAL/blob/d1/service/src/main/java/org/dice/FactCheck/Corraborative/sum/ScoreSummarist.java
 *
 */
public interface ScoreSummarist {

	/**
	 * This method summarizes the given scores to a single score.
	 * 
	 * @param scores the scores that should be summarized
	 * @return the summarized score
	 */
	public double summarize(double[] scores);
}
