package SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.databranch;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.Data;


/** Defines some API-elements for Data-observer classes. */
public interface DataListener {

    /** This method is invoked when a new {@link Data} object becomes available.
     * @param data*/
    public void processDataFrame(Data data);

}
