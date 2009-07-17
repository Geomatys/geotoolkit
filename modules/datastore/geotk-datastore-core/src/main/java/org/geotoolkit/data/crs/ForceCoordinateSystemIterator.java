/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.crs;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.FeatureTypes;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * ForceCoordinateSystemFeatureReader provides a CoordinateReferenceSystem for
 * FeatureTypes.
 * 
 * <p>
 * ForceCoordinateSystemFeatureReader is a wrapper used to force
 * GeometryAttributes to a user supplied CoordinateReferenceSystem rather then
 * the default supplied by the DataStore.
 * </p>
 * 
 * <p>
 * Example Use:
 * <pre><code>
 * ForceCoordinateSystemFeatureReader reader =
 *     new ForceCoordinateSystemFeatureReader( originalReader, forceCS );
 * 
 * CoordinateReferenceSystem originalCS =
 *     originalReader.getFeatureType().getDefaultGeometry().getCoordianteSystem();
 * 
 * CoordinateReferenceSystem newCS =
 *     reader.getFeatureType().getDefaultGeometry().getCoordianteSystem();
 * 
 * assertEquals( forceCS, newCS );
 * </code></pre>
 * </p>
 *
 * @author jgarnett, Refractions Research, Inc.
 * @author aaime
 * @author $Author: jive $ (last modification)
 * @source $URL$
 * @version $Id$
 */
public class ForceCoordinateSystemIterator implements Iterator<SimpleFeature> {

    protected final FeatureIterator<SimpleFeature> reader;
    protected final SimpleFeatureBuilder builder;

    /**
     * Shortcut constructor that can be used if the new schema has already been computed
     * @param reader
     * @param schema
     */
    ForceCoordinateSystemIterator(final FeatureIterator<SimpleFeature> reader, SimpleFeatureType schema) {
        if(reader == null) throw new NullPointerException("Feature iterator can not be null");
        if(schema == null) throw new NullPointerException("Feature type can not be null");

        this.reader = reader;
        this.builder = new SimpleFeatureBuilder(schema);
    }

    /**
     * Builds a new ForceCoordinateSystemFeatureReader
     *
     * @param reader
     * @param cs
     *
     * @throws SchemaException
     * @throws NullPointerException DOCUMENT ME!
     * @throws IllegalArgumentException DOCUMENT ME!
     */
    public ForceCoordinateSystemIterator(final FeatureIterator<SimpleFeature> reader, SimpleFeatureType type,
            final CoordinateReferenceSystem cs) throws SchemaException{

        if(reader == null) throw new NullPointerException("Feature iterator can not be null");
        
        if (cs == null) {
            throw new NullPointerException("CoordinateSystem required");
        }
        final CoordinateReferenceSystem originalCs = type.getGeometryDescriptor().getCoordinateReferenceSystem();

        if (!cs.equals(originalCs)) {
            type = FeatureTypes.transform(type, cs);
        }
        builder = new SimpleFeatureBuilder(type);

        this.reader = reader;
    }

    /**
     * @see org.geotools.data.FeatureReader#getFeatureType()
     */
    public SimpleFeatureType getFeatureType() {
        return builder.getFeatureType();
    }

    /**
     * @see org.geotools.data.FeatureReader#next()
     */
    @Override
    public SimpleFeature next() throws NoSuchElementException {

        final SimpleFeature next = reader.next();
        if (builder == null) {
            return next;
        }

        try {
            return SimpleFeatureBuilder.retype(next, builder);
        } catch (IllegalAttributeException eep) {
            throw (IllegalStateException) new IllegalStateException(eep.getMessage()).initCause(eep);
        }
    }

    /**
     * @see org.geotools.data.FeatureReader#hasNext()
     */
    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.geotools.data.FeatureReader#close()
     */
    public void close() {
        reader.close();
    }
}
