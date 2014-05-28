package org.geotoolkit.data.geojson.utils;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.geojson.binding.GeoJSONFeature;
import org.geotoolkit.util.collection.CloseableIterator;

/**
 * Custom FeatureIterator used for lazy parsing of GeoJSONFeature in a json file.
 *
 * @author Quentin Boileau (Geomatys)
 */
public interface GeoJSONFeatureIterator<F extends GeoJSONFeature> extends CloseableIterator<F> {

    @Override
    F next() throws FeatureStoreRuntimeException;

    @Override
    boolean hasNext() throws FeatureStoreRuntimeException;

    @Override
    void close() throws FeatureStoreRuntimeException;

}
