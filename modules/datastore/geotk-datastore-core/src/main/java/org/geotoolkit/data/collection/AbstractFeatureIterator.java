/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.collection;

import org.opengis.feature.Feature;

/**
 * Abstract Feature Iterator,
 * Override methods remove and close doing nothing.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractFeatureIterator<F extends Feature> implements FeatureIterator<F>{

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
    }

}
