/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.collection.CloseableIterator;

/**
 * Simple feature collection row iterator.
 * Iterate over a single feature iterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultRowIterator implements CloseableIterator<FeatureCollectionRow>{

    private final String selectorName;
    private final FeatureIterator ite;
    private final DefaultFeatureCollectionRow row = new DefaultFeatureCollectionRow();

    public DefaultRowIterator(String selectorName, FeatureIterator ite){
        this.selectorName = selectorName;
        this.ite = ite;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
        ite.close();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return ite.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollectionRow next() {
        try {
            row.getFeatures().put(selectorName, ite.next());
        } catch (DataStoreException ex) {
            Logger.getLogger(DefaultRowIterator.class.getName()).log(Level.WARNING, null, ex);
        }
        return row;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        ite.remove();
    }
}
