/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

// JTS dependencies
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

// Apache Lucene dependencies
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

// Geotoolkit dependencies
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.tree.NamedEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.FileUtilities;

// GeoAPI dependencies
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * An abstract lucene Indexer used to create and writer lucene index.
 *
 * @author Mehdi Sidhoum
 * @author Guilhem Legal
 * @module pending
 */
public abstract class AbstractIndexer<E> extends IndexLucene {

    protected static final int SRID_4326 = 4326;

    protected static final GeometryFactory GF = new GeometryFactory();
    protected static final String CORRUPTED_SINGLE_MSG = "CorruptIndexException while indexing document: ";
    protected static final String CORRUPTED_MULTI_MSG  = "CorruptIndexException while indexing document: ";
    protected static final String LOCK_SINGLE_MSG      = "LockObtainException while indexing document: ";
    protected static final String LOCK_MULTI_MSG       = "LockObtainException while indexing documents.";
    protected static final String IO_SINGLE_MSG        = "IOException while indexing document: ";
    
    /**
     * A flag use in child constructor.
     */
    protected boolean create;

    /**
     * A flag to stop the indexation going on
     */
    protected static boolean stopIndexing = false;

    /**
     * A list of services id
     */
    protected static final List<String> indexationToStop = new ArrayList<String>();

    /**
     * Map of fieldName / Number type.
     */
    private final Map<String, String> numericFields = new HashMap<String, String>();
    
    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with the specified analyzer.
     *
     * @param serviceID
     * @param configDirectory
     * @param analyzer
     */
    public AbstractIndexer(final String serviceID, final File configDirectory, final Analyzer analyzer) {
        super(analyzer);
        
        // we get the last index directory
        long maxTime = 0;
        File currentIndexDirectory = null;
        if (configDirectory != null && configDirectory.exists() && configDirectory.isDirectory()) {
            for (File indexDirectory : configDirectory.listFiles(new IndexDirectoryFilter(serviceID))) {
                String suffix = indexDirectory.getName();
                suffix = suffix.substring(suffix.lastIndexOf('-') + 1);
                try {
                    long currentTime = Long.parseLong(suffix);
                    if (currentTime > maxTime) {
                        maxTime = currentTime;
                        currentIndexDirectory = indexDirectory;
                    }
                } catch(NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "Unable to parse the timestamp:{0}", suffix);
                }
            }
        }

        if (currentIndexDirectory == null) {
            currentIndexDirectory = new File(configDirectory, serviceID + "index-" + System.currentTimeMillis());
            create = true;
            setFileDirectory(currentIndexDirectory);
        } else {
            LOGGER.log(logLevel, "Index already created.");
            deleteOldIndexDir(configDirectory, serviceID, currentIndexDirectory.getName());
            // must be set before reading tree
            setFileDirectory(currentIndexDirectory);
            create = false;
            try {
                readTree();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "IO exception while reading tree", ex);
            }
        }
    }

    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with a Keyword analyzer.
     *
     * @param serviceID
     * @param configDirectory
     */
    public AbstractIndexer(final String serviceID, final File configDirectory) {
        this(serviceID, configDirectory, null);
    }

    /**
     * Replace the precedent index directory by another pre-generated.
     */
    private void deleteOldIndexDir(final File configDirectory, final String serviceID, final String currentDirName) {
        for (File indexDirectory : configDirectory.listFiles(new IndexDirectoryFilter(serviceID))) {
            final String dirName = indexDirectory.getName();
            if (!dirName.equals(currentDirName)) {
                FileUtilities.deleteDirectory(indexDirectory);
            }
        }
        
    }
    
    protected abstract List<String> getAllIdentifiers() throws IndexingException;
    
    protected abstract E getEntry(final String identifier) throws IndexingException;
    
    /**
     * Create a new Index with the specified list of object.
     *
     * @throws IndexingException
     */
    public void createIndex(final List<E> toIndex) throws IndexingException {
        LOGGER.log(logLevel, "Creating lucene index for please wait...");
        
        final long time = System.currentTimeMillis();
        int nbEntries = 0;
        try {
            final IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            final IndexWriter writer     = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), conf);
            final String serviceID       = getServiceID();
            
            nbEntries = toIndex.size();
            for (E entry : toIndex) {
                if (!stopIndexing && !indexationToStop.contains(serviceID)) {
                    indexDocument(writer, entry);
                } else {
                     LOGGER.info("Index creation stopped after " + (System.currentTimeMillis() - time) + " ms for service:" + serviceID);
                     stopIndexation(writer, serviceID);
                     return;
                }
            }
            // writer.optimize(); no longer justified
            writer.close();

            // we store the R-tree (only if there is results)
            if (!toIndex.isEmpty()) {
                writeTree();
            }
            
            // we store the numeric fields in a properties file int the index directory
            storeNumericFieldsFile();
            
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, IO_SINGLE_MSG, ex);
        }
        LOGGER.log(logLevel, "Index creation process in " + (System.currentTimeMillis() - time) + " ms\n" +
                " documents indexed: " + nbEntries);
    }

    /**
     * Create a new Index.
     *
     * @throws IndexingException
     */
    public void createIndex() throws IndexingException {
        LOGGER.log(logLevel, "(light memory) Creating lucene index please wait...");

        final long time  = System.currentTimeMillis();
        int nbEntries      = 0;
        try {
            final IndexWriterConfig conf   = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            final IndexWriter writer       = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), conf);
            final String serviceID         = getServiceID();
            final List<String> identifiers = getAllIdentifiers();
            
            LOGGER.log(logLevel, "{0} entry to read.", identifiers.size());
            for (String identifier : identifiers) {
                if (!stopIndexing && !indexationToStop.contains(serviceID)) {
                    try {
                        final E entry = getEntry(identifier);
                        indexDocument(writer, entry);
                        nbEntries++;
                    } catch (IndexingException ex) {
                        LOGGER.warning("Metadata IO exeption while indexing metadata: " + identifier + " " + ex.getMessage() + "\nmove to next metadata...");
                    }
                } else {
                     LOGGER.info("Index creation stopped after " + (System.currentTimeMillis() - time) + " ms for service:" + serviceID);
                     stopIndexation(writer, serviceID);
                     return;
                }
            }
            // writer.optimize(); no longer justified
            writer.close();
            
            // we store the R-tree (obly if there is results)
            if (!identifiers.isEmpty()) {
                writeTree();
            }
            
            // we store the numeric fields in a properties file int the index directory
            storeNumericFieldsFile();

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE,IO_SINGLE_MSG + "{0}", ex.getMessage());
            throw new IndexingException("IOException while indexing documents:" + ex.getMessage(), ex);
        }
        LOGGER.log(logLevel, "Index creation process in " + (System.currentTimeMillis() - time) + " ms\n documents indexed: " + nbEntries + ".");
    }

    
   /**
     * Index a document from the specified object with the specified index writer.
     * Used when indexing in line many document.
     *
     * @param writer An Lucene index writer.
     * @param object The object to index.
     */
    public void indexDocument(final IndexWriter writer, final E meta) {
        try {
            final int docId = writer.maxDoc();
            //adding the document in a specific model. in this case we use a MDwebDocument.
            writer.addDocument(createDocument(meta, docId));
            LOGGER.log(Level.FINER, "Metadata: {0} indexed", getIdentifier(meta));

        } catch (IndexingException ex) {
            LOGGER.log(Level.WARNING, "indexingException " +ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, IO_SINGLE_MSG + ex.getMessage(), ex);
        }
    }

    /**
     * This method add to index of lucene a new document.
     *
     * @param object The object to index.
     */
    public void indexDocument(final E meta) {
        try {
            final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            final IndexWriter writer = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), config);

            final int docId = writer.maxDoc();
            //adding the document in a specific model. in this case we use a MDwebDocument.
            writer.addDocument(createDocument(meta, docId));
            LOGGER.log(Level.FINER, "Metadata: {0} indexed", getIdentifier(meta));
            writer.close();

        } catch (IndexingException ex) {
            LOGGER.log(Level.WARNING, "IndexingException " + ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, IO_SINGLE_MSG + ex.getMessage(), ex);
        }
    }


    /**
     * Return the identifier of the metadata
     * 
     * @param metadata
     * @return
     */
    protected abstract String getIdentifier(E metadata);
    
    /**
     * This method stop all the current indexation running
     */
    public static void stopIndexation() {
        stopIndexing = true;
    }
    
    private void stopIndexation(final IndexWriter writer, final String serviceID) throws IOException {
        // writer.optimize(); no longer justified
        writer.close();
        FileUtilities.deleteDirectory(getFileDirectory());
        if (indexationToStop.contains(serviceID)) {
            indexationToStop.remove(serviceID);
        }
        if (indexationToStop.isEmpty()) {
            stopIndexing = false;
        }
    }
    
    /**
     * Store the numeric fields in a properties file int the index directory
     */
    protected void storeNumericFieldsFile() {
        final File indexDirectory   = getFileDirectory();
        final File numericFieldFile = new File(indexDirectory, "numericFields.properties");
        final Properties prop       = new Properties();
        prop.putAll(numericFields);
        try {
            FileUtilities.storeProperties(prop, numericFieldFile);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Unable to store the numeric fields properties file.", ex);
        }
        
    }
    
    /**
     * Add a numeric fields to the current list.
     * 
     * @param fieldName
     * @param numberType 
     */
    protected void addNumericField(final String fieldName, final Character numberType) {
        if (numericFields.get(fieldName) == null) {
            numericFields.put(fieldName, numberType.toString());
        }
    }
    
    /**
     * This method remove index of lucene a document identified by identifier.
     *
     * @param identifier
     */
    public void removeDocument(final String identifier) {
        try {
            final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
            final IndexWriter writer = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), config);

            final Term t          = new Term("id", identifier);
            final TermQuery query = new TermQuery(t);
            LOGGER.log(logLevel, "Term query:{0}", query);

            writer.deleteDocuments(query);
            LOGGER.log(logLevel, "Metadata: {0} removed from the index", identifier);

            writer.commit();
            writer.close();

        } catch (CorruptIndexException ex) {
            LOGGER.log(Level.WARNING, "CorruptIndexException while indexing document: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IOException while indexing document: " + ex.getMessage(), ex);
        }
    }


    /**
    * Makes a document from the specified object.
    *
    * @param object an object to index.
    * @return A Lucene document.
    */
    protected abstract Document createDocument(E object, int docId) throws IndexingException;

    /**
     * Return a JTS polygon from bounding box coordinate.
     * 
     * @param minx minimal X coordinate.
     * @param maxx maximal X coordinate.
     * @param miny minimal Y coordinate.
     * @param maxy maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     */
    private Polygon getPolygon(final double minx, final double maxx, final double miny, final double maxy, final int srid){
        final Coordinate[] crds = new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0)};

        final CoordinateSequence pts = new CoordinateArraySequence(crds);
        final LinearRing rg          = new LinearRing(pts, GF);
        final Polygon poly           = new Polygon(rg, new LinearRing[0],GF);
        crds[0].x = minx;
        crds[0].y = miny;
        crds[1].x = minx;
        crds[1].y = maxy;
        crds[2].x = maxx;
        crds[2].y = maxy;
        crds[3].x = maxx;
        crds[3].y = miny;
        crds[4].x = minx;
        crds[4].y = miny;
        poly.setSRID(srid);
        return poly;
    }

    /**
     * Add a geometric field with on ore more boundingBox object in the specified lucene document.
     *
     * @param doc The lucene document currently building.
     * @param minx a list of minimal X coordinate.
     * @param maxx a list of maximal X coordinate.
     * @param miny a list of minimal Y coordinate.
     * @param maxy a list of maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     */
    protected void addBoundingBox(final Document doc, final List<Double> minx, final List<Double> maxx, final List<Double> miny, final List<Double> maxy, final int srid) {
        final Polygon[] polygons = new Polygon[minx.size()];
        for (int i = 0; i < minx.size(); i++) {
            polygons[i] = getPolygon(minx.get(i), maxx.get(i), miny.get(i), maxy.get(i),srid);
        }
        Geometry geom;
        if (polygons.length == 1) {
            geom = polygons[0];
        } else {
            geom = GF.createGeometryCollection(polygons);
            geom.setSRID(srid);
        }
        addGeometry(doc, geom, rTree);
    }

    /**
     * Add a geometric field with a JTS geometry in the specified lucene document.
     * @param doc The lucene document currently building.
     * @param geom A JTS geometry
     */
    public static void addGeometry(final Document doc, final Geometry geom, final Tree rTree) {
        if (rTree != null) {
            final Envelope jtsBound = geom.getEnvelopeInternal();
            try {
                final String name     =  doc.get("docid");
                final String epsgCode = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                final CoordinateReferenceSystem geomCRS = CRS.decode(epsgCode);
                final GeneralEnvelope bound = new GeneralEnvelope(geomCRS);
                bound.setRange(0, jtsBound.getMinX(), jtsBound.getMaxX());
                bound.setRange(1, jtsBound.getMinY(), jtsBound.getMaxY());
            
                // reproject to cartesian CRS
                final NamedEnvelope namedBound = new NamedEnvelope(Envelopes.transform(bound, rTree.getCrs()), name);
                rTree.insert(namedBound);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, "Unable to insert envelope in R-Tree.", ex);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, "Unable to insert envelope in R-Tree.", ex);
            }
        }
        doc.add(new Field(LuceneOGCFilter.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(geom)));
    }

    /**
     * Free the resources.
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * This method stop all the current indexation running
     */
    public static void stopIndexation(final List<String> ids) {
        stopIndexing = true;
        if (ids != null) {
            for (String id: ids) {
                indexationToStop.add(id);
            }
        }
    }

    /**
     * Return the service ID of this index or "" if there is not explicit service ID.
     * 
     * @return the service ID of this index or "" if there is not explicit service ID.
     */
    protected String getServiceID() {
        final File directory       = getFileDirectory();
        final String directoryName = directory.getName();
        final String serviceId;
        if (directoryName.contains("index")) {
            serviceId = directoryName.substring(0, directoryName.indexOf("index"));

        } else {
            serviceId = "";
        }
        return serviceId;
    }
}

