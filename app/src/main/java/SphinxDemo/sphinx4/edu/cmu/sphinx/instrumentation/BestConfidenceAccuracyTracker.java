/*
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2004 Sun Microsystems, Inc.  
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
package SphinxDemo.sphinx4.edu.cmu.sphinx.instrumentation;

import SphinxDemo.sphinx4.edu.cmu.sphinx.result.*;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.NISTAlign;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.*;
import SphinxDemo.sphinx4.edu.cmu.sphinx.recognizer.Recognizer;

/**
 * Tracks and reports recognition accuracy using the "confidenceScorer" component specified in the ConfigurationManager.
 * The "confidenceScorer" component is typically configured to be SphinxDemo.sphinx4.edu.cmu.sphinx.result.SausageMaker.
 *
 * @see SphinxDemo.sphinx4.edu.cmu.sphinx.result.SausageMaker
 */
public class BestConfidenceAccuracyTracker extends AccuracyTracker {

    /** Defines the class to use for confidence scoring. */
    @S4Component(type = ConfidenceScorer.class)
    public final static String PROP_CONFIDENCE_SCORER = "confidenceScorer";

    /** The confidence scorer */
    protected ConfidenceScorer confidenceScorer;

    public BestConfidenceAccuracyTracker(ConfidenceScorer confidenceScorer, Recognizer recognizer, boolean showSummary, boolean showDetails, boolean showResults, boolean showAlignedResults, boolean showRawResults) {
        super( recognizer, showSummary, showDetails, showResults, showAlignedResults, showRawResults);
        this.confidenceScorer = confidenceScorer;
    }

    public BestConfidenceAccuracyTracker() {
    }

    /*
    * (non-Javadoc)
    *
    * @see SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.Configurable#newProperties(SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet)
    */
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        confidenceScorer = (ConfidenceScorer)ps.getComponent(PROP_CONFIDENCE_SCORER);
    }

    /*
    * (non-Javadoc)
    *
    * @see SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.ResultListener#newResult(SphinxDemo.sphinx4.edu.cmu.sphinx.result.Result)
    */
    @Override
    public void newResult(Result result) {
        NISTAlign aligner = getAligner();
        String ref = result.getReferenceText();
        if (result.isFinal() && (ref != null)) {
            try {
                Path bestPath = null;
                String hyp = "";
                if (result.getBestFinalToken() != null) {
                    ConfidenceResult confidenceResult =
                            confidenceScorer.score(result);
                    bestPath = confidenceResult.getBestHypothesis();
                    hyp = bestPath.getTranscriptionNoFiller();
                }
                aligner.align(ref, hyp);
                if (bestPath != null) {
                    showDetails(bestPath.getTranscription());
                } else {
                    showDetails("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
