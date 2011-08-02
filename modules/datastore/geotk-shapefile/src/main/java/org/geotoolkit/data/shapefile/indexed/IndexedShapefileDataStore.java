/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import com.vividsolutions.jts.geom.Envelope;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DefaultSimpleFeatureReader;
import org.geotoolkit.data.FeatureIDReader;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.shapefile.ShapefileDataStore;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.data.shapefile.ShpFileType;
import org.geotoolkit.data.dbf.IndexedDbaseFileReader;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader.Record;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.shapefile.ShpDBF;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.filter.visitor.ExtractBoundsFilterVisitor;
import org.geotoolkit.filter.visitor.IdCollectorFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.LockTimeoutException;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.DataReader;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;
import org.geotoolkit.index.quadtree.LazySearchCollection;
import org.geotoolkit.index.quadtree.LazyTyleSearchIterator;
import org.geotoolkit.index.rtree.RTree;
import org.geotoolkit.util.NullProgressListener;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.referencing.CRS;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.shapefile.ShpFileType.*;
import static org.geotoolkit.data.shapefile.ShpFiles.*;


/**
 * A DataStore implementation which allows reading and writing from Shapefiles.
 * 
 * @author Ian Schneider
 * @author Tommaso Nolli
 * @author jesse eichar
 * 
 * @module pending
 */
public class IndexedShapefileDataStore extends ShapefileDataStore {

    private static final Comparator<Identifier> IDENTIFIER_COMPARATOR = new Comparator<Identifier>(){
        @Override
        public int compare(Identifier o1, Identifier o2){
            return o1.toString().compareTo(o2.toString());
        }
    };

    IndexType treeType;
    final boolean useIndex;
    private RTree rtree;
    int maxDepth;

    /**
     * Creates a new instance of ShapefileDataStore.
     * 
     * @param url The URL of the shp file to use for this DataSource.
     */
    public IndexedShapefileDataStore(final URL url)
            throws MalformedURLException,DataStoreException {
        this(url, null, false, true, IndexType.QIX);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     * 
     * @param url The URL of the shp file to use for this DataSource.
     * @param namespace DOCUMENT ME!
     */
    public IndexedShapefileDataStore(final URL url, final String namespace)
            throws MalformedURLException,DataStoreException {
        this(url, namespace, false, true, IndexType.QIX);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     * 
     * @param url The URL of the shp file to use for this DataSource.
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     */
    public IndexedShapefileDataStore(final URL url, final boolean useMemoryMappedBuffer,
            final boolean createIndex) throws MalformedURLException,DataStoreException {
        this(url, null, useMemoryMappedBuffer, createIndex, IndexType.QIX);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     * 
     * @param url The URL of the shp file to use for this DataSource.
     * @param namespace DOCUMENT ME!
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     * @param treeType The type of index to use
     * 
     */
    public IndexedShapefileDataStore(final URL url, final String namespace, final boolean useMemoryMappedBuffer,
            final boolean createIndex, final IndexType treeType)
            throws MalformedURLException,DataStoreException {
        this(url, namespace, useMemoryMappedBuffer, createIndex, treeType, DEFAULT_STRING_CHARSET);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     * 
     * @param url The URL of the shp file to use for this DataSource.
     * @param namespace DOCUMENT ME!
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     * @param treeType The type of index used
     * @param dbfCharset {@link Charset} used to decode strings from the DBF
     * 
     * @throws MalformedURLException
     */
    public IndexedShapefileDataStore(final URL url, final String namespace, final boolean useMemoryMappedBuffer,
            final boolean createIndex, final IndexType treeType, final Charset dbfCharset)
            throws MalformedURLException,DataStoreException {
        super(url, namespace, useMemoryMappedBuffer, dbfCharset);

        this.treeType = treeType;
        this.useIndex = treeType != IndexType.NONE;
        maxDepth = -1;
        try {
            if (shpFiles.isLocal() && createIndex
                    && needsGeneration(treeType.shpFileType)) {
                createSpatialIndex();
            }
        } catch (IOException e) {
            this.treeType = IndexType.NONE;
            ShapefileDataStoreFactory.LOGGER.log(Level.WARNING, e
                    .getLocalizedMessage());
        }
        try {
            if (shpFiles.isLocal() && needsGeneration(FIX)) {
                generateFidIndex();
            }
        } catch (IOException e) {
            ShapefileDataStoreFactory.LOGGER.log(Level.WARNING, e
                    .getLocalizedMessage());
        }

    }

    /**
     * Forces the spatial index to be created
     */
    public final void createSpatialIndex() throws IOException {
        buildQuadTree(maxDepth);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (rtree != null) {
            try {
                rtree.close();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "org.geotoolkit.data.shapefile.indexed"
                        + ".IndexedShapeFileDataStore#finalize(): Error closing rtree. {0}", e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Use the spatial index if available and adds a small optimization: if no
     * attributes are going to be read, don't uselessly open and read the dbf
     * file.
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(final Query query)
            throws DataStoreException {
        final SimpleFeatureType originalSchema = getFeatureType();
        final Name              queryTypeName = query.getTypeName();
        final Name[]            queryPropertyNames = query.getPropertyNames();
        final SortBy[]          querySortBy = query.getSortBy();
        final Hints             queryHints = query.getHints();
        final double[]          queryRes = query.getResolution();
        Filter                  queryFilter = query.getFilter();

        //check if we must read the 3d values
        final CoordinateReferenceSystem reproject = query.getCoordinateSystemReproject();
        final boolean read3D = (reproject==null || (reproject != null && CRS.getVerticalCRS(reproject)!=null));

        if (queryFilter == Filter.EXCLUDE){
            return GenericEmptyFeatureIterator.createReader(originalSchema);
        }
        if (querySortBy != null && querySortBy.length > 0) {
            throw new DataStoreException("The ShapeFileDatastore does not support sortby query");
        }

        //find the properties we will read and return --------------------------
        List<PropertyDescriptor> readProperties;
        List<PropertyDescriptor> returnedProperties;

        if(queryPropertyNames == null){
            //return all properties
            readProperties = new ArrayList<PropertyDescriptor>(originalSchema.getDescriptors());
            returnedProperties = readProperties;
        }else{
            //return only a subset of properties
            returnedProperties = new ArrayList<PropertyDescriptor>(queryPropertyNames.length);
            for(Name n : queryPropertyNames){
                final AttributeDescriptor property = originalSchema.getDescriptor(n);
                if(property == null){
                    throw new DataStoreException("Query requieres property : "+ n +
                        " which is not present in feature type :\n"+ originalSchema);
                }
                returnedProperties.add(property);
            }

            final FilterAttributeExtractor fae = new FilterAttributeExtractor();
            queryFilter.accept(fae, null);
            final Set<Name> filterPropertyNames = fae.getAttributeNameSet();
            if(filterPropertyNames.isEmpty()){
                //filter do not requiere attributs
                readProperties = returnedProperties;
            }else{
                final Set<Name> attributes = new LinkedHashSet<Name>(filterPropertyNames);
                attributes.addAll(Arrays.asList(queryPropertyNames));
                readProperties = new ArrayList<PropertyDescriptor>(attributes.size());
                for(Name n : attributes){
                    final AttributeDescriptor property = originalSchema.getDescriptor(n);
                    if(property == null){
                        throw new DataStoreException("Query filter requieres property : "+ n +
                            " which is not present in feature type :\n"+ originalSchema);
                    }
                    readProperties.add(property);
                }
                //check if we read the same properties in different order
                if(readProperties.size()== returnedProperties.size() && readProperties.containsAll(returnedProperties)){
                    //we avoid a useless retype iterator
                    readProperties = returnedProperties;
                }

            }
        }

        //create a reader ------------------------------------------------------
        final SimpleFeatureType readSchema;
        final FeatureReader reader;
        try {
            final Name[] readPropertyNames = new Name[readProperties.size()];
            for(int i=0;i<readPropertyNames.length;i++){
                readPropertyNames[i] = readProperties.get(i).getName();
            }
            readSchema = (SimpleFeatureType)FeatureTypeUtilities.createSubType(originalSchema,readPropertyNames);
            
            if(queryFilter instanceof BBOX){
                //in case we have a BBOX filter only, which is very commun, we can speed
                //the process by relying on the quadtree estimations
                final Envelope bbox = (Envelope) queryFilter.accept(
                        ExtractBoundsFilterVisitor.BOUNDS_VISITOR, new JTSEnvelope2D());
                final boolean loose = (queryFilter instanceof LooseBBox);
                queryFilter = Filter.INCLUDE;
                reader = createFeatureReader(
                        getBBoxAttributesReader(readProperties, bbox, loose, queryHints,read3D,queryRes),
                        readSchema, queryHints);

            }else if(queryFilter instanceof Id && ((Id)queryFilter).getIdentifiers().isEmpty()){
                //in case we have an empty id set
                return GenericEmptyFeatureIterator.createReader(getFeatureType());

            }else{
                reader = createFeatureReader(
                    getAttributesReader(readProperties, queryFilter,read3D,queryRes),
                    readSchema, queryHints);
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        //handle remaining query parameters ------------------------------------
        final QueryBuilder qb = new QueryBuilder(queryTypeName);
        if(readProperties != returnedProperties){
            qb.setProperties(queryPropertyNames);
        }
        qb.setFilter(queryFilter);
        qb.setHints(queryHints);
        qb.setCRS(query.getCoordinateSystemReproject());
        return handleRemaining(reader, qb.buildQuery());
    }

    protected FeatureReader<SimpleFeatureType, SimpleFeature> createFeatureReader(
            final IndexedShapefileAttributeReader r, final SimpleFeatureType featureType, final Hints hints)
            throws SchemaException, IOException,DataStoreException {

        final FeatureIDReader fidReader;
        if (!indexUseable(FIX)) {
            fidReader = new ShapeFIDReader(getName().getLocalPart(), r);
        } else {
            fidReader = new IndexedFidReader(shpFiles, r);
        }

        return DefaultSimpleFeatureReader.create(r, fidReader, featureType, hints);
    }

    /**
     * Forces the FID index to be regenerated
     * 
     * @throws IOException
     */
    public final void generateFidIndex() throws IOException {
        IndexedFidWriter.generate(shpFiles);
    }

    private IndexedShapefileAttributeReader getAttributesReader(final List<? extends PropertyDescriptor> properties, 
            final Filter filter, final boolean read3D, final double[] resample) throws DataStoreException{


        CloseableCollection<ShpData> goodRecs = null;
        if (filter instanceof Id && shpFiles.isLocal() && shpFiles.exists(FIX)) {
            final Id fidFilter = (Id) filter;

            final TreeSet<Identifier> idsSet = new TreeSet<Identifier>(IDENTIFIER_COMPARATOR);
            idsSet.addAll(fidFilter.getIdentifiers());
            try {
                goodRecs = queryFidIndex(idsSet);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
        } else {
            // will be bbox.isNull() to start
            Envelope bbox = new JTSEnvelope2D();
            
            if (filter != null) {
                // Add additional bounds from the filter
                // will be null for Filter.EXCLUDES
                bbox = (Envelope) filter.accept(
                        ExtractBoundsFilterVisitor.BOUNDS_VISITOR, bbox);
                if (bbox == null) {
                    bbox = new JTSEnvelope2D();
                    // we hit Filter.EXCLUDES consider returning an empty
                    // reader?
                    // (however should simplify the filter to detect ff.not(
                    // fitler.EXCLUDE )
                }
            }

            if (!bbox.isNull() && this.useIndex) {
                try {
                    goodRecs = this.queryQuadTree(bbox);
                } catch (TreeException e) {
                    throw new DataStoreException("Error querying index: " + e.getMessage());
                } catch (IOException e) {
                    throw new DataStoreException("Error querying index: " + e.getMessage());
                }
            }
        }

        final IndexedDbaseFileReader dbfR;
        //check if we need to open the dbf reader, no need when only geometry
        if(properties.size() == 1 && properties.get(0) instanceof GeometryDescriptor){
            dbfR = null;
        }else{
            dbfR = openDbfReader();
        }
        
        return new IndexedShapefileAttributeReader(properties, openShapeReader(read3D,resample), dbfR,
                goodRecs, ((goodRecs!=null)?goodRecs.iterator():null),resample);
    }


    protected IndexedShapefileAttributeReader getBBoxAttributesReader(final List<PropertyDescriptor> properties, 
            final Envelope bbox, final boolean loose, final Hints hints, final boolean read3D, final double[] res) throws DataStoreException {

        final double[] minRes = (double[]) hints.get(HintsPending.KEY_IGNORE_SMALL_FEATURES);

        CloseableCollection<ShpData> goodCollec = null;
        
        final ShxReader shx;
        try {
            shx = openIndexFile();
        } catch (IOException ex) {
            throw new DataStoreException("Error opening Shx file: " + ex.getMessage(), ex);
        }

        try {
            final QuadTree quadTree = openQuadTree();
            final DataReader<ShpData> dr = new IndexDataReader(shx);
            if (quadTree != null) {
                goodCollec = quadTree.search(dr,bbox,minRes);
            }

        } catch (Exception e) {
            throw new DataStoreException("Error querying index: " + e.getMessage());
        }
        final LazySearchCollection<ShpData> col = (LazySearchCollection) goodCollec;
        
        //check if we need to open the dbf reader, no need when only geometry
        final IndexedDbaseFileReader dbfR;
        if(properties.size() == 1 && properties.get(0) instanceof GeometryDescriptor){
            dbfR = null;
        }else{
            dbfR = openDbfReader();
        }

        return new IndexedBBoxShapefileAttributeReader(properties, 
                openShapeReader(read3D,res), dbfR, col,
                (LazyTyleSearchIterator.Buffered<ShpData>)col.bboxIterator(),bbox,loose,res, minRes);
    }

    /**
     * Uses the Fid index to quickly lookup the shp offset and the record number
     * for the list of fids
     * 
     * @param fids
     *                the fids of the features to find.  If the set is sorted by alphabet the performance is likely to be better.
     * @return a list of Data objects
     * @throws IOException
     * @throws TreeException
     */
    private CloseableCollection<ShpData> queryFidIndex(final Set<Identifier> idsSet) throws IOException {

        if (!indexUseable(FIX)) {
            return null;
        }

        final IndexedFidReader reader = new IndexedFidReader(shpFiles);
        final CloseableCollection<ShpData> records = new CloseableArrayList(idsSet.size());

        try {
            final ShxReader shx = openIndexFile();
            try {

                for (Identifier identifier : idsSet) {
                    String fid = identifier.toString();
                    long recno = reader.findFid(fid);
                    if (recno == -1){
                        if(getLogger().isLoggable(Level.FINEST)){
                            getLogger().finest("fid " + fid+ " not found in index, continuing with next queried fid...");
                        }
                        continue;
                    }
                    try {
                        ShpData data = new ShpData(
                                (int)(recno+1),
                                (long)shx.getOffsetInBytes((int) recno));
                        if(getLogger().isLoggable(Level.FINEST)){
                            getLogger().finest("fid " + fid+ " found for record #"
                                    + data.getValue(0) + " at index file offset "
                                    + data.getValue(1));
                        }
                        records.add(data);
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            } finally {
                shx.close();
            }
        } finally {
            reader.close();
        }

        return records;
    }

    /**
     * Returns true if the index for the given type exists and is useable.
     * 
     * @param indexType the type of index to check
     * @return true if the index for the given type exists and is useable.
     */
    public boolean indexUseable(final ShpFileType indexType) {
        if (shpFiles.isLocal()) {
            if (needsGeneration(indexType) || !shpFiles.exists(indexType)) {
                return false;
            }
        } else {

            ReadableByteChannel read = null;
            try {
                read = shpFiles.getReadChannel(indexType, this);
            } catch (IOException e) {
                return false;
            } finally {
                //todo replace by ARM in JDK 1.7
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException e) {
                        ShapefileDataStoreFactory.LOGGER.log(Level.WARNING,
                                "could not close stream", e);
                    }
                }
            }
        }

        return true;
    }

    final boolean needsGeneration(final ShpFileType indexType) {
        if (!shpFiles.isLocal())
            throw new IllegalStateException(
                    "This method only applies if the files are local and the file can be created");

        final URL indexURL = shpFiles.acquireRead(indexType, this);
        final URL shpURL = shpFiles.acquireRead(SHP, this);
        try {

            if (indexURL == null) {
                return true;
            }
            // indexes require both the SHP and SHX so if either or missing then
            // you don't need to index
            if (!shpFiles.exists(SHX) || !shpFiles.exists(SHP)) {
                return false;
            }

            final File indexFile = toFile(indexURL);
            final File shpFile = toFile(shpURL);
            final long indexLastModified = indexFile.lastModified();
            final long shpLastModified = shpFile.lastModified();
            final boolean shpChangedMoreRecently = indexLastModified < shpLastModified;
            return !indexFile.exists() || shpChangedMoreRecently;
        } finally {
            if (shpURL != null) {
                shpFiles.unlockRead(shpURL, this);
            }
            if (indexURL != null) {
                shpFiles.unlockRead(indexURL, this);
            }
        }
    }

    /**
     * Returns true if the indices already exist and do not need to be
     * regenerated or cannot be generated (IE isn't local).
     * 
     * @return true if the indices already exist and do not need to be regenerated.
     */
    public boolean isIndexed() {
        if (shpFiles.isLocal()) {
            return true;
        }
        return !needsGeneration(FIX) && !needsGeneration(treeType.shpFileType);
    }


    /**
     * QuadTree Query
     * 
     * @param bbox
     * 
     * @throws DataSourceException
     * @throws IOException
     * @throws TreeException DOCUMENT ME!
     */
    private CloseableCollection<ShpData> queryQuadTree(final Envelope bbox)
            throws DataStoreException, IOException, TreeException {
        CloseableCollection<ShpData> tmp = null;

        try {
            final QuadTree quadTree = openQuadTree();
            final DataReader dr = new IndexDataReader(openIndexFile());
            if ((quadTree != null) && !bbox.contains(quadTree.getRoot().getBounds(new Envelope()))) {
                tmp = quadTree.search(dr,bbox);

                if (tmp == null || !tmp.isEmpty())
                    return tmp;
            }
            if (quadTree != null) {
                quadTree.close();
            }
        } catch (Exception e) {
            throw new DataStoreException("Error querying QuadTree", e);
        }

        return null;
    }

    /**
     * Convenience method for opening a DbaseFileReader.
     * 
     * @return A new DbaseFileReader
     * @throws DataStoreException If an error occurs during creation.
     */
    @Override
    protected IndexedDbaseFileReader openDbfReader() throws DataStoreException {
        if (shpFiles.get(DBF) == null) {
            return null;
        }

        if (shpFiles.isLocal() && !shpFiles.exists(DBF)) {
            return null;
        }
        try {
            return ShpDBF.indexed(shpFiles, false, dbfCharset);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * Convenience method for opening a QuadTree index.
     * 
     * @return A new QuadTree
     * @throws StoreException
     */
    protected QuadTree openQuadTree() throws StoreException {
        return shpFiles.getQIX();
    }

    /**
     * Create a FeatureWriter for the given type name.
     * 
     * @param typeName The typeName of the FeatureType to write
     * @return A new FeatureWriter.
     * @throws IOException If the typeName is not available or some other error occurs.
     */
    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(final Name typeName, 
            final Filter filter, final Hints hints) throws DataStoreException {

        //will raise an error if it does not exist
        final SimpleFeatureType schema = (SimpleFeatureType) getFeatureType(typeName);

        //we read all properties
        final IndexedShapefileAttributeReader attReader = getAttributesReader(
                schema.getAttributeDescriptors(),Filter.INCLUDE,true,null);

        try{
            final FeatureReader<SimpleFeatureType, SimpleFeature> reader = createFeatureReader(attReader, schema, null);
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = new IndexedShapefileFeatureWriter(
                    typeName.getLocalPart(), shpFiles, attReader, reader, this, dbfCharset);
            return handleRemaining(writer, filter);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    
    @Override
    public org.opengis.geometry.Envelope getEnvelope(final Query query) throws DataStoreException {

        final Filter filter = query.getFilter();
        if (filter == Filter.INCLUDE || QueryUtilities.queryAll(query) ) {
            //use the generic envelope calculation
            return super.getEnvelope(query);
        }

        final Set<Identifier> fids = (Set<Identifier>) filter.accept(
                IdCollectorFilterVisitor.IDENTIFIER_COLLECTOR, new TreeSet<Identifier>(IDENTIFIER_COMPARATOR));

        final Set records = new HashSet();
        if (!fids.isEmpty()) {
            Collection<ShpData> recordsFound = null;
            try {
                recordsFound = queryFidIndex(fids);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
            if (recordsFound != null) {
                records.addAll(recordsFound);
            }
        }

        if (records.isEmpty()) return null;

        ShapefileReader reader = null;
        try {
            reader = new ShapefileReader(shpFiles, false, false,false);

            final JTSEnvelope2D ret = new JTSEnvelope2D(getFeatureType(getNames().iterator().next()).getCoordinateReferenceSystem());
            for(final Iterator iter = records.iterator(); iter.hasNext();) {
                final Data data = (Data) iter.next();
                reader.goTo(((Long) data.getValue(1)).intValue());
                final Record record = reader.nextRecord();
                ret.expandToInclude(record.minX,record.minY);
                ret.expandToInclude(record.maxX,record.maxY);
            }
            return ret;
        } catch(IOException ex){
            throw new DataStoreException(ex);
        } finally {
            //todo replace by ARM in JDK 1.7
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException ex) {
                    throw new DataStoreException(ex);
                }
            }
        }
    }

    /**
     * Builds the QuadTree index. Usually not necessary since reading features
     * will index when required
     * 
     * @param maxDepth depth of the tree. if < 0 then a best guess is made.
     * @throws TreeException
     */
    public void buildQuadTree(final int maxDepth) throws TreeException {
        if (shpFiles.isLocal()) {
            shpFiles.unloadIndexes();
            getLogger().fine("Creating spatial index for " + shpFiles.get(SHP));

            final ShapeFileIndexer indexer = new ShapeFileIndexer();
            indexer.setIdxType(IndexType.QIX);
            indexer.setShapeFileName(shpFiles);
            indexer.setMax(maxDepth);

            try {
                indexer.index(false, new NullProgressListener());
            } catch (MalformedURLException e) {
                throw new TreeException(e);
            } catch (LockTimeoutException e) {
                throw new TreeException(e);
            } catch (Exception e) {
                if (e instanceof TreeException) {
                    throw (TreeException) e;
                } else {
                    throw new TreeException(e);
                }
            }
        }
    }

}
