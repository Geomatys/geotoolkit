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

// Apache Lucene dependencies
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

// Geotoolkit dependencies
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.manager.NamedEnvelope;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.LuceneUtils;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import static org.geotoolkit.lucene.index.IndexLucene.LOGGER;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.geometry.MismatchedReferenceSystemException;

// Types dependencies
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * An abstract lucene Indexer used to create and writer lucene index.
 *
 * @author Mehdi Sidhoum
 * @author Guilhem Legal
 * @param <E> The object's type to insert in a document.
 * @module pending
 */
public abstract class AbstractIndexer<E> extends IndexLucene {

    protected static final String CORRUPTED_SINGLE_MSG = "CorruptIndexException while indexing document: ";
    protected static final String CORRUPTED_MULTI_MSG  = "CorruptIndexException while indexing document: ";
    protected static final String LOCK_SINGLE_MSG      = "LockObtainException while indexing document: ";
    protected static final String LOCK_MULTI_MSG       = "LockObtainException while indexing documents.";
    protected static final String IO_SINGLE_MSG        = "IOException while indexing document: ";
    
    /**
     * A flag use in child constructor.
     */
    private boolean needCreation;

    /**
     * A flag to stop the indexation going on
     */
    protected static boolean stopIndexing = false;

    /**
     * A list of services id
     */
    protected static final List<String> indexationToStop = new ArrayList<>();

    /**
     * Map of fieldName / Number type.
     */
    private final Map<String, String> numericFields = new HashMap<>();
    
    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with the specified analyzer.
     *
     * @param indexID
     * @param configDirectory
     * @param analyzer
     */
    public AbstractIndexer(final String indexID, final File configDirectory, final Analyzer analyzer) {
        super(analyzer);
        
        // we get the last index directory
        long maxTime = 0;
        File currentIndexDirectory = null;
        if (configDirectory != null && configDirectory.exists() && configDirectory.isDirectory()) {
            for (File indexDirectory : configDirectory.listFiles(new IndexDirectoryFilter(indexID))) {
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
            currentIndexDirectory = new File(configDirectory, indexID + "index-" + System.currentTimeMillis());
            currentIndexDirectory.mkdir();
            needCreation = true;
            setFileDirectory(currentIndexDirectory);
        } else {
            LOGGER.finer("Index already created.");
            deleteOldIndexDir(configDirectory, indexID, currentIndexDirectory.getName());
            // must be set before reading tree
            setFileDirectory(currentIndexDirectory);
            needCreation = false;
        }
        rTree = SQLRtreeManager.get(currentIndexDirectory, this);
    }

    /**
     * Build a new Indexer witch create an index in the specified directory,
     * with a Keyword analyzer.
     *
     * @param indexID
     * @param configDirectory
     */
    public AbstractIndexer(final String indexID, final File configDirectory) {
        this(indexID, configDirectory, null);
    }

    public boolean needCreation() {
        return needCreation;
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
    
    protected abstract Collection<String> getAllIdentifiers() throws IndexingException;

    protected abstract Iterator<String> getIdentifierIterator() throws IndexingException;

    protected abstract Iterator<E> getEntryIterator() throws IndexingException;

    protected abstract boolean useEntryIterator();
    
    protected abstract E getEntry(final String identifier) throws IndexingException;
    
    /**
     * Create a new Index with the specified list of object.
     *
     * @param toIndex objects to index.
     * @throws IndexingException
     */
    public void createIndex(final List<E> toIndex) throws IndexingException {
        LOGGER.log(logLevel, "Creating lucene index for please wait...");
        
        final long time = System.currentTimeMillis();
        int nbEntries = 0;
        try {
            final IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, analyzer);
            final IndexWriter writer     = new IndexWriter(LuceneUtils.getAppropriateDirectory(getFileDirectory()), conf);
            final String serviceID       = getServiceID();
            
            resetTree();
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

            // we store the numeric fields in a properties file int the index directory
            storeNumericFieldsFile();
            
        } catch (IOException | StoreIndexException ex) {
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
            final IndexWriterConfig conf       = new IndexWriterConfig(Version.LATEST, analyzer);
            final IndexWriter writer           = new IndexWriter(LuceneUtils.getAppropriateDirectory(getFileDirectory()), conf);
            final String serviceID             = getServiceID();

            resetTree();
            LOGGER.log(logLevel, "starting indexing...");

            if (useEntryIterator()) {
                final Iterator<E> entries = getEntryIterator();
                while (entries.hasNext()) {
                    if (!stopIndexing && !indexationToStop.contains(serviceID)) {
                        
                        final E entry = entries.next();
                        indexDocument(writer, entry);
                        nbEntries++;
                        
                    } else {
                         LOGGER.info("Index creation stopped after " + (System.currentTimeMillis() - time) + " ms for service:" + serviceID);
                         stopIndexation(writer, serviceID);
                         return;
                    }
                }
                if (entries instanceof CloseableIterator) {
                    ((CloseableIterator)entries).close();
                }
            } else {
                final Iterator<String> identifiers = getIdentifierIterator();
                while (identifiers.hasNext()) {
                    final String identifier = identifiers.next();
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
                if (identifiers instanceof CloseableIterator) {
                    ((CloseableIterator)identifiers).close();
                }
            }
            // writer.optimize(); no longer justified
            writer.close();
            
            // we store the numeric fields in a properties file int the index directory
            storeNumericFieldsFile();

        } catch (IOException | StoreIndexException ex) {
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
     * @param meta The object to index.
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
     * @param meta The object to index.
     */
    public void indexDocument(final E meta) {
        try {
            final IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
            final IndexWriter writer = new IndexWriter(LuceneUtils.getAppropriateDirectory(getFileDirectory()), config);

            final int docId = writer.maxDoc();
            //adding the document in a specific model. in this case we use a MDwebDocument.
            writer.addDocument(createDocument(meta, docId));
            LOGGER.log(Level.FINER, "Metadata: {0} indexed", getIdentifier(meta));
            writer.close();
            if (rTree != null) {
                rTree.getTreeElementMapper().flush();
                rTree.flush();
            }

        } catch (IndexingException | StoreIndexException ex) {
            LOGGER.log(Level.WARNING, "Error while indexing single document", ex);
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
            final Directory dir   = LuceneUtils.getAppropriateDirectory(getFileDirectory());
            final Term t          = new Term("id", identifier);
            final TermQuery query = new TermQuery(t);
            LOGGER.log(logLevel, "Term query:{0}", query);

            // look for DOC ID for R-Tree removal
            final NamedEnvelope env = new NamedEnvelope(getTreeCrs(), identifier);
            final TreeElementMapper<NamedEnvelope> mapper = rTree.getTreeElementMapper();
            final int treeID = mapper.getTreeIdentifier(env);
            if (treeID != -1) {
                final NamedEnvelope realEnv = mapper.getObjectFromTreeIdentifier(treeID);
                boolean removed = rTree.remove(realEnv);
                if (!removed) {
                    LOGGER.log(Level.WARNING, "unable to remove envelope for:{0}", identifier);
                } else {
                    //remove from mapper
                    mapper.setTreeIdentifier(null, treeID);
                    mapper.flush();
                    rTree.flush();
                }
            }
            
            final IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
            final IndexWriter writer       = new IndexWriter(dir, config);
            writer.deleteDocuments(query);
            LOGGER.log(logLevel, "Metadata: {0} removed from the index", identifier);

            writer.commit();
            writer.close();
            
        } catch (CorruptIndexException ex) {
            LOGGER.log(Level.WARNING, "CorruptIndexException while indexing document: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IOException while indexing document: " + ex.getMessage(), ex);
        } catch (StoreIndexException ex) {
            LOGGER.log(Level.WARNING, "StoreIndexException while indexing document: " + ex.getMessage(), ex);
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
        final Polygon[] polygons = LuceneUtils.getPolygons(minx, maxx, miny, maxy, srid);
        Geometry geom;
        if (polygons.length == 1) {
            geom = polygons[0];
        } else if (polygons.length > 1 ){
            geom = LuceneUtils.GF.createGeometryCollection(polygons);
            geom.setSRID(srid);
        } else {
            return;
        }
        addGeometry(doc, geom, getTreeCrs());
    }

    /**
     * Add a geometric field with a JTS geometry in the specified lucene document.
     * @param doc The lucene document currently building.
     * @param geom A JTS geometry
     */
    public NamedEnvelope addGeometry(final Document doc, final Geometry geom, final CoordinateReferenceSystem crs) {
        NamedEnvelope namedBound = null;
        try {
            final String id = doc.get("id");
            namedBound      = LuceneUtils.getNamedEnvelope(id, geom, crs);
            rTree.insert(namedBound);
            rTree.getTreeElementMapper().flush();
            rTree.flush();
        } catch (TransformException | FactoryException | MismatchedReferenceSystemException | StoreIndexException | IOException ex) {
            LOGGER.log(Level.WARNING, "Unable to insert envelope in R-Tree.", ex);
        }
        doc.add(new StoredField(LuceneOGCFilter.GEOMETRY_FIELD_NAME,WKBUtils.toWKBwithSRID(geom)));
        return namedBound;
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

