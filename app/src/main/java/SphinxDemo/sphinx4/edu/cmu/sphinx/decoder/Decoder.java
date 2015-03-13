/*
 * 
 * Copyright 1999-2004 Carnegie Mellon University.  
 * Portions Copyright 2004 Sun Microsystems, Inc.  
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 *
 */
package SphinxDemo.sphinx4.edu.cmu.sphinx.decoder;

import SphinxDemo.sphinx4.edu.cmu.sphinx.result.Result;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertyException;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.S4Integer;
import SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.search.SearchManager;

import java.util.List;

/** The primary decoder class */
public class Decoder extends AbstractDecoder {

    /** The property for the number of features to recognize at once. */
    @S4Integer(defaultValue = 100000)
    public final static String PROP_FEATURE_BLOCK_SIZE = "featureBlockSize";
    private int featureBlockSize;

    public Decoder() {

    }
    
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);
        featureBlockSize = ps.getInt(PROP_FEATURE_BLOCK_SIZE);
    }

    /**
     *
     * @param searchManager
     * @param fireNonFinalResults
     * @param autoAllocate
     * @param resultListeners
     * @param featureBlockSize
     */
    public Decoder( SearchManager searchManager, boolean fireNonFinalResults, boolean autoAllocate, List<ResultListener> resultListeners, int featureBlockSize) {
        super( searchManager, fireNonFinalResults, autoAllocate, resultListeners);
        this.featureBlockSize = featureBlockSize;
    }
    
    /**
     * Decode frames until recognition is complete.
     *
     * @param referenceText the reference text (or null)
     * @return a result
     */
    @Override
    public Result decode(String referenceText) {
        searchManager.startRecognition();
        Result result;
        do {
            result = searchManager.recognize(featureBlockSize);
            if (result != null) {
                result.setReferenceText(referenceText);
                fireResultListeners(result);
                System.out.println(result+"----------"+referenceText);
            }
        } while (result != null && !result.isFinal());
        searchManager.stopRecognition();
        //result.getDataFrames();
        return result;
    }
}
