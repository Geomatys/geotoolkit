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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.util.FileUtilities;

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
        } else {
            LOGGER.log(logLevel, "Index already created.");
            deleteOldIndexDir(configDirectory, serviceID, currentIndexDirectory.getName());
            create = false;
        }
        
        setFileDirectory(currentIndexDirectory);
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
    
    /**
     * Create a new Index.
     *
     * @throws IndexingException
     */
    public abstract void createIndex() throws IndexingException;

    /**
     * Create a new Index with the specified list of object.
     *
     * @throws IndexingException
     */
    public abstract void createIndex(List<E> toIndex) throws IndexingException;

    
   /**
     * Index a document from the specified object with the specified index writer.
     * Used when indexing in line many document.
     *
     * @param writer An Lucene index writer.
     * @param object The object to index.
     */
    public void indexDocument(final IndexWriter writer, final E meta) {
        try {
            //adding the document in a specific model. in this case we use a MDwebDocument.
            writer.addDocument(createDocument(meta));
            LOGGER.log(Level.FINER, "Metadata: {0} indexed", getIdentifier(meta));

        } catch (IndexingException ex) {
            LOGGER.log(Level.WARNING, "indexingException " +ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, IO_SINGLE_MSG + ex.getMessage(), ex);
        }
    }

    /**
     * This method add to index of lucene a new document.
     * (implements AbstractIndex.indexDocument() )
     *
     * @param object The object to index.
     */
    public void indexDocument(final E meta) {
        try {
            final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_31, analyzer);
            final IndexWriter writer = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), config);

            //adding the document in a specific model. in this case we use a MDwebDocument.
            writer.addDocument(createDocument(meta));
            LOGGER.log(Level.FINER, "Metadata: {0} indexed", getIdentifier(meta));
            writer.optimize();
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
    
    /**
     * This method remove index of lucene a document identified by identifier.
     *
     * @param identifier
     */
    public void removeDocument(final String identifier) {
        try {
            final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_31, analyzer);
            final IndexWriter writer = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), config);

            final Term t          = new Term("id", identifier);
            final TermQuery query = new TermQuery(t);
            LOGGER.log(logLevel, "Term query:{0}", query);

            writer.deleteDocuments(query);
            LOGGER.log(logLevel, "Metadata: {0} removed from the index", identifier);

            writer.commit();
            writer.optimize();
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
    protected abstract Document createDocument(E object) throws IndexingException;


    
    private final Coordinate[] coords = new Coordinate[]{
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0),
        new Coordinate(0, 0)
    };
    private final CoordinateSequence points = new CoordinateArraySequence(coords);
    private final LinearRing ring           = new LinearRing(points, GF);
    private final Polygon polygon           = new Polygon(ring, new LinearRing[0],GF);
    

    /**
     * Encode bounding box coordinate in a byte array.
     * 
     * @param minx minimal X coordinate.
     * @param maxx maximal X coordinate.
     * @param miny minimal Y coordinate.
     * @param maxy maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     * @return
     */
    protected byte[] toBytes(final double minx, final double maxx, final double miny, final double maxy, final int srid){
        coords[0].x = minx;
        coords[0].y = miny;
        coords[1].x = minx;
        coords[1].y = maxy;
        coords[2].x = maxx;
        coords[2].y = maxy;
        coords[3].x = maxx;
        coords[3].y = miny;
        coords[4].x = minx;
        coords[4].y = miny;
        polygon.setSRID(srid);
        return WKBUtils.toWKBwithSRID(polygon);
    }

    /**
     * Return a JTS polygon from bounding box coordinate.
     * 
     * @param minx minimal X coordinate.
     * @param maxx maximal X coordinate.
     * @param miny minimal Y coordinate.
     * @param maxy maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     */
    protected Polygon getPolygon(final double minx, final double maxx, final double miny, final double maxy, final int srid){
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
     * Add a geometric field with a boundingBox object in the specified document.
     * 
     * @param doc The lucene document currently building.
     * @param minx minimal X coordinate.
     * @param maxx maximal X coordinate.
     * @param miny minimal Y coordinate.
     * @param maxy maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     */
    protected void addBoundingBox(final Document doc, final double minx, final double maxx, final double miny, final double maxy, final int srid) {
        addGeometry(doc, toBytes(minx, maxx, miny, maxy,srid));
    }

    /**
     * Add a geometric field with a Multi-boundingBox object in the specified lucene document.
     *
     * @param doc The lucene document currently building.
     * @param minx a list of minimal X coordinate.
     * @param maxx a list of maximal X coordinate.
     * @param miny a list of minimal Y coordinate.
     * @param maxy a list of maximal Y coordinate.
     * @param srid coordinate spatial reference identifier.
     */
    protected void addMultipleBoundingBox(final Document doc, final List<Double> minx, final List<Double> maxx, final List<Double> miny, final List<Double> maxy, final int srid) {
        final Polygon[] polygons = new Polygon[minx.size()];
        for (int i = 0; i < minx.size(); i++) {
            polygons[i] = getPolygon(minx.get(i), maxx.get(i), miny.get(i), maxy.get(i),srid);
        }
        final GeometryCollection geom = GF.createGeometryCollection(polygons);
        addGeometry(doc, geom);
    }

    /**
     * Add a geometric field with a JTS geometry in the specified lucene document.
     * @param doc The lucene document currently building.
     * @param geom A JTS geometry
     */
    public static void addGeometry(final Document doc, final Geometry geom) {
        addGeometry(doc, WKBUtils.toWKBwithSRID(geom));
    }

     /**
     * Add a geometric field with a geometry byte encoded in the specified lucene document.
     * @param doc The lucene document currently building.
     * @param geom A geometry byte encoded.
     */
    public static void addGeometry(final Document doc, final byte[] geom) {
        doc.add(new Field(LuceneOGCFilter.GEOMETRY_FIELD_NAME,geom));
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

    /**
     * A file filter to retrieve all the index directory in a specified directory.
     *
     * @author Guilhem Legal (Geomatys)
     */
    public static class IndexDirectoryFilter implements FilenameFilter {

        /**
         * The service ID.
         */
        private final String prefix;

        public IndexDirectoryFilter(final String id) {
            if (id != null) {
                prefix = id;
            } else {
                prefix = "";
            }
        }

        /**
         * Return true if the specified file is a directory and if its name start with the serviceID + 'index-'.
         *
         * @param dir The current directory explored.
         * @param name The name of the file.
         * @return True if the specified file in the current directory match the conditions.
         */
        @Override
        public boolean accept(final File dir, final String name) {
            File f = new File(dir, name);
            if ("all".equals(prefix)) {
                return (name.indexOf("index-") != -1 && f.isDirectory());
            } else {
                return (name.startsWith(prefix + "index-") && f.isDirectory());
            }
        }
    }
}

