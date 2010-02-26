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

import org.geotoolkit.util.collection.CloseableIterator;

/**
 * Simple feature collection row iterator.
 * Iterate over a single feature iterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultRowIterator implements CloseableIterator<FeatureCollectionRow>{


    private final FeatureIterator ite;
    private final DefaultFeatureCollectionRow row = new DefaultFeatureCollectionRow();

    public DefaultRowIterator(FeatureIterator ite){
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
        row.getSelectorFeatureMap().put("s1", ite.next());
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
