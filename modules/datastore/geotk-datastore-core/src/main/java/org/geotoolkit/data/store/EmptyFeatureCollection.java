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
import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Envelope;

public class EmptyFeatureCollection extends DataFeatureCollection {

    /**
     * null bounds
     */
    private static final JTSEnvelope2D BOUNDS = new JTSEnvelope2D(new Envelope(), null);

    static {
        BOUNDS.setToNull();
    }

    public EmptyFeatureCollection(final SimpleFeatureType schema) {
        super(null, schema);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSEnvelope2D getBounds() {
        return BOUNDS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getCount() throws IOException {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Iterator openIterator() {
        return new EmptyIterator();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void closeIterator(final Iterator close) {
        //do nothing
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(SimpleFeature object) {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(Object object) {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean removeAll(Collection collection) {
        return false;
    }

    public boolean isValid() {
        return true;
    }
}
