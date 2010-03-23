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

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.DefaultFeatureIDReader;
import org.geotoolkit.data.DefaultSimpleFeatureReader;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.shapefile.dbf.DbaseFileException;
import org.geotoolkit.data.shapefile.dbf.DbaseFileHeader;
import org.geotoolkit.data.shapefile.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.shp.IndexFile;
import org.geotoolkit.data.shapefile.shp.ShapeType;
import org.geotoolkit.data.shapefile.shp.ShapefileHeader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.shapefile.ShpFileType.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileDataStore extends AbstractDataStore{

    // This is the default character as specified by the DBF specification
    public static final Charset DEFAULT_STRING_CHARSET = DbaseFileReader.DEFAULT_STRING_CHARSET;

    private final QueryCapabilities capabilities = new DefaultQueryCapabilities(false);
    protected final ShpFiles shpFiles;
    protected final String namespace;
    protected final boolean useMemoryMappedBuffer;
    protected final Charset dbfCharset;
    private Name name;
    private SimpleFeatureType schema;


    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param url The URL of the shp file to use for this DataSource.
     *
     * @throws NullPointerException DOCUMENT ME!
     * @throws DataStoreException If computation of related URLs (dbf,shx) fails.
     */
    public ShapefileDataStore(URL url) throws DataStoreException,MalformedURLException {
        this(url, false, DEFAULT_STRING_CHARSET);
    }

    public ShapefileDataStore(URL url, boolean useMemoryMappedBuffer)
            throws DataStoreException,MalformedURLException {
        this(url, useMemoryMappedBuffer, DEFAULT_STRING_CHARSET);
    }

    public ShapefileDataStore(URL url, boolean useMemoryMappedBuffer, Charset dbfCharset)
            throws DataStoreException,MalformedURLException {
        this(url, null, false, dbfCharset);
    }

    /**
     * this sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     */
    public ShapefileDataStore(URL url, String namespace)
            throws DataStoreException,MalformedURLException {
        this(url, namespace, false, DEFAULT_STRING_CHARSET);
    }

    /**
     * this sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     * @param useMemoryMapped
     */
    public ShapefileDataStore(URL url, String namespace, boolean useMemoryMapped)
            throws DataStoreException,MalformedURLException {
        this(url, namespace, useMemoryMapped, DEFAULT_STRING_CHARSET);
    }

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
    public ShapefileDataStore(URL url, String namespace, boolean useMemoryMapped,
            Charset dbfCharset) throws MalformedURLException, DataStoreException {
        shpFiles = new ShpFiles(url);

        if (!shpFiles.isLocal() || !shpFiles.exists(SHP)) {
            this.useMemoryMappedBuffer = false;
        } else {
            this.useMemoryMappedBuffer = useMemoryMapped;
        }

        if(namespace != null){
            this.namespace = namespace.toString();
        }else{
            this.namespace = "http://geotoolkit.org";
        }

        this.dbfCharset = dbfCharset;
    }

    @Override
    public boolean isWritable(Name typeName) throws DataStoreException {
        return shpFiles.isLocal();
    }

    public Name getName() throws DataStoreException{
        checkTypeExist();
        return name;
    }

    public SimpleFeatureType getFeatureType() throws DataStoreException{
        checkTypeExist();
        return schema;
    }

    private void checkTypeExist() throws DataStoreException{
        if(name != null && schema != null) return;

        if(namespace != null){
            this.schema = buildSchema(namespace);
        }else{
            this.schema = buildSchema("http://geotoolkit.org");
        }

        this.name = schema.getName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        return Collections.singleton(getName());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        typeCheck(typeName);

        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return capabilities;
    }

    /**
     * Gets the bounding box of the file represented by this data store as a
     * whole (that is, off all of the features in the shapefile)
     *
     * @return The bounding box of the datasource or null if unknown and too
     *         expensive for the method to calculate.
     * @throws DataSourceException DOCUMENT ME!
     */
    @Override
    public Envelope getEnvelope(Query query) throws DataStoreException, DataStoreRuntimeException {
        typeCheck(query.getTypeName());

        if(QueryUtilities.queryAll(query)){

            // This is way quick!!!
            ReadableByteChannel in = null;

            try {
                final ByteBuffer buffer = ByteBuffer.allocate(100);

                in = shpFiles.getReadChannel(SHP, "Shapefile Datastore's getBounds Method");
                try {
                    in.read(buffer);
                    buffer.flip();

                    final ShapefileHeader header = ShapefileHeader.read(buffer, true);
                    final JTSEnvelope2D bounds = new JTSEnvelope2D(schema.getCoordinateReferenceSystem());
                    bounds.include(header.minX(), header.minY());
                    bounds.include(header.minX(), header.minY());

                    final com.vividsolutions.jts.geom.Envelope env =
                            new com.vividsolutions.jts.geom.Envelope(header.minX(), header.maxX(), header.minY(), header.maxY());

                    if (schema != null) {
                        return new JTSEnvelope2D(env, schema.getCoordinateReferenceSystem());
                    }
                    return new JTSEnvelope2D(env, null);
                } finally {
                    in.close();
                }

            } catch (IOException ioe) {
                // What now? This seems arbitrarily appropriate !
                throw new DataStoreException("Problem getting Bbox", ioe);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }else{
            return super.getEnvelope(query);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final Hints hints = query.getHints();
        final String typeName = query.getTypeName().getLocalPart();
        final Name[] propertyNames = query.getPropertyNames();
        final Name defaultGeomName = schema.getGeometryDescriptor().getName();

        if (query.getSortBy() != null) {
            throw new DataStoreException("The ShapeFIleDatastore does not support sortby query");
        }
        // gather attributes needed by the query tool, they will be used by the
        // query filter
        final FilterAttributeExtractor extractor = new FilterAttributeExtractor();
        final Filter filter = query.getFilter();
        filter.accept(extractor, null);
        final Name[] filterAttnames = extractor.getAttributeNames();

        // check if the geometry is the one and only attribute needed
        // to return attribute _and_ to run the query filter
        if (   propertyNames != null
            && propertyNames.length == 1
            && propertyNames[0].getLocalPart().equals(defaultGeomName.getLocalPart())
            && (filterAttnames.length == 0 || (filterAttnames.length == 1 && filterAttnames[0].getLocalPart()
                        .equals(defaultGeomName.getLocalPart())))) {
            try {
                final SimpleFeatureType newSchema = FeatureTypeUtilities.createSubType(
                        schema, propertyNames);

                FeatureReader reader = createFeatureReader(typeName,getAttributesReader(false), newSchema,hints);
                QueryBuilder query2 = new QueryBuilder(query.getTypeName());
                query2.setProperties(query.getPropertyNames());
                query2.setFilter(query.getFilter());
                query2.setHints(query.getHints());
                reader = handleRemaining(reader, query2.buildQuery());

                return reader;
            } catch (SchemaException se) {
                throw new DataStoreException("Error creating schema", se);
            }
        }else{
             try {
                final SimpleFeatureType newSchema;
                if (propertyNames != null) {
                    newSchema = FeatureTypeUtilities.createSubType(schema, propertyNames);
                } else {
                    newSchema = schema;
                }

                FeatureReader reader = createFeatureReader(typeName,getAttributesReader(true), newSchema, hints);
                QueryBuilder query2 = new QueryBuilder(query.getTypeName());
                query2.setProperties(query.getPropertyNames());
                query2.setFilter(query.getFilter());
                query2.setHints(query.getHints());
                reader = handleRemaining(reader, query2.buildQuery());

                return reader;
            } catch (SchemaException se) {
                throw new DataStoreException("Error creating schema", se);
            }
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
            featureReader = createFeatureReader(typeName.getLocalPart(), attReader, schema,null);

        } catch (Exception e) {
            featureReader = GenericEmptyFeatureIterator.createReader(schema);
        }
        try {
            return new ShapefileFeatureWriter(typeName.getLocalPart(), shpFiles, attReader, featureReader, dbfCharset);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Set the FeatureType of this DataStore. This method will delete any
     * existing local resources or throw an IOException if the DataStore is
     * remote.
     *
     * @param featureType The desired FeatureType.
     * @throws DataStoreException If the DataStore is remote.
     *
     * @todo must synchronize this properly
     */
    @Override
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        if (!shpFiles.isLocal()) {
            throw new DataStoreException("Cannot create FeatureType on remote shapefile");
        }

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(!(featureType instanceof SimpleFeatureType)){
            throw new DataStoreException("Feature type must not be null and must be a simple feature type.");
        }

        if(!featureType.getName().equals(typeName)){
            throw new DataStoreException("Shapefile datastore can only hold typename same as feature type name.");
        }


        //delete the files
        shpFiles.delete();

        //update schema and name
        name = typeName;
        schema = (SimpleFeatureType) featureType;

        CoordinateReferenceSystem crs = featureType.getGeometryDescriptor().getCoordinateReferenceSystem();
        final Class<?> geomType = featureType.getGeometryDescriptor().getType().getBinding();
        final ShapeType shapeType =ShapeType.findBestGeometryType(geomType);

        if(shapeType == ShapeType.UNDEFINED){
            throw new DataStoreException("Cannot create a shapefile whose geometry type is "+ geomType);
        }

        try{
            final StorageFile shpStoragefile = shpFiles.getStorageFile(SHP);
            final StorageFile shxStoragefile = shpFiles.getStorageFile(SHX);
            final StorageFile dbfStoragefile = shpFiles.getStorageFile(DBF);
            final StorageFile prjStoragefile = shpFiles.getStorageFile(PRJ);

            final FileChannel shpChannel = shpStoragefile.getWriteChannel();
            final FileChannel shxChannel = shxStoragefile.getWriteChannel();

            final ShapefileWriter writer = new ShapefileWriter(shpChannel, shxChannel);
            try {
                // try to get the domain first
                final org.opengis.geometry.Envelope domain = CRS.getEnvelope(crs);
                if (domain != null) {
                    writer.writeHeaders(new JTSEnvelope2D(domain), shapeType, 0, 100);
                } else {
                    // try to reproject the single overall envelope keeping poles out of the way
                    final JTSEnvelope2D env = new JTSEnvelope2D(-179, 179, -89, 89, DefaultGeographicCRS.WGS84);
                    JTSEnvelope2D transformedBounds;
                    if (crs != null) {
                        try {
                            transformedBounds = env.transform(crs, true);
                        } catch (Throwable t) {
                            if (getLogger().isLoggable(Level.WARNING)) {
                                getLogger().log(Level.WARNING, t.getLocalizedMessage(), t);
                            }
                            transformedBounds = env;
                            crs = null;
                        }
                    } else {
                        transformedBounds = env;
                    }

                    writer.writeHeaders(transformedBounds, shapeType, 0, 100);
                }
            } finally {
                writer.close();
                assert !shpChannel.isOpen();
                assert !shxChannel.isOpen();
            }

            final DbaseFileHeader dbfheader = createDbaseHeader(schema);
            dbfheader.setNumRecords(0);

            final WritableByteChannel dbfChannel = dbfStoragefile.getWriteChannel();
            try {
                dbfheader.writeHeader(dbfChannel);
            } finally {
                dbfChannel.close();
            }

            if (crs != null) {
                // .prj files should have no carriage returns in them, this messes up
                // ESRI's ArcXXX software, so we'll be compatible
                final String s = crs.toWKT().replaceAll("\n", "").replaceAll("  ", "");
                final FileWriter prjWriter = new FileWriter(prjStoragefile.getFile());
                try {
                    prjWriter.write(s);
                } finally {
                    prjWriter.close();
                }
            } else {
                getLogger().warning("PRJ file not generated for null CoordinateReferenceSystem");
            }

            StorageFile.replaceOriginals(shpStoragefile, shxStoragefile, dbfStoragefile, prjStoragefile);
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }
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

    /**
     * Obtain the FeatureType of the given name. ShapefileDataStore contains
     * only one FeatureType.
     *
     * @return The FeatureType that this DataStore contains.
     * @throws IOException If a type by the requested name is not present.
     */
    private SimpleFeatureType buildSchema(String namespace) throws DataStoreException {

        final List<AttributeDescriptor> types = readAttributes(namespace);
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

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.addAll(types);
        builder.setDefaultGeometry(geomDescriptor.getLocalName());

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

        return builder.buildSimpleFeatureType();
    }

    /**
     * Create the AttributeDescriptor contained within this DataStore.
     *
     * @return List of new AttributeDescriptor
     * @throws DataStoreException If AttributeType reading fails
     */
    protected List<AttributeDescriptor> readAttributes(String namespace) throws DataStoreException {
        final ShapefileReader shp = openShapeReader();
        final DbaseFileReader dbf = openDbfReader();

        CoordinateReferenceSystem crs = null;
        ReadableByteChannel channel = null;

        try{
            channel = shpFiles.getReadChannel(PRJ, new Object());
            crs = PrjFiles.read(channel, true);
        }catch(IOException ex){
            crs = null;
        }finally{
            //todo replace by ARM in JDK 1.7
            if(channel!= null){
                try {
                    channel.close();
                } catch (IOException ex) {
                    getLogger().log(Level.WARNING, "failed to close pro channel.",ex);
                }
            }
        }


        final AttributeTypeBuilder buildAtt = new AttributeTypeBuilder();
        final AttributeDescriptorBuilder buildDesc = new AttributeDescriptorBuilder();
        final List<AttributeDescriptor> attributes = new ArrayList<AttributeDescriptor>();

        try {
            final Class<?> geometryClass = shp.getHeader().getShapeType().bestJTSClass();
            buildAtt.setName(ensureGMLNS(namespace, Classes.getShortName(geometryClass)));
            buildAtt.setCRS(crs);
            buildAtt.setBinding(geometryClass);

            buildDesc.setNillable(true);
            buildDesc.setName(ensureGMLNS(namespace, BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME));
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
                    buildAtt.setName(ensureGMLNS(namespace, name));
                    buildAtt.setBinding(attributeClass);
                    buildAtt.setLength(length);

                    buildDesc.reset();
                    buildDesc.setName(ensureGMLNS(namespace, name));
                    buildDesc.setNillable(true);
                    buildDesc.setType(buildAtt.buildType());

                    attributes.add(buildDesc.buildDescriptor());
                }
            }
            return attributes;
        } finally {
            //todo replace by ARM in JDK 1.7
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
     * @param readDbf - if true, the dbf fill will be opened and read
     * @throws IOException
     */
    protected ShapefileAttributeReader getAttributesReader(boolean readDbf)
            throws DataStoreException {

        if (!readDbf) {
            getLogger().fine("The DBF file won't be opened since no attributes will be read from it");
            final AttributeDescriptor[] desc = new AttributeDescriptor[]{schema.getGeometryDescriptor()};
            return new ShapefileAttributeReader(desc, openShapeReader(), null);
        }

        List<AttributeDescriptor> atts = (schema == null) ? readAttributes(namespace) : schema.getAttributeDescriptors();
        return new ShapefileAttributeReader(atts, openShapeReader(), openDbfReader());
    }

    protected DefaultSimpleFeatureReader createFeatureReader(String typeName,
            ShapefileAttributeReader reader, SimpleFeatureType readerSchema, Hints hints)
            throws SchemaException {

        return DefaultSimpleFeatureReader.create(reader, new DefaultFeatureIDReader(typeName), readerSchema, hints);
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
     * Convenience method for opening an index file.
     *
     * @return An IndexFile
     * @throws IOException
     */
    protected IndexFile openIndexFile() throws IOException {
        if (shpFiles.get(SHX) == null) {
            return null;
        }

        if (shpFiles.isLocal() && !shpFiles.exists(SHX)) {
            return null;
        }

        try {
            return new IndexFile(shpFiles, this.useMemoryMappedBuffer);
        } catch (IOException e) {
            // could happen if shx file does not exist remotely
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
