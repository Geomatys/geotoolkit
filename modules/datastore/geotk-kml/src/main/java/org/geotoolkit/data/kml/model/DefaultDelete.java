package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultDelete implements Delete {

    private List<AbstractFeature> features;

    /**
     * 
     */
    public DefaultDelete() {
        this.features = EMPTY_LIST;
    }

    /**
     * 
     * @param features
     */
    public DefaultDelete(List<AbstractFeature> features) {
        this.features = (features == null) ? EMPTY_LIST : features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getFeatures() {
        return this.features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFeatures(List<AbstractFeature> features) {
        this.features = features;
    }
}
