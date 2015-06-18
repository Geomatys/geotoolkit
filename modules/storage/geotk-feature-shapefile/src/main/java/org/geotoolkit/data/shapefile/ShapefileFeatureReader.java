/*
 *    Geotoolkit  An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 20032008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 20092010, Geomatys
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
package org.geotoolkit.data.shapefile;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.geometry.jts.JTS;

import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Experimental  FeatureReader<SimpleFeatureType, SimpleFeature> that always takes the first column of the
 * attributeReader as the FeatureID.  I want to get this working with postgis,
 * but then will consider other options, for those who want featureIDs created
 * automatically.  Perhaps a constructor param or a method to say that you
 * would just like to have the  FeatureReader<SimpleFeatureType, SimpleFeature> increment one for each feature,
 * prepending the typeName.  I'm also don't really like the one argument
 * constructor defaulting to the xxx typename.  I feel that it should perhaps
 * take a typename.  If people deliberately set to null then we could use xxx
 * or something. ch
 *
 * <p>
 * This now feels sorta hacky, I'm not sure that I like it, but I'm going to
 * commit as I need to go now and revisit it in a bit.  I think the idea of
 * passing in an FIDAttributeReader might be cleaner, and if none is provided
 * then do an autoincrement one.  This might then work as the
 * DefaultFeatureReader.
 * </p>
 *
 * @author Ian Schneider
 * @author Chris Holmes, TOPP
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class ShapefileFeatureReader implements FeatureReader {

    /**
     * Stores the creation stack trace if assertion are enable.
     */
    protected Throwable creationStack;
    protected final ShapefileAttributeReader attributeReader;
    protected final FeatureType schema;
    protected final FeatureIDReader fidReader;
    protected final Object[] buffer;
    protected final CoordinateReferenceSystem geomCRS;
    /**
     * if the attributs between reader and schema are the same but not in the same order.
     */
    protected final int[] attributIndexes;
    /**
     * Store the stat of the reader.
     */
    protected boolean closed = false;

    /**
     * Creates a new instance of AbstractFeatureReader
     *
     * @param attributeReader AttributeReader
     * @param fidReader FIDReader used to ID Features
     * @param schema FeatureType to use, may be <code>null</code>
     *
     * @throws SchemaException if we could not determine the correct FeatureType
     */
    private ShapefileFeatureReader(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader,
            FeatureType schema) throws MismatchedFeatureException {
        this.attributeReader = attributeReader;
        this.fidReader = fidReader;

        if (schema == null) {
            schema = createSchema(attributeReader);
            attributIndexes = new int[0];
        } else {
            //check if the attributs are mixed
            final AttributeDescriptor[] readerAtt = getDescriptors(attributeReader);
            final AttributeDescriptor[] schemaAtt = schema.getDescriptors().toArray(new AttributeDescriptor[0]);

            if (Arrays.deepEquals(readerAtt, schemaAtt)) {
                attributIndexes = new int[0];
            } else {
                //attributs are mixed
                attributIndexes = new int[readerAtt.length];
                for (int i = 0; i < readerAtt.length; i++) {
                    attributIndexes[i] = FeatureTypeUtilities.indexOfProperty(schema, readerAtt[i].getName());
                }
            }

        }

        this.schema = schema;
        this.buffer = new Object[attributeReader.getPropertyCount()];

        geomCRS = schema.getCoordinateReferenceSystem();

        // init the tracer if we need to debug a connection leak
        assert (creationStack = new IllegalStateException().fillInStackTrace()) != null;
    }

    public ShapefileFeatureReader(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader)
            throws MismatchedFeatureException {
        this(attributeReader, fidReader, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException, NoSuchElementException {
        try {
            if (attributeReader.hasNext()) {
                attributeReader.next();
                try {
                    return readFeature();
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            } else {
                throw new NoSuchElementException("There are no more Features to be read");
            }
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    protected abstract Feature readFeature() throws DataStoreException;

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws FeatureStoreRuntimeException {
        if(closed) return;
        closed = true;

        Exception ex = null;

        try {
            fidReader.close();
        } catch (DataStoreException e) {
            ex = e;
        }

        try {
            attributeReader.close();
        } catch (IOException e) {
            if (ex == null) {
                ex = e;
            } else {
                //we tryed to close both and both failed
                //return the first exception
            }
        }

        if (ex != null) {
            throw new FeatureStoreRuntimeException(ex);
        }

    }

    @Override
    protected void finalize() throws Throwable {
        if (!closed) {
            Logging.getLogger(ShapefileFeatureReader.class).warning(
                    "UNCLOSED ITERATOR : There is code leaving simple feature reader open, "
                    + "this may cause memory leaks or data integrity problems !");
            if (creationStack != null) {
                Logging.getLogger(ShapefileFeatureReader.class).log(Level.WARNING,
                        "The unclosed reader originated on this stack trace", creationStack);
            }
            close();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType() {
        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        try {
            return attributeReader.hasNext();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append('\n');
        String strFidReader = "\u251C\u2500\u2500" + fidReader.toString(); //move text to the right
        strFidReader = strFidReader.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(strFidReader);
        String strAttReader = "\u2514\u2500\u2500" + attributeReader.toString(); //move text to the right
        strAttReader = strAttReader.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append('\n').append(strAttReader);
        return sb.toString();
    }

    /**
     * Create a FeatureType based on the attributs described in the attribut reader.
     */
    private static FeatureType createSchema(final ShapefileAttributeReader attributeReader) throws MismatchedFeatureException {
        final FeatureTypeBuilder b = new FeatureTypeBuilder();
        b.setName("noTypeName");
        b.addAll(getDescriptors(attributeReader));
        return b.buildSimpleFeatureType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() throws FeatureStoreRuntimeException {
        throw new FeatureStoreRuntimeException("Can not remove from a feature reader.");
    }

    public static ShapefileFeatureReader create(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader,
            final FeatureType schema, final Hints hints) throws MismatchedFeatureException {
        final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);
        if (detached == null || detached) {
            //default behavior, make separate features
            return new DefaultSeparateFeatureReader(attributeReader, fidReader, schema);
        } else {
            //reuse same feature
            return new DefaultReuseFeatureReader(attributeReader, fidReader, schema);
        }
    }

    private static class DefaultSeparateFeatureReader extends ShapefileFeatureReader {

        protected final FeatureBuilder builder;

        private DefaultSeparateFeatureReader(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader,
                final FeatureType schema) throws MismatchedFeatureException {
            super(attributeReader, fidReader, schema);

            this.builder = new FeatureBuilder(schema);
        }

        @Override
        protected Feature readFeature() throws DataStoreException {

            final String fid = fidReader.next();
            builder.reset();
            try {
                attributeReader.read(buffer);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }

            //set crs on geometry
            if(buffer[0] instanceof Geometry){
                JTS.setCRS((Geometry)buffer[0], geomCRS);
            }

            if (attributIndexes.length == 0) {
                builder.addAll(buffer);
            } else {
                for (int i = 0; i < attributIndexes.length; i++) {
                    if (attributIndexes[i] >= 0) {
                        builder.setPropertyValue(attributIndexes[i], buffer[i]);
                    }
                }
            }

            return builder.buildFeature(fid);
        }
    }

    private static class DefaultReuseFeatureReader extends ShapefileFeatureReader {

        protected final DefaultSimpleFeature feature;

        private DefaultReuseFeatureReader(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader,
                final FeatureType schema) throws MismatchedFeatureException {
            super(attributeReader, fidReader, schema);

            feature = new DefaultSimpleFeature((SimpleFeatureType) schema, null, new Object[schema.getDescriptors().size()], false);
        }

        @Override
        protected Feature readFeature() throws DataStoreException {

            final String fid = fidReader.next();
            feature.setId(fid);

            try {
                attributeReader.read(buffer);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }

            //set crs on geometry
            if(buffer[0] instanceof Geometry){
                JTS.setCRS((Geometry)buffer[0], geomCRS);
            }

            if (attributIndexes.length == 0) {
                feature.setAttributes(buffer);
            } else {
                for (int i = 0; i < attributIndexes.length; i++) {
                    if (attributIndexes[i] >= 0) {
                        feature.setAttribute(attributIndexes[i], buffer[i]);
                    }
                }
            }

            return feature;
        }
    }

    private static AttributeDescriptor[] getDescriptors(final ShapefileAttributeReader reader) {
        final PropertyDescriptor[] vals = reader.getPropertyDescriptors();
        final AttributeDescriptor[] atts = new AttributeDescriptor[vals.length];
        for (int i = 0; i < vals.length; i++) {
            atts[i] = (AttributeDescriptor) vals[i];
        }
        return atts;
    }
}
