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
import java.util.NoSuchElementException;
import java.util.logging.Level;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.factory.Hints;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyNotFoundException;
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
    protected boolean generateId = false;
    /**
     * if the attributs between reader and schema are the same but not in the same order.
     */
    protected final String[] attributIndexes;
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
        }

        try {
            schema.getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            generateId = true;
        } catch (PropertyNotFoundException ex){}


        //check if the attributs are mixed
        final AttributeType[] readerAtt = getDescriptors(attributeReader);
        attributIndexes = new String[readerAtt.length];
        for(int i=0; i<readerAtt.length; i++){
            try{
                final String name = readerAtt[i].getName().toString();
                schema.getProperty(readerAtt[i].getName().toString());
                attributIndexes[i] = name;
            }catch(PropertyNotFoundException ex){
                //property does not exist in output type
            }
        }

        this.schema = schema;
        this.buffer = new Object[attributeReader.getPropertyCount()];

        geomCRS = FeatureExt.getCRS(schema);

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
            Logging.getLogger("org.geotoolkit.data.shapefile").warning(
                    "UNCLOSED ITERATOR : There is code leaving simple feature reader open, "
                    + "this may cause memory leaks or data integrity problems !");
            if (creationStack != null) {
                Logging.getLogger("org.geotoolkit.data.shapefile").log(Level.WARNING,
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
        b.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        for(AttributeType at : getDescriptors(attributeReader)){
            b.addAttribute(at);
        }
        return b.build();
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
        return new DefaultSeparateFeatureReader(attributeReader, fidReader, schema);
    }

    private static class DefaultSeparateFeatureReader extends ShapefileFeatureReader {

        private DefaultSeparateFeatureReader(final ShapefileAttributeReader attributeReader, final FeatureIDReader fidReader,
                final FeatureType schema) throws MismatchedFeatureException {
            super(attributeReader, fidReader, schema);
        }

        @Override
        protected Feature readFeature() throws DataStoreException {

            final Feature feature = schema.newInstance();
            final String fid = fidReader.next();
            if(generateId){
                feature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), fid);
            }
            try {
                attributeReader.read(buffer);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }

            //set crs on geometry
            if(buffer[0] instanceof Geometry){
                JTS.setCRS((Geometry)buffer[0], geomCRS);
            }

            for (int i = 0; i < attributIndexes.length; i++) {
                if (attributIndexes[i]!=null) {
                    feature.setPropertyValue(attributIndexes[i], buffer[i]);
                }
            }

            return feature;
        }
    }

    private static AttributeType[] getDescriptors(final ShapefileAttributeReader reader) {
        return reader.getPropertyDescriptors();
    }
}
