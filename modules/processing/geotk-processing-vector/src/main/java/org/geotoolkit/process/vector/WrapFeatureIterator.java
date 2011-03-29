/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.process.vector;

import java.util.NoSuchElementException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureIterator;
import org.opengis.feature.Feature;

/**
 * Encapsulate FeatureIterator
 * @author Quentin Boileau
 * @module pending
 */
public abstract class WrapFeatureIterator implements FeatureIterator<Feature> {

    private final FeatureIterator<?> originalFI;
    private Feature nextFeature;

    /**
     * Connect to the original FeatureIterator
     * @param originalFI FeatureIterator
     */
    public WrapFeatureIterator(final FeatureIterator<?> originalFI) {
        this.originalFI = originalFI;
        nextFeature = null;
    }

    /**
     * Return the Feature modify by the process
     * @return Feature
     */
    @Override
    public Feature next() {
        findNext();

        if (nextFeature == null) {
            throw new NoSuchElementException("No more Feature.");
        }

        final Feature feat = nextFeature;
        nextFeature = null;
        return feat;
    }

    /**
     * Close the original FeatureIterator
     */
    @Override
    public void close() {
        originalFI.close();
    }

    /**
     * Return hasNext() result from the original FeatureIterator
     */
    @Override
    public boolean hasNext() {
        findNext();
        return nextFeature != null;
    }

    /**
     * Useless because current FeatureCollection can't be modified
     */
    @Override
    public void remove() {
        throw new DataStoreRuntimeException("Unmodifiable collection");
    }

    /**
     * Find the next feature
     */
    private void findNext() {
        if (nextFeature != null) {
            return;
        }

        while (nextFeature == null && originalFI.hasNext()) {
            nextFeature = modify(originalFI.next());
        }
    }

    protected abstract Feature modify (Feature feature);
    
}