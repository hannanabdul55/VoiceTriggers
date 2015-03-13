/*
* Copyright 1999-2002 Carnegie Mellon University.
* Portions Copyright 2002 Sun Microsystems, Inc.
* Portions Copyright 2002 Mitsubishi Electric Research Laboratories.
* All Rights Reserved.  Use is subject to license terms.
*
* See the file "license.terms" for information on usage and
* redistribution of this file, and for a DISCLAIMER OF ALL
* WARRANTIES.
*
*/

package SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.pruner;

import SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.search.ActiveList;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertyException;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet;

/** A Null pruner. Does no actual pruning */
public class NullPruner implements Pruner {


    /* (non-Javadoc)
    * @see SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.Configurable#newProperties(SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet)
    */
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
    }


    /** Creates a simple pruner */
    public NullPruner() {
    }


    /** starts the pruner */
    @Override
    public void startRecognition() {
    }


    /**
     * prunes the given set of states
     *
     * @param activeList the active list of tokens
     * @return the pruned (and possibly new) activeList
     */
    @Override
    public ActiveList prune(ActiveList activeList) {
        return activeList;
    }


    /** Performs post-recognition cleanup. */
    @Override
    public void stopRecognition() {
    }


    /* (non-Javadoc)
    * @see SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.pruner.Pruner#allocate()
    */
    @Override
    public void allocate() {

    }


    /* (non-Javadoc)
    * @see SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.pruner.Pruner#deallocate()
    */
    @Override
    public void deallocate() {

    }

}
