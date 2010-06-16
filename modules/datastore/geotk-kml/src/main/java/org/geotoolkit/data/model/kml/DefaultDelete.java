package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultDelete implements Delete {

    private final List<AbstractFeature> features;

    /**
     *
     * @param features
     */
    public DefaultDelete(List<AbstractFeature> features){
        this.features = (features == null) ? EMPTY_LIST : features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getFeatures() {return this.features;}
}
