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
package org.geotoolkit.storage.memory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.apache.sis.util.Classes;
import org.opengis.feature.Feature;

/**
 * Encapsulate FeatureIterator
 * @author Quentin Boileau
 * @module
 * @deprecated Please restrict usage. It's a bridge object between obsolete and SIS Feature API.
 */
@Deprecated
public abstract class WrapFeatureIterator implements FeatureIterator {

    private final Iterator<? extends Feature> originalFI;
    private Feature nextFeature;

    private AutoCloseable closeOp;

    /**
     * Connect to the original FeatureIterator
     * @param originalFI FeatureIterator
     */
    public WrapFeatureIterator(final Iterator<? extends Feature> originalFI) {
        this.originalFI = originalFI;
        nextFeature = null;
        if (originalFI instanceof AutoCloseable) {
            onClose((AutoCloseable)originalFI);
        }
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
        if (closeOp == null) return;
        try {
            closeOp.close();
        } catch (Exception e) {
            Logger.getLogger("org.geotoolkit.data.memory").log(Level.WARNING, "Error while closing resources", e);
        }
    }

    /**
     * Same behavior as {@link Stream#onClose(Runnable)}. Any error on closing will be logged, but do not cause failure.
     * @param toCloseAlso A resource to close when this iterator is. Note that it stacks upon any previous registered
     *                    resource. It does not replace it/them.
     */
    public void onClose(final AutoCloseable toCloseAlso) {
        if (closeOp == null) closeOp = toCloseAlso;
        else {
            final AutoCloseable currentCloseOp = closeOp;
            closeOp = () -> {
                try (
                        final AutoCloseable newResourceToClose = toCloseAlso;
                        final AutoCloseable previouscloseOp = currentCloseOp;
                ) {}
            };
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
        throw new FeatureStoreRuntimeException("Unmodifiable collection");
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

    /**
     * @return Original/Wrapped feature iterator.
     */
    public Iterator<? extends Feature> getOriginal() {
        return originalFI;
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
