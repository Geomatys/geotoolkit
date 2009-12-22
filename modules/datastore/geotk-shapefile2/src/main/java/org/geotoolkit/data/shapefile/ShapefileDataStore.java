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

package org.geotoolkit.data.shapefile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DefaultFeatureIDReader;
import org.geotoolkit.data.DefaultSimpleFeatureReader;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.dbf.DbaseFileException;
import org.geotoolkit.data.shapefile.dbf.DbaseFileHeader;
import org.geotoolkit.data.shapefile.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.shapefile.ShpFileType.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShapefileDataStore extends AbstractDataStore{

    // This is the default character as specified by the DBF specification
    public static final Charset DEFAULT_STRING_CHARSET = DbaseFileReader.DEFAULT_STRING_CHARSET;

    protected final ShpFiles shpFiles;
    protected final Name name;
    protected final SimpleFeatureType schema;
    protected final boolean useMemoryMappedBuffer;
    protected final Charset dbfCharset;

    /**
     * This sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     * @param useMemoryMapped : default is true
     * @param dbfCharset : default is ShapefileDataStore.DEFAULT_STRING_CHARSET
     */
    public ShapefileDataStore(URL url, URI namespace, boolean useMemoryMapped,
            Charset dbfCharset) throws MalformedURLException, DataStoreException {
        shpFiles = new ShpFiles(url);

        if (!shpFiles.isLocal() || !shpFiles.exists(SHP)) {
            this.useMemoryMappedBuffer = false;
        } else {
            this.useMemoryMappedBuffer = useMemoryMapped;
        }

        if(namespace != null){
            this.schema = buildSchema(namespace.toString());
        }else{
            this.schema = buildSchema(null);
        }
        
        this.name = schema.getName();

        this.dbfCharset = dbfCharset;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        return Collections.singleton(this.name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema(Name typeName) throws DataStoreException {
        typeCheck(name);

        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final String typeName = query.getTypeName().getLocalPart();
        final String[] propertyNames = query.getPropertyNames();
        final String defaultGeomName = schema.getGeometryDescriptor().getLocalName();

        // gather attributes needed by the query tool, they will be used by the
        // query filter
        final FilterAttributeExtractor extractor = new FilterAttributeExtractor();
        final Filter filter = query.getFilter();
        filter.accept(extractor, null);
        final String[] filterAttnames = extractor.getAttributeNames();

        // check if the geometry is the one and only attribute needed
        // to return attribute _and_ to run the query filter
        if ((propertyNames != null)
                && (propertyNames.length == 1)
                && propertyNames[0].equals(defaultGeomName)
                && (filterAttnames.length == 0 || (filterAttnames.length == 1 && filterAttnames[0]
                        .equals(defaultGeomName)))) {
            try {
                final SimpleFeatureType newSchema = FeatureTypeUtilities.createSubType(
                        schema, propertyNames);

                return createFeatureReader(typeName,getAttributesReader(false), newSchema);
            } catch (SchemaException se) {
                throw new DataStoreException("Error creating schema", se);
            }
        }

        try {
            return createFeatureReader(typeName,getAttributesReader(true), schema);
        } catch (SchemaException se) {
            throw new DataStoreException("Error creating schema", se);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        typeCheck(typeName);

        final ShapefileAttributeReader attReader = getAttributesReader(true);
        FeatureReader<SimpleFeatureType, SimpleFeature> featureReader;
        try {
            featureReader = createFeatureReader(name.getLocalPart(), attReader, schema);

        } catch (Exception e) {
            featureReader = GenericEmptyFeatureIterator.createReader(schema);
        }
        try {
            return new ShapefileFeatureWriter(name.getLocalPart(), shpFiles, attReader, featureReader, dbfCharset);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Can not create shapefile schema.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Can not update shapefile schema.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        throw new DataStoreException("Can not delete shapefile schema.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // utils ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public void typeCheck(Name candidate) throws DataStoreException{
        if(!this.name.equals(name)){
            throw new DataStoreException("Type name : " + candidate +" don't exist in this datastore, available names are : " + name);
        }
    }

    /**
     * Obtain the FeatureType of the given name. ShapefileDataStore contains
     * only one FeatureType.
     *
     * @return The FeatureType that this DataStore contains.
     * @throws IOException If a type by the requested name is not present.
     */
    private SimpleFeatureType buildSchema(String namespace) throws DataStoreException {

        final List<AttributeDescriptor> types = readAttributes();
        final GeometryDescriptor geomDescriptor = (GeometryDescriptor) types.get(0);
        final Class<?> geomBinding = geomDescriptor.getType().getBinding();

        SimpleFeatureType parent = null;
        if ((geomBinding == Point.class) || (geomBinding == MultiPoint.class)) {
            parent = BasicFeatureTypes.POINT;
        } else if ((geomBinding == Polygon.class)
                || (geomBinding == MultiPolygon.class)) {
            parent = BasicFeatureTypes.POLYGON;
        } else if ((geomBinding == LineString.class)
                || (geomBinding == MultiLineString.class)) {
            parent = BasicFeatureTypes.LINE;
        }

        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setDefaultGeometry(geomDescriptor.getLocalName());
        builder.addAll(types);

        //configure the name
        final String local = shpFiles.getTypeName();
        if (namespace == null) {
            namespace = BasicFeatureTypes.DEFAULT_NAMESPACE;
        }

        builder.setName(namespace,local);
        builder.setAbstract(false);
        if (parent != null) {
            builder.setSuperType(parent);
        }

        return builder.buildFeatureType();
    }

    /**
     * Create the AttributeDescriptor contained within this DataStore.
     *
     * @return List of new AttributeDescriptor
     * @throws DataStoreException If AttributeType reading fails
     */
    protected List<AttributeDescriptor> readAttributes() throws DataStoreException {
        final ShapefileReader shp = openShapeReader();
        final DbaseFileReader dbf = openDbfReader();

        CoordinateReferenceSystem crs = null;
        InputStream prjStream = null;
        try{
             prjStream = shpFiles.getInputStream(PRJ, new Object());
            crs = PrjFiles.read(prjStream, true);
        }catch(IOException ex){
            //there might not be any prj file
            crs = null;
        }finally{
            if(prjStream!= null){
                try {
                    prjStream.close();
                } catch (IOException ex) {
                    //we tryed
                }
            }
        }


        final AttributeTypeBuilder buildAtt = new AttributeTypeBuilder();
        final AttributeDescriptorBuilder buildDesc = new AttributeDescriptorBuilder();
        final List<AttributeDescriptor> attributes = new ArrayList<AttributeDescriptor>();

        try {
            final Class<?> geometryClass = shp.getHeader().getShapeType().bestJTSClass();
            buildAtt.setName(Classes.getShortName(geometryClass));
            buildAtt.setCRS(crs);
            buildAtt.setBinding(geometryClass);

            buildDesc.setNillable(true);
            buildDesc.setName(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME);
            buildDesc.setType(buildAtt.buildGeometryType());

            attributes.add(buildDesc.buildDescriptor());

            // record names in case of duplicates
            final Set<String> usedNames = new HashSet<String>();
            usedNames.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME);

            // take care of the case where no dbf and query wants all =>
            // geometry only
            if (dbf != null) {
                final DbaseFileHeader header = dbf.getHeader();
                for (int i=0, n=header.getNumFields(); i<n; i++) {
                    final Class attributeClass = header.getFieldClass(i);
                    final int length = header.getFieldLength(i);
                    String name = header.getFieldName(i);
                    if (usedNames.contains(name)) {
                        final String origional = name;
                        int count = 1;
                        name = name + count;
                        while (usedNames.contains(name)) {
                            count++;
                            name = origional + count;
                        }
                    }
                    usedNames.add(name);

                    buildAtt.reset();
                    buildAtt.setName(name);
                    buildAtt.setBinding(attributeClass);
                    buildAtt.setLength(length);

                    buildDesc.reset();
                    buildDesc.setName(name);
                    buildDesc.setNillable(true);
                    buildDesc.setType(buildAtt.buildType());

                    attributes.add(buildDesc.buildDescriptor());
                }
            }
            return attributes;
        } finally {

            try {
                if (dbf != null) {
                    dbf.close();
                }
            } catch (IOException ioe) {
                // do nothing
            }
            try {
                if (shp != null) {
                    shp.close();
                }
            } catch (IOException ioe) {
                // do nothing
            }
        }
    }

    /**
     * Returns the attribute reader, allowing for a pure shapefile reader, or a
     * combined dbf/shp reader.
     *
     * @param readDbf -
     *                if true, the dbf fill will be opened and read
     *
     *
     * @throws IOException
     */
    protected ShapefileAttributeReader getAttributesReader(boolean readDbf)
            throws DataStoreException {

        if (!readDbf) {
            getLogger().fine("The DBF file won't be opened since no attributes will be read from it");
            final AttributeDescriptor[] desc = new AttributeDescriptor[]{schema.getGeometryDescriptor()};
            return new ShapefileAttributeReader(desc, openShapeReader(), null);
        }

        List<AttributeDescriptor> atts = (schema == null) ? readAttributes() : schema.getAttributeDescriptors();
        return new ShapefileAttributeReader(atts, openShapeReader(), openDbfReader());
    }

    protected DefaultSimpleFeatureReader createFeatureReader(String typeName,
            ShapefileAttributeReader reader, SimpleFeatureType readerSchema)
            throws SchemaException {

        return new org.geotoolkit.data.DefaultSimpleFeatureReader(reader,
                new DefaultFeatureIDReader(typeName), readerSchema);
    }

    /**
     * Convenience method for opening a ShapefileReader.
     *
     * @return A new ShapefileReader.
     * @throws IOException If an error occurs during creation.
     */
    protected ShapefileReader openShapeReader() throws DataStoreException {
        try {
            return new ShapefileReader(shpFiles, true, useMemoryMappedBuffer);
        } catch (IOException se) {
            throw new DataStoreException("Error creating ShapefileReader", se);
        }
    }

    /**
     * Convenience method for opening a DbaseFileReader.
     *
     * @return A new DbaseFileReader
     * @throws IOException If an error occurs during creation.
     */
    protected DbaseFileReader openDbfReader() throws DataStoreException {

        if (shpFiles.get(ShpFileType.DBF) == null) {
            return null;
        }

        if (shpFiles.isLocal() && !shpFiles.exists(DBF)) {
            return null;
        }

        try {
            return new DbaseFileReader(shpFiles, useMemoryMappedBuffer, dbfCharset);
        } catch (IOException e) {
            // could happen if dbf file does not exist
            return null;
        }
    }


    /**
     * Attempt to create a DbaseFileHeader for the FeatureType. Note, we cannot
     * set the number of records until the write has completed.
     *
     * @param featureType
     *                DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException
     *                 DOCUMENT ME!
     * @throws DbaseFileException
     *                 DOCUMENT ME!
     */
    protected static DbaseFileHeader createDbaseHeader(SimpleFeatureType featureType)
            throws IOException,DbaseFileException {

        final DbaseFileHeader header = new DbaseFileHeader();

        for (int i=0, n=featureType.getAttributeCount(); i<n; i++) {
            final AttributeDescriptor type = featureType.getDescriptor(i);
            final Class<?> colType = type.getType().getBinding();
            final String colName = type.getLocalName();

            int fieldLen = FeatureTypeUtilities.getFieldLength(type);
            if (fieldLen == FeatureTypeUtilities.ANY_LENGTH)
                fieldLen = 255;
            if ((colType == Integer.class) || (colType == Short.class)
                    || (colType == Byte.class)) {
                header.addColumn(colName, 'N', Math.min(fieldLen, 9), 0);
            } else if (colType == Long.class) {
                header.addColumn(colName, 'N', Math.min(fieldLen, 19), 0);
            } else if (colType == BigInteger.class) {
                header.addColumn(colName, 'N', Math.min(fieldLen, 33), 0);
            } else if (Number.class.isAssignableFrom(colType)) {
                int l = Math.min(fieldLen, 33);
                int d = Math.max(l - 2, 0);
                header.addColumn(colName, 'N', l, d);
            } else if (java.util.Date.class.isAssignableFrom(colType)) {
                header.addColumn(colName, 'D', fieldLen, 0);
            } else if (colType == Boolean.class) {
                header.addColumn(colName, 'L', 1, 0);
            } else if (CharSequence.class.isAssignableFrom(colType)) {
                // Possible fix for GEOT-42 : ArcExplorer doesn't like 0 length
                // ensure that maxLength is at least 1
                header.addColumn(colName, 'C', Math.min(254, fieldLen), 0);
            } else if (Geometry.class.isAssignableFrom(colType)) {
                continue;
            } else {
                throw new IOException("Unable to write : " + colType.getName());
            }
        }

        return header;
    }

}
