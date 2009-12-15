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
package org.geotoolkit.data;

import java.io.Closeable;
import java.util.Iterator;

import org.opengis.feature.Feature;

/**
 * Extent the Standard Iterator, limit to Feature class
 * and add a close method (interface Closeable) that is needed by the datastore
 * to release potential resources.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FeatureIterator<F extends Feature> extends Iterator<F>, Closeable{

    /**
     * Reduce possibilties to Feature only.
     * @return Feature
     */
    @Override
    F next();

    /**
     * Release the underlying resources associated with this stream.
     */
    @Override
    void close();

}
