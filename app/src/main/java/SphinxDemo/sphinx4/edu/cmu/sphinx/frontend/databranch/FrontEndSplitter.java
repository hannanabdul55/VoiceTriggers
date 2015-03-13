package SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.databranch;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.BaseDataProcessor;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.Data;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataProcessingException;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.Configurable;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertyException;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.PropertySheet;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.S4ComponentList;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates push-branches out of a Frontend. This might be used for for push-decoding or to create new pull-streams
 *
 * @see SphinxDemo.sphinx4.edu.cmu.sphinx.decoder.FrameDecoder
 * @see SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.databranch.DataBufferProcessor
 */
public class FrontEndSplitter extends BaseDataProcessor implements DataProducer {


    @S4ComponentList(type = Configurable.class, beTolerant = true)
    public static final String PROP_DATA_LISTENERS = "dataListeners";
    private List<DataListener> listeners = new ArrayList<DataListener>();

    public FrontEndSplitter() {
    }

    @Override
    public void newProperties(PropertySheet ps) throws PropertyException {
        super.newProperties(ps);

        listeners = ps.getComponentList(PROP_DATA_LISTENERS, DataListener.class);
    }


    /**
     * Reads and returns the next Data frame or return <code>null</code> if no data is available.
     *
     * @return the next Data or <code>null</code> if none is available
     * @throws SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataProcessingException
     *          if there is a data processing error
     */
    @Override
    public Data getData() throws DataProcessingException {
        Data input = getPredecessor().getData();

        for (DataListener l : listeners)
            l.processDataFrame(input);

        return input;
    }


    @Override
    public void addDataListener(DataListener l) {
        if (l == null) {
            return;
        }
        listeners.add(l);
    }


    @Override
    public void removeDataListener(DataListener l) {
        if (l == null) {
            return;
        }
        listeners.remove(l);
    }
}

