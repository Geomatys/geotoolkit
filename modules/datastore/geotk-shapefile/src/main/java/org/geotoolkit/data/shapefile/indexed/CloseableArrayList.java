/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;

/**
 * Currently just wraps ArrayList and delegates to that class
 * 
 * @author jesse
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CloseableArrayList<T extends Data> extends ArrayList<T> implements
        CloseableCollection<T> {

    public CloseableArrayList() {
    }

    public CloseableArrayList(final int length) {
        super(length);
    }
    
    @Override
    public void close() throws IOException {
        // do nothing
    }

    @Override
    public void closeIterator( final Iterator<T> iter ) throws IOException {
        // do nothing
    }
    
}
