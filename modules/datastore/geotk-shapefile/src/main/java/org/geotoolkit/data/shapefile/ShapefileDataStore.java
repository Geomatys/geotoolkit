/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.data.shapefile.lock.StorageFile;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.io.FileWriter;
import java.io.IOException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.dbf.DbaseFileHeader;
import org.geotoolkit.data.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.data.shapefile.shp.ShapeType;
import org.geotoolkit.data.shapefile.shp.ShapefileHeader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileWriter;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

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

import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;

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
    public ShapefileDataStore(final URL url) throws DataStoreException,MalformedURLException {
        this(url, null);
    }

    /**
     * this sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     */
    public ShapefileDataStore(final URL url, final String namespace)
            throws DataStoreException,MalformedURLException {
        this(url, namespace, false, DEFAULT_STRING_CHARSET);
    }
    
    /**
     * This sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     * @param useMemoryMapped : default is true
     * @param dbfCharset : if null default will be ShapefileDataStore.DEFAULT_STRING_CHARSET
     */
    public ShapefileDataStore(final URL url, final String namespace, final boolean useMemoryMapped,
            Charset dbfCharset) throws MalformedURLException, DataStoreException {
        super(namespace);
        shpFiles = new ShpFiles(url);

        if(dbfCharset == null){
            dbfCharset = DEFAULT_STRING_CHARSET;
        }
        
        if (!shpFiles.isLocal() || !shpFiles.exists(SHP)) {
            this.useMemoryMappedBuffer = false;
        } else {
            this.useMemoryMappedBuffer = useMemoryMapped;
        }

        this.dbfCharset = dbfCharset;
    }

    @Override
    public boolean isWritable(final Name typeName) throws DataStoreException {
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

    private void checkTypeExist() throws DataStoreException {
        if (name != null && schema != null) {
            return;
        }
        this.schema = buildSchema(getDefaultNamespace());
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
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
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
    public Envelope getEnvelope(final Query query) throws DataStoreException, DataStoreRuntimeException {
        typeCheck(query.getTypeName());

        if(QueryUtilities.queryAll(query)){

            // This is way quick!!!
            ReadableByteChannel in = null;

            try {
                final ByteBuffer buffer = ByteBuffer.allocate(100);

                in = shpFiles.getReadChannel(SHP);
                try {
                    in.read(buffer);
                    buffer.flip();
                    final ShapefileHeader header = ShapefileHeader.read(buffer, true);

                    final com.vividsolutions.jts.geom.Envelope env =
                            new com.vividsolutions.jts.geom.Envelope(
                            header.minX(), header.maxX(), header.minY(), header.maxY());

                    if (schema != null) {
                        return new JTSEnvelope2D(env, schema.getCoordinateReferenceSystem());
                    }else{
                        return new JTSEnvelope2D(env, null);
                    }
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
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final Hints hints = query.getHints();
        final String typeName = query.getTypeName().getLocalPart();
        final Name[] propertyNames = query.getPropertyNames();
        final Name defaultGeomName = schema.getGeometryDescriptor().getName();
        final double[] resample = query.getResolution();

        //check if we must read the 3d values
        final CoordinateReferenceSystem reproject = query.getCoordinateSystemReproject();
        final boolean read3D = (reproject==null || (reproject != null && CRS.getVerticalCRS(reproject)!=null));

        if (query.getSortBy() != null) {
            throw new DataStoreException("The ShapeFileDatastore does not support sortby query");
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
                final SimpleFeatureType newSchema = (SimpleFeatureType) FeatureTypeUtilities.createSubType(
                        schema, propertyNames);
               
                final ShapefileAttributeReader attReader = getAttributesReader(false,read3D,resample);
                final FeatureIDReader idReader = new DefaultFeatureIDReader(typeName);
                FeatureReader reader = ShapefileFeatureReader.create(attReader, idReader, newSchema, hints);
                final QueryBuilder remaining = new QueryBuilder(query.getTypeName());
                remaining.setProperties(query.getPropertyNames());
                remaining.setFilter(query.getFilter());
                remaining.setHints(query.getHints());
                remaining.setCRS(query.getCoordinateSystemReproject());
                reader = handleRemaining(reader, remaining.buildQuery());

                return reader;
            } catch (SchemaException se) {
                throw new DataStoreException("Error creating schema", se);
            }
        }else{
             try {
                final SimpleFeatureType newSchema;
                if (propertyNames != null) {
                    newSchema = (SimpleFeatureType) FeatureTypeUtilities.createSubType(schema, propertyNames);
                } else {
                    newSchema = schema;
                }

                final ShapefileAttributeReader attReader = getAttributesReader(true,read3D,resample);
                final FeatureIDReader idReader = new DefaultFeatureIDReader(typeName);
                FeatureReader reader = ShapefileFeatureReader.create(attReader,idReader, newSchema, hints);
                QueryBuilder query2 = new QueryBuilder(query.getTypeName());
                query2.setProperties(query.getPropertyNames());
                query2.setFilter(query.getFilter());
                query2.setHints(query.getHints());
                query2.setCRS(query.getCoordinateSystemReproject());
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
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        typeCheck(typeName);

        final ShapefileAttributeReader attReader = getAttributesReader(true,true,null);
        final FeatureIDReader idReader = new DefaultFeatureIDReader(typeName.getLocalPart());
        FeatureReader<SimpleFeatureType, SimpleFeature> featureReader;
        try {
            featureReader = ShapefileFeatureReader.create(attReader,idReader, schema, hints);
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
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
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
        
        final AccessManager locker = shpFiles.createLocker();

        //update schema and name
        name = typeName;
        schema = (SimpleFeatureType) featureType;

        final GeometryDescriptor desc = featureType.getGeometryDescriptor();
        CoordinateReferenceSystem crs = null;
        final Class<?> geomType;
        final ShapeType shapeType;
        if(desc != null){
            crs = desc.getCoordinateReferenceSystem();
            geomType = desc.getType().getBinding();
            shapeType =ShapeType.findBestGeometryType(geomType);
        }else{
            geomType = null;
            shapeType = ShapeType.NULL;
        }
        
        if(shapeType == ShapeType.UNDEFINED){
            throw new DataStoreException("Cannot create a shapefile whose geometry type is "+ geomType);
        }

        try{
            final StorageFile shpStoragefile = locker.getStorageFile(SHP);
            final StorageFile shxStoragefile = locker.getStorageFile(SHX);
            final StorageFile dbfStoragefile = locker.getStorageFile(DBF);
            final StorageFile prjStoragefile = locker.getStorageFile(PRJ);

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

            final DbaseFileHeader dbfheader = DbaseFileHeader.createDbaseHeader(schema);
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

            locker.disposeReaderAndWriters();
            locker.replaceStorageFiles();
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Can not update shapefile schema.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
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
    private synchronized SimpleFeatureType buildSchema(final String namespace) throws DataStoreException {

        //read all attributes///////////////////////////////////////////////////
        final AccessManager locker = shpFiles.createLocker();
        
        final ShapefileReader shp;
        final DbaseFileReader dbf;
        try {
            shp = locker.getSHPReader(true, useMemoryMappedBuffer, true, null);
            dbf = locker.getDBFReader(useMemoryMappedBuffer, dbfCharset);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        CoordinateReferenceSystem crs = null;
        ReadableByteChannel channel = null;

        //read the projection
        try{
            channel = shpFiles.getReadChannel(PRJ);
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

        final GeometryDescriptor geomDescriptor;
        final List<AttributeDescriptor> attributes = new ArrayList<AttributeDescriptor>();

        try {
            //get the descriptor from shp
            geomDescriptor = shp.getHeader().createDescriptor(namespace, crs);
            attributes.add(geomDescriptor);
            
            //get dbf attributes if exist
            if (dbf != null) {
                final DbaseFileHeader header = dbf.getHeader();
                attributes.addAll(header.createDescriptors(namespace));
            }
        } finally {
            //we have finish readring what we want, dispose everything
            locker.disposeReaderAndWriters();
        }
        
        //create the feature type //////////////////////////////////////////////
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

        final FeatureTypeBuilder builder = new FeatureTypeBuilder(null,false);
        builder.setName(namespace,shpFiles.getTypeName());
        builder.setAbstract(false);
        if (parent != null) {
            builder.setSuperType(parent);
        }
        builder.addAll(attributes);
        builder.setDefaultGeometry(geomDescriptor.getLocalName());

        return builder.buildSimpleFeatureType();
    }

    /**
     * Returns the attribute reader, allowing for a pure shapefile reader, or a
     * combined dbf/shp reader.
     *
     * @param readDbf - if true, the dbf fill will be opened and read
     * @throws IOException
     */
    protected ShapefileAttributeReader getAttributesReader(final boolean readDbf, 
            final boolean read3D, final double[] resample) throws DataStoreException {

        final AccessManager locker = shpFiles.createLocker();
        final SimpleFeatureType schema = getFeatureType();

        final PropertyDescriptor[] descs;
        if(readDbf){
            final List<? extends PropertyDescriptor> lst = schema.getAttributeDescriptors();
            descs =  lst.toArray(new PropertyDescriptor[lst.size()]);
        }else{
            getLogger().fine("The DBF file won't be opened since no attributes will be read from it");
            descs = new PropertyDescriptor[]{schema.getGeometryDescriptor()};
        }
        try {
            return new ShapefileAttributeReader(locker, descs, read3D, 
                    useMemoryMappedBuffer,resample, readDbf, dbfCharset,null);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }
        
    ////////////////////////////////////////////////////////////////////////////
    //Fallback on iterative reader and writer //////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures, 
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

}
