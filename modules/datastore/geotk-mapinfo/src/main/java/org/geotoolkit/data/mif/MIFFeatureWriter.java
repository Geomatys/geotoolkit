package org.geotoolkit.data.mif;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * Class Description
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 06/03/13
 */
public class MIFFeatureWriter implements FeatureWriter {

    MIFManager master;

    @Override
    public FeatureType getFeatureType() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public void remove() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("No implementation exists for this method.");
    }
}
