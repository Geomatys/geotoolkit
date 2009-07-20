/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.NoSuchElementException;
import junit.framework.TestCase;

import org.geotoolkit.data.FeatureReader;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureReaderIteratorTest extends TestCase {

    public void testCloseOnException() {
        FeatureReaderIterator it = new FeatureReaderIterator(new BreakingFeatureReader());
        assertFalse(it.hasNext());
    }

    class BreakingFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

        public void close() throws IOException {
            throw new IllegalStateException("The exception we saw in GEOT-2068");
        }

        public SimpleFeatureType getFeatureType() {
            return null;
        }

        public boolean hasNext() throws IOException {
            throw new IllegalStateException("The exception we saw in GEOT-2068");
        }

        public SimpleFeature next() throws IOException, IllegalArgumentException,
                NoSuchElementException {
            return null;
        }
    }
}
