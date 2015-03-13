package SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.scorer;

import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertyException;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet;

import java.util.List;

/**
 * Performs a simple normalization of all token-scores by
 *
 * @author Holger Brandl
 */
public class MaxScoreNormalizer implements ScoreNormalizer {


    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
    }

    public MaxScoreNormalizer() {
    }


    @Override
    public Scoreable normalize(List<? extends Scoreable> scoreableList, Scoreable bestToken) {
        for (Scoreable scoreable : scoreableList) {
            scoreable.normalizeScore(bestToken.getScore());
        }

        return bestToken;
    }
}
