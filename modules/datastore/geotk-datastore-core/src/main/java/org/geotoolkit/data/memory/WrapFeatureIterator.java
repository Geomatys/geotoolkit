/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.memory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.util.converter.Classes;
import org.opengis.feature.Feature;

/**
 * Encapsulate FeatureIterator
 * @author Quentin Boileau
 * @module pending
 */
public abstract class WrapFeatureIterator implements FeatureIterator<Feature> {

    private final Iterator<? extends Feature> originalFI;
    private Feature nextFeature;

    /**
     * Connect to the original FeatureIterator
     * @param originalFI FeatureIterator
     */
    public WrapFeatureIterator(final Iterator<? extends Feature> originalFI) {
        this.originalFI = originalFI;
        nextFeature = null;
    }

    /**
     * Return the next Feature
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
        if (originalFI instanceof Closeable) {
            try {
                ((Closeable) originalFI).close();
            } catch (IOException ex) {
                Logger.getLogger(WrapFeatureIterator.class.getName()).log(Level.WARNING, null, ex);
            }
        }
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

    /**
     * Override this method to modify on the fly the feature.
     * By default this method return the original feature.
     * @param feature
     * @return modified feature.
     */
    protected Feature modify (Feature feature){
        return feature;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String subIterator = "\u2514\u2500\u2500" + originalFI.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

}