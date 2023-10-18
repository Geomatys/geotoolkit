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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.shapefile.FeatureIDReader;
import org.geotoolkit.data.shapefile.ShapefileFeatureReader;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.data.shapefile.fix.IndexedFidReader;
import org.geotoolkit.data.shapefile.fix.IndexedFidWriter;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFileType;
import static org.geotoolkit.data.shapefile.lock.ShpFileType.*;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader.Record;
import org.geotoolkit.data.shapefile.shx.ShxReader;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.visitor.ExtractBoundsFilterVisitor;
import org.geotoolkit.filter.visitor.FilterAttributeExtractor;
import org.geotoolkit.filter.visitor.IdCollectorFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.quadtree.*;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.storage.feature.query.QueryUtilities;
import org.geotoolkit.util.NullProgressListener;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;


/**
 * A FeatureStore implementation which allows reading and writing from Shapefiles.
 *
 * @author Ian Schneider
 * @author Tommaso Nolli
 * @author jesse eichar
 *
 * @module
 */
public class IndexedShapefileFeatureStore extends ShapefileFeatureStore {

    private static final Comparator<ResourceId> IDENTIFIER_COMPARATOR = new Comparator<ResourceId>() {
        @Override
        public int compare(ResourceId o1, ResourceId o2){
            return o1.getIdentifier().toString().compareTo(o2.getIdentifier().toString());
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
        this(uri, false, false, IndexType.QIX,null);
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
        this(uri, useMemoryMappedBuffer, createIndex, IndexType.QIX,null);
    }

    /**
     * Creates a new instance of ShapefileDataStore.
     *
     * @param uri The URL of the shp file to use for this DataSource.
     * @param useMemoryMappedBuffer enable/disable memory mapping of files
     * @param createIndex enable/disable automatic index creation if needed
     * @param treeType The type of index used
     * @param dbfCharset {@link Charset} used to decode strings from the DBF
     */
    public IndexedShapefileFeatureStore(final URI uri, final boolean useMemoryMappedBuffer,
            final boolean createIndex, final IndexType treeType, final Charset dbfCharset)
            throws MalformedURLException,DataStoreException {
        super(uri, useMemoryMappedBuffer, dbfCharset);

        this.treeType = treeType;
        this.useIndex = treeType != IndexType.NONE;
        maxDepth = -1;
        try {
            if (shpFiles.isWritable() && createIndex
                    && needsGeneration(treeType.shpFileType)) {
                createSpatialIndex();
            }
        } catch (IOException e) {
            this.treeType = IndexType.NONE;
            ShapefileProvider.LOGGER.log(Level.WARNING, e
                    .getLocalizedMessage());
        }
        try {
            if (shpFiles.isWritable() && needsGeneration(FIX)) {
                //regenerate index
                IndexedFidWriter.generate(shpFiles);
            }
        } catch (IOException e) {
            ShapefileProvider.LOGGER.log(Level.WARNING, e
                    .getLocalizedMessage());
        }
    }

    /**
     * Forces the spatial index to be created
     */
    public final void createSpatialIndex() throws IOException {
        buildQuadTree(maxDepth);
    }

    /**
     * Use the spatial index if available and adds a small optimization: if no
     * attributes are going to be read, don't uselessly open and read the dbf
     * file.
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;



        final FeatureType   baseType = getFeatureType();
        final String        queryTypeName = gquery.getTypeName();
        final String[]      queryPropertyNames = gquery.getPropertyNames();
        final Hints         queryHints = gquery.getHints();
        double[]      queryRes = null;
        Filter              queryFilter = gquery.getSelection();

        //convert resolution
        Quantity<Length> linearResolution = gquery.getLinearResolution();
        if (linearResolution != null && linearResolution.getUnit().isCompatible(Units.METRE)) {

            //find the envelope we are working on
            org.opengis.geometry.Envelope srcEnvelope = null;
            final Filter<? super Feature> selection = gquery.getSelection();
            if (selection != null) {
                final Envelope base = new JTSEnvelope2D(getHeaderEnvelope().getCoordinateReferenceSystem());
                Envelope bbox = ExtractBoundsFilterVisitor.bbox(selection, base);
                if (bbox != null) srcEnvelope = new JTSEnvelope2D(bbox, FeatureExt.getCRS(getFeatureType()));
            } else {
                srcEnvelope = getHeaderEnvelope();
            }

            try {
                //create a fake envelope in metric crs
                double r = linearResolution.to(Units.METRE).getValue().doubleValue();
                org.opengis.geometry.Envelope env = Envelopes.transform(srcEnvelope, CRS.forCode("EPSG:3395"));
                queryRes = ReferencingUtilities.convertResolution(env, new double[]{r,r}, srcEnvelope.getCoordinateReferenceSystem());
            } catch (TransformException | FactoryException ex) {
                throw new DataStoreException("Failed to convert requested resolution", ex);
            }
        }

        //check if we must read the 3d values
        final boolean read3D = true;


        //find the properties we will read and return --------------------------
        final AttributeType idAttribute = (AttributeType) baseType.getProperty(AttributeConvention.IDENTIFIER);
        Set<AttributeType> readProperties;
        Set<PropertyType> returnedProperties;

        if(queryPropertyNames == null){
            //return all properties. Note : preserve order by using a linked set implementation
            readProperties = new LinkedHashSet<>(getAttributes(baseType,true));
            returnedProperties = new LinkedHashSet<>((Collection)baseType.getProperties(true));
        }else{
            //return only a subset of properties. Note : preserve order by using a linked set implementation
            readProperties = new LinkedHashSet<>(queryPropertyNames.length);
            returnedProperties = new LinkedHashSet<>(queryPropertyNames.length);
            for(String n : queryPropertyNames){
                final PropertyType cdt = baseType.getProperty(n);
                if (cdt instanceof AttributeType) {
                    readProperties.add((AttributeType) cdt);
                } else if (cdt instanceof AbstractOperation) {
                    final Set<String> deps = ((AbstractOperation)cdt).getDependencies();
                    for (String dep : deps) readProperties.add((AttributeType) baseType.getProperty(dep));
                }
                returnedProperties.add(cdt);
            }

            //add filter properties
            final FilterAttributeExtractor fae = new FilterAttributeExtractor();
            fae.visit(queryFilter, null);
            final Set<GenericName> filterPropertyNames = fae.getAttributeNameSet();
            for (GenericName n : filterPropertyNames) {
                final PropertyType cdt = baseType.getProperty(n.toString());
                if (cdt instanceof AttributeType) {
                    readProperties.add((AttributeType) cdt);
                } else if (cdt instanceof AbstractOperation) {
                    final Set<String> deps = ((AbstractOperation)cdt).getDependencies();
                    for (String dep : deps) readProperties.add((AttributeType) baseType.getProperty(dep));
                }
            }
        }
        final Set<PropertyType> allProperties = new LinkedHashSet<>(returnedProperties);
        allProperties.addAll(readProperties);

        //create a reader ------------------------------------------------------
        final FeatureType readType;
        final FeatureReader reader;
        try {
            final GenericName[] readPropertyNames = new GenericName[allProperties.size()];
            int i=0;
            for(PropertyType prop : allProperties){
                readPropertyNames[i++] = prop.getName();
            }
            readType = FeatureTypeExt.createSubType(baseType,readPropertyNames);

            if (queryFilter.getOperatorType() == SpatialOperatorName.BBOX) {
                //in case we have a BBOX filter only, which is very commun, we can speed
                //the process by relying on the quadtree estimations
                final Envelope base = new JTSEnvelope2D(getHeaderEnvelope().getCoordinateReferenceSystem());
                final Envelope bbox = ExtractBoundsFilterVisitor.bbox(queryFilter, base);
                final boolean loose = (queryFilter instanceof LooseBBox);
                queryFilter = Filter.include();
                final List<AttributeType> attsProperties = new ArrayList<>(readProperties);
                attsProperties.remove(idAttribute);
                reader = createFeatureReader(
                        getBBoxAttributesReader(attsProperties, bbox, loose, queryHints,read3D,queryRes),
                        readType, queryHints);

            } else if (queryFilter instanceof ResourceId && ((ResourceId) queryFilter).getIdentifier() == null) {
                // in case we have an empty id set (TODO: should never happen, maybe we should remove this case).
                return FeatureStreams.emptyReader(getFeatureType());
            } else {
                final List<AttributeType> attsProperties = new ArrayList<>(readProperties);
                attsProperties.remove(idAttribute);
                reader = createFeatureReader(
                    getAttributesReader(attsProperties, queryFilter,read3D,queryRes),
                    readType, queryHints);
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        //handle remaining query parameters ------------------------------------
        final org.geotoolkit.storage.feature.query.Query qb = new org.geotoolkit.storage.feature.query.Query(queryTypeName);
        if(readProperties.equals(returnedProperties)){
            qb.setProperties(queryPropertyNames);
        }
        qb.setSelection(queryFilter);
        qb.setHints(queryHints);
        qb.setSortBy(gquery.getSortBy());
        qb.setOffset(gquery.getOffset());
        gquery.getLimit().ifPresent(qb::setLimit);
        return FeatureStreams.subset(reader, qb);
    }

    protected FeatureReader createFeatureReader(
            final IndexedShapefileAttributeReader r, final FeatureType featureType, final Hints hints)
            throws MismatchedFeatureException, IOException,DataStoreException {

        final FeatureIDReader fidReader;
        if (!indexUseable(FIX)) {
            fidReader = new ShapeFIDReader(getName().tip().toString(), r);
        } else {
            fidReader = r.getLocker().getFIXReader(r);
        }

        return ShapefileFeatureReader.create(r, fidReader, featureType, hints);
    }

    private IndexedShapefileAttributeReader getAttributesReader(final List<? extends AttributeType> properties,
            final Filter filter, final boolean read3D, final double[] resample) throws DataStoreException{

        final AccessManager locker = shpFiles.createLocker();

        CloseableCollection<ShpData> goodRecs = null;
        if (filter instanceof ResourceId && shpFiles.isWritable() && shpFiles.exists(FIX)) {
            final ResourceId fidFilter = (ResourceId) filter;

            final TreeSet<String> idsSet = new TreeSet<>();
            idsSet.add(fidFilter.getIdentifier());
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
                bbox = ExtractBoundsFilterVisitor.bbox(filter, null);
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

        final boolean readDBF = !(properties.size()==1 && Geometry.class.isAssignableFrom(properties.get(0).getValueClass()));
        final AttributeType[] atts = properties.toArray(new AttributeType[properties.size()]);
        try {
            return new IndexedShapefileAttributeReader(locker,atts,
                    read3D, useMemoryMappedBuffer,resample,
                    readDBF, dbfCharset, resample,
                    goodRecs, ((goodRecs!=null)?goodRecs.iterator():null));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    protected IndexedShapefileAttributeReader getBBoxAttributesReader(final List<AttributeType> properties,
            final Envelope bbox, final boolean loose, final Hints hints, final boolean read3D, final double[] res) throws DataStoreException {

        final AccessManager locker = shpFiles.createLocker();
        final double[] minRes = (double[]) hints.get(Hints.KEY_IGNORE_SMALL_FEATURES);

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
        final boolean readDBF = !(properties.size()==1 && Geometry.class.isAssignableFrom(properties.get(0).getValueClass()));
        final AttributeType[] atts = properties.toArray(new AttributeType[properties.size()]);
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
     */
    private CloseableCollection<ShpData> queryFidIndex(final Set<String> idsSet) throws IOException {
        if (!indexUseable(FIX)) {
            return null;
        }
        final AccessManager locker = shpFiles.createLocker();

        final IndexedFidReader reader = locker.getFIXReader(null);
        final CloseableCollection<ShpData> records = new CloseableArrayList(idsSet.size());

        try {
            final ShxReader shx = locker.getSHXReader(useMemoryMappedBuffer);
            try {
                for (String fid : idsSet) {
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
    boolean indexUseable(final ShpFileType indexType) throws IOException {
        if (shpFiles.isWritable()) {
            if (needsGeneration(indexType) || !shpFiles.exists(indexType)) {
                return false;
            }
        } else {

            ReadableByteChannel read = null;
            try (ReadableByteChannel reader = shpFiles.getReadChannel(indexType)) {
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    final boolean needsGeneration(final ShpFileType indexType) throws IOException {
        if (!shpFiles.isWritable()){
            throw new IllegalStateException(
                    "This method only applies if the files are local and the file can be created");
        }

        // indexes require both the SHP and SHX so if either or missing then
        // you don't need to index
        if (!shpFiles.exists(SHX) || !shpFiles.exists(SHP)) {
            return false;
        } else if (!shpFiles.exists(indexType)) {
            return true;
        }

        final Path indexFile = shpFiles.getPath(indexType);
        final Path shpFile = shpFiles.getPath(SHP);
        return Files.getLastModifiedTime(indexFile).compareTo(Files.getLastModifiedTime(shpFile)) < 0;
    }

    /**
     * QuadTree Query
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
     */
    protected QuadTree openQuadTree() throws StoreException {
        return shpFiles.getQIX();
    }

    /**
     * Create a FeatureWriter for the given type name.
     *
     * @param query The typeName of the FeatureType to write
     * @return A new FeatureWriter.
     * @throws DataStoreException If the typeName is not available or some other error occurs.
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;

        //will raise an error if it does not exist
        final FeatureType schema = getFeatureType(gquery.getTypeName());

        //we read all properties
        final IndexedShapefileAttributeReader attReader = getAttributesReader(
                getAttributes(schema,false),Filter.include(), true, null);

        try{
            final FeatureReader reader = createFeatureReader(attReader, schema, null);
            FeatureWriter writer = new IndexedShapefileFeatureWriter(
                    schema.getName().tip().toString(), shpFiles, attReader, reader, this, dbfCharset);
            return FeatureStreams.filter(writer, gquery.getSelection());
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }


    @Override
    public org.opengis.geometry.Envelope getEnvelope(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;

        final Filter filter = gquery.getSelection();
        if (filter == Filter.include() || QueryUtilities.queryAll(gquery) ) {
            //use the generic envelope calculation
            return super.getEnvelope(gquery);
        }

        final Set<String> fids = new TreeSet<>();
        IdCollectorFilterVisitor.ID_COLLECTOR.visit(filter, fids);

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

            final JTSEnvelope2D ret = new JTSEnvelope2D(FeatureExt.getCRS(getFeatureType(getNames().iterator().next().toString())));
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
     */
    public void buildQuadTree(final int maxDepth) throws TreeException {
        if (shpFiles.isWritable()) {
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
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        super.createFeatureType(featureType);

        //generate proper indexes
        try {
            if (shpFiles.isWritable() && useIndex
                    && needsGeneration(treeType.shpFileType)) {
                createSpatialIndex();
            }
        } catch (IOException e) {
            this.treeType = IndexType.NONE;
            ShapefileProvider.LOGGER.log(Level.WARNING, e.getLocalizedMessage());
        }
        try {
            if (shpFiles.isWritable() && needsGeneration(FIX)) {
                //regenerate index
                IndexedFidWriter.generate(shpFiles);
            }
        } catch (IOException e) {
            ShapefileProvider.LOGGER.log(Level.WARNING, e.getLocalizedMessage());
        }
    }
}
