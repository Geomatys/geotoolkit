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
package org.geotoolkit.data;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.util.converter.Classes;
import org.geotools.data.FeatureReader;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;


/**
 * Supports on the fly retyping of  FeatureReader<SimpleFeatureType, SimpleFeature> contents.
 * <p>
 * This may be used to have a DataStore work with your own representation of
 * Feature information.
 * </p>
 * <p>
 * Example Use:
 * </p>
 * <pre><code>
 *  FeatureReader<SimpleFeatureType, SimpleFeature> reader = dataStore.getFeatureReader( query, Transaction.AUTO_COMMIT );
 * reader = new ReTypeFeatureReader( reader, myFeatureType );
 * try {
 *   while( reader.hasNext() ){
 *     Feature f = reader.next();
 *     System.out.println( f );
 *   }
 * }
 * finally {
 *   reader.close(); // will close both
 * } 
 * </code></pre>
 * <p>
 * This Reader makes a simple <b>one to one</b> between the original schema and the target schema based
 * on descriptor name.
 * 
 * @author Jody Garnett (Refractions Research)
 * @source $URL$
 */
public class ReTypeFeatureReader implements DelegatingFeatureReader<SimpleFeatureType, SimpleFeature> {
    /**
     * The original reader we are grabbing content from
     */
    private FeatureReader<SimpleFeatureType, SimpleFeature> reader;

    /**
     * This is the target feature type we are preparing data for
     */
    private SimpleFeatureType featureType;

    /**
     * The descriptors we are going to from the original reader
     */
    private AttributeDescriptor[] types;

    /**
     * Creates retyped features
     */
    private final SimpleFeatureBuilder builder;

    private final boolean clone;

    /**
     * Constructs a FetureReader that will ReType streaming content.
     *
     * @param reader Origional FeatureReader
     * @param featureType Target FeatureType
     */
    public ReTypeFeatureReader(final FeatureReader<SimpleFeatureType, SimpleFeature> reader,
            final SimpleFeatureType featureType)
    {
        this(reader, featureType, true);
    }

    /**
     * Constructs a FetureReader that will ReType streaming content.
     *
     * @param reader Original FeatureReader
     * @param featureType Target FeatureType
     * @param clone true to clone the content
     * @since 2.3
     */
    public ReTypeFeatureReader(final FeatureReader<SimpleFeatureType, SimpleFeature> reader,
            final SimpleFeatureType featureType, final boolean clone)
    {
        this.reader = reader;
        this.featureType = featureType;
        this.clone = clone;
        types = typeAttributes(featureType, reader.getFeatureType());
        builder = new SimpleFeatureBuilder(featureType);
    }

    @Override
    public FeatureReader getDelegate() {
        return reader;
    }

    /**
     * Supplies mapping from original to target FeatureType.
     * 
     * <p>
     * Will also ensure that mapping results in a valid selection of values
     * from the original. Only the xpath expression and binding are checked.
     * </p>
     *
     * @param target Desired FeatureType
     * @param origional Original FeatureType
     *
     * @return Mapping from originoal to target FeatureType
     *
     * @throws IllegalArgumentException if unable to provide a mapping
     */
    protected AttributeDescriptor[] typeAttributes(final SimpleFeatureType target,
            final SimpleFeatureType origional)
    {
        if (target.equals(origional)) {
            throw new IllegalArgumentException(
                    "FeatureReader allready produces contents with the correct schema");
        }

        if (target.getAttributeCount() > origional.getAttributeCount()) {
            throw new IllegalArgumentException(
                    "Unable to retype  FeatureReader<SimpleFeatureType, SimpleFeature> (origional does not cover requested type)");
        }

        String xpath;
        final AttributeDescriptor[] types = new AttributeDescriptor[target.getAttributeCount()];

        for (int i = 0; i < target.getAttributeCount(); i++) {
            final AttributeDescriptor attrib = target.getDescriptor(i);
            xpath = attrib.getLocalName();

            types[i] = attrib;

            final AttributeDescriptor check = origional.getDescriptor(xpath);
            final Class<?> targetBinding = attrib.getType().getBinding();
            final Class<?> checkBinding = check.getType().getBinding();
            if (!targetBinding.isAssignableFrom(checkBinding)) {
                throw new IllegalArgumentException(
                        "Unable to retype FeatureReader for " + xpath +
                        " as " + Classes.getShortName(checkBinding) +
                        " cannot be assigned to " + Classes.getShortName(targetBinding));
            }
        }

        return types;
    }

    /**
     * @see org.geotools.data.FeatureReader#getFeatureType()
     */
    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    /**
     * @see org.geotools.data.FeatureReader#next()
     */
    @Override
    public SimpleFeature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        if (reader == null) {
            throw new IOException("FeatureReader has been closed");
        }

        final SimpleFeature next = reader.next();
        final String id = next.getID();

        String xpath;

        for (int i = 0; i < types.length; i++) {
            xpath = types[i].getLocalName();
            if (clone) {
                builder.add(DataUtilities.duplicate(next.getAttribute(xpath)));
            } else {
                builder.add(next.getAttribute(xpath));
            }
        }

        return builder.buildFeature(id);
    }

    /**
     * @see org.geotools.data.FeatureReader#hasNext()
     */
    @Override
    public boolean hasNext() throws IOException {
        return reader.hasNext();
    }

    /**
     * @see org.geotools.data.FeatureReader#close()
     */
    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
            featureType = null;
            types = null;
        }
    }
}
