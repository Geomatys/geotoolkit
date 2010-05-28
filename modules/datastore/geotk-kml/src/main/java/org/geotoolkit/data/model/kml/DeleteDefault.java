package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class DeleteDefault implements Delete {

    private List<AbstractFeature> features;

    /**
     *
     * @param features
     */
    public DeleteDefault(List<AbstractFeature> features){
        this.features = features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getFeatures() {return this.features;}
}
