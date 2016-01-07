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
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.shapefile.FeatureIDReader;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.data.shapefile.ShapefileFeatureReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;
import static org.geotoolkit.data.shapefile.lock.ShpFiles.toFile;

import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader.Record;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.visitor.ExtractBoundsFilterVisitor;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.filter.visitor.IdCollectorFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.*;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.util.NullProgressListener;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * A FeatureStore implementation which allows reading and writing from Shapefiles.
 *
 * @author Ian Schneider
 * @author Tommaso Nolli
 * @author jesse eichar
 *
 * @module pending
 */
public class IndexedShapefileFeatureStore extends ShapefileFeatureStore {

    private static final Comparator<Identifier> IDENTIFIER_COMPARATOR = new Comparator<Identifier>(){
        @Override
        public int compare(Identifier o1, Identifier o2){
            return o1.toString().compareTo(o2.toString());
        }
    };

    IndexType treeType;
    final boolean useIndex;
    int maxDepth;

    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param uri The URL of the shp file to use for this DataSource.
     */
    public IndexedShapefileFeatureStore(final URI uri)
            throws MalformedURLException,DataStoreException {
        this(uri, null, false, true, IndexType.QIX,null);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param uri The URL of the shp file to use for this DataSource.
     * @param namespace DOCUMENT ME!
     */
    public IndexedShapefileFeatureStore(final URI uri, final String namespace)
            throws MalformedURLException,DataStoreException {
        this(uri, namespace, false, true, IndexType.QIX,null);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param uri The URL of the shp file to use for this DataSource.
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     */
    public IndexedShapefileFeatureStore(final URI uri, final boolean useMemoryMappedBuffer,
            final boolean createIndex) throws MalformedURLException,DataStoreException {
        this(uri, null, useMemoryMappedBuffer, createIndex, IndexType.QIX,null);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param uri The URL of the shp file to use for this DataSource.
     * @param namespace DOCUMENT ME!
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     * @param treeType The type of index used
     * @param dbfCharset {@link Charset} used to decode strings from the DBF
     *
     * @throws MalformedURLException
     */
    public IndexedShapefileFeatureStore(final URI uri, final String namespace, final boolean useMemoryMappedBuffer,
            final boolean createIndex, final IndexType treeType, final Charset dbfCharset)
            throws MalformedURLException,DataStoreException {
        super(uri, namespace, useMemoryMappedBuffer, dbfCharset);

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
            ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING, e
                    .getLocalizedMessage());
        }
        try {
            if (shpFiles.isLocal() && needsGeneration(FIX)) {
                //regenerate index
                IndexedFidWriter.generate(shpFiles);
            }
        } catch (IOException e) {
            ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING, e
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
    }

    /**
     * Use the spatial index if available and adds a small optimization: if no
     * attributes are going to be read, don't uselessly open and read the dbf
     * file.
     */
    @Override
    public FeatureReader getFeatureReader(final Query query)
            throws DataStoreException {
        final FeatureType originalSchema = getFeatureType();
        final GenericName              queryTypeName = query.getTypeName();
        final GenericName[]            queryPropertyNames = query.getPropertyNames();
        final Hints             queryHints = query.getHints();
        final double[]          queryRes = query.getResolution();
        Filter                  queryFilter = query.getFilter();

        //check if we must read the 3d values
        final CoordinateReferenceSystem reproject = query.getCoordinateSystemReproject();
        final boolean read3D = (reproject==null || CRS.getVerticalComponent(reproject, true) != null);

        if (queryFilter == Filter.EXCLUDE){
            return GenericEmptyFeatureIterator.createReader(originalSchema);
        }

        //find the properties we will read and return --------------------------
        List<PropertyDescriptor> readProperties;
        List<PropertyDescriptor> returnedProperties;

        if(queryPropertyNames == null){
            //return all properties
            readProperties = new ArrayList<>(originalSchema.getDescriptors());
            returnedProperties = readProperties;
        }else{
            //return only a subset of properties
            returnedProperties = new ArrayList<>(queryPropertyNames.length);
            for(GenericName n : queryPropertyNames){
                final AttributeDescriptor property = (AttributeDescriptor) originalSchema.getDescriptor(n);
                if(property == null){
                    throw new DataStoreException("Query requieres property : "+ n +
                        " which is not present in feature type :\n"+ originalSchema);
                }
                returnedProperties.add(property);
            }

            final FilterAttributeExtractor fae = new FilterAttributeExtractor();
            queryFilter.accept(fae, null);
            final Set<GenericName> filterPropertyNames = fae.getAttributeNameSet();
            if (filterPropertyNames.isEmpty()) {
                //filter do not requiere attributs
                readProperties = returnedProperties;
            } else {
                final Set<GenericName> attributes = new LinkedHashSet<>(filterPropertyNames);
                attributes.addAll(Arrays.asList(queryPropertyNames));
                readProperties = new ArrayList<>(attributes.size());
                for (GenericName n : attributes) {
                    final AttributeDescriptor property = (AttributeDescriptor) originalSchema.getDescriptor(n);
                    if (property == null) {
                        throw new DataStoreException("Query filter requieres property : " + n
                                + " which is not present in feature type :\n" + originalSchema);
                    }
                    if (!readProperties.contains(property)) {
                        readProperties.add(property);
                    }
                }
                //check if we read the same properties in different order
                if (readProperties.size() == returnedProperties.size() && readProperties.containsAll(returnedProperties)) {
                    //we avoid a useless retype iterator
                    readProperties = returnedProperties;
                }

            }
        }

        //create a reader ------------------------------------------------------
        final SimpleFeatureType readSchema;
        final FeatureReader reader;
        try {
            final GenericName[] readPropertyNames = new GenericName[readProperties.size()];
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
        qb.setSortBy(query.getSortBy());
        qb.setStartIndex(query.getStartIndex());
        qb.setMaxFeatures(query.getMaxFeatures());
        return handleRemaining(reader, qb.buildQuery());
    }

    protected FeatureReader createFeatureReader(
            final IndexedShapefileAttributeReader r, final SimpleFeatureType featureType, final Hints hints)
            throws MismatchedFeatureException, IOException,DataStoreException {

        final FeatureIDReader fidReader;
        if (!indexUseable(FIX)) {
            fidReader = new ShapeFIDReader(getName().tip().toString(), r);
        } else {
            fidReader = r.getLocker().getFIXReader(r);
        }

        return ShapefileFeatureReader.create(r, fidReader, featureType, hints);
    }

    private IndexedShapefileAttributeReader getAttributesReader(final List<? extends PropertyDescriptor> properties,
            final Filter filter, final boolean read3D, final double[] resample) throws DataStoreException{

        final AccessManager locker = shpFiles.createLocker();

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
                    goodRecs = this.queryQuadTree(locker,bbox);
                } catch (TreeException e) {
                    throw new DataStoreException("Error querying index: " + e.getMessage());
                } catch (IOException e) {
                    throw new DataStoreException("Error querying index: " + e.getMessage());
                }
            }
        }

        final boolean readDBF = !(properties.size()==1 && properties.get(0) instanceof GeometryDescriptor);
        final PropertyDescriptor[] atts = properties.toArray(new PropertyDescriptor[properties.size()]);
        try {
            return new IndexedShapefileAttributeReader(locker,atts,
                    read3D, useMemoryMappedBuffer,resample,
                    readDBF, dbfCharset, resample,
                    goodRecs, ((goodRecs!=null)?goodRecs.iterator():null));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }


    protected IndexedShapefileAttributeReader getBBoxAttributesReader(final List<PropertyDescriptor> properties,
            final Envelope bbox, final boolean loose, final Hints hints, final boolean read3D, final double[] res) throws DataStoreException {

        final AccessManager locker = shpFiles.createLocker();
        final double[] minRes = (double[]) hints.get(HintsPending.KEY_IGNORE_SMALL_FEATURES);

        CloseableCollection<ShpData> goodCollec = null;

        try {
            final QuadTree quadTree = openQuadTree();
            if (quadTree != null) {
                final ShxReader shx;
                try {
                    shx = locker.getSHXReader(useMemoryMappedBuffer);
                } catch (IOException ex) {
                    throw new DataStoreException("Error opening Shx file: " + ex.getMessage(), ex);
                }
                final DataReader<ShpData> dr = new IndexDataReader(shx);
                goodCollec = quadTree.search(dr,bbox,minRes);
            }

        } catch (Exception e) {
            throw new DataStoreException("Error querying index: " + e.getMessage());
        }
        final LazySearchCollection<ShpData> col = (LazySearchCollection) goodCollec;
        final LazyTyleSearchIterator.Buffered<ShpData> ite = (col!=null) ? col.bboxIterator() : null;

        //check if we need to open the dbf reader, no need when only geometry
        final boolean readDBF = !(properties.size()==1 && properties.get(0) instanceof GeometryDescriptor);
        final PropertyDescriptor[] atts = properties.toArray(new PropertyDescriptor[properties.size()]);
        try {
            return new IndexedBBoxShapefileAttributeReader(locker,atts,
                    read3D, useMemoryMappedBuffer,res,readDBF, dbfCharset,
                    minRes,col, ite, bbox,loose,minRes);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
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

        final AccessManager locker = shpFiles.createLocker();

        final IndexedFidReader reader = locker.getFIXReader(null);
        final CloseableCollection<ShpData> records = new CloseableArrayList(idsSet.size());

        try {
            final ShxReader shx = locker.getSHXReader(useMemoryMappedBuffer);
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
                read = shpFiles.getReadChannel(indexType);
            } catch (IOException e) {
                return false;
            } finally {
                //todo replace by ARM in JDK 1.7
                if (read != null) {
                    try {
                        read.close();
                    } catch (IOException e) {
                        ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING,
                                "could not close stream", e);
                    }
                }
            }
        }

        return true;
    }

    final boolean needsGeneration(final ShpFileType indexType) {
        if (!shpFiles.isLocal()){
            throw new IllegalStateException(
                    "This method only applies if the files are local and the file can be created");
        }

        final URI indexURI = shpFiles.getURI(indexType);
        final URI shpURI = shpFiles.getURI(SHP);

        if (indexURI == null) {
            return true;
        }
        // indexes require both the SHP and SHX so if either or missing then
        // you don't need to index
        if (!shpFiles.exists(SHX) || !shpFiles.exists(SHP)) {
            return false;
        }

        final File indexFile = toFile(indexURI);
        final File shpFile = toFile(shpURI);
        final long indexLastModified = indexFile.lastModified();
        final long shpLastModified = shpFile.lastModified();
        final boolean shpChangedMoreRecently = indexLastModified < shpLastModified;
        return !indexFile.exists() || shpChangedMoreRecently;
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
    private CloseableCollection<ShpData> queryQuadTree(final AccessManager locker, final Envelope bbox)
            throws DataStoreException, IOException, TreeException {
        CloseableCollection<ShpData> tmp = null;

        try {
            final QuadTree quadTree = openQuadTree();
            final DataReader dr = new IndexDataReader(locker.getSHXReader(useMemoryMappedBuffer));
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
    public FeatureWriter getFeatureWriter(final GenericName typeName,
            final Filter filter, final Hints hints) throws DataStoreException {

        //will raise an error if it does not exist
        final SimpleFeatureType schema = (SimpleFeatureType) getFeatureType(typeName);

        //we read all properties
        final IndexedShapefileAttributeReader attReader = getAttributesReader(
                schema.getAttributeDescriptors(),Filter.INCLUDE,true,null);

        try{
            final FeatureReader reader = createFeatureReader(attReader, schema, null);
            FeatureWriter writer = new IndexedShapefileFeatureWriter(
                    typeName.tip().toString(), shpFiles, attReader, reader, this, dbfCharset);
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

        final AccessManager locker = shpFiles.createLocker();

        ShapefileReader reader = null;
        try {
            reader = locker.getSHPReader(false, false, false, null);

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
            } catch (Exception e) {
                if (e instanceof TreeException) {
                    throw (TreeException) e;
                } else {
                    throw new TreeException(e);
                }
            }
        }
    }

    @Override
    public void createFeatureType(GenericName typeName, FeatureType featureType) throws DataStoreException {
        super.createFeatureType(typeName, featureType);

        //generate proper indexes
        try {
            if (shpFiles.isLocal() && useIndex
                    && needsGeneration(treeType.shpFileType)) {
                createSpatialIndex();
            }
        } catch (IOException e) {
            this.treeType = IndexType.NONE;
            ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING, e.getLocalizedMessage());
        }
        try {
            if (shpFiles.isLocal() && needsGeneration(FIX)) {
                //regenerate index
                IndexedFidWriter.generate(shpFiles);
            }
        } catch (IOException e) {
            ShapefileFeatureStoreFactory.LOGGER.log(Level.WARNING, e.getLocalizedMessage());
        }

    }



}
