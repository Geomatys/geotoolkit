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
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import org.apache.lucene.store.SimpleFSDirectory;
import org.geotoolkit.io.wkb.WKBUtils;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.resources.NIOUtilities;

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
    public AbstractIndexer(String serviceID, File configDirectory, Analyzer analyzer) {
        super(analyzer);
        
        // we get the last index directory
        long maxTime = 0;
        File currentIndexDirectory = null;
        if (configDirectory != null && configDirectory.exists() && configDirectory.isDirectory()) {
            for (File indexDirectory : configDirectory.listFiles(new IndexDirectoryFilter(serviceID))) {
                String suffix = indexDirectory.getName();
                suffix = suffix.substring(suffix.indexOf('-') + 1);
                try {
                    long currentTime = Long.parseLong(suffix);
                    if (currentTime > maxTime) {
                        maxTime = currentTime;
                        currentIndexDirectory = indexDirectory;
                    }
                } catch(NumberFormatException ex) {
                    LOGGER.warning("Unable to parse the timestamp:" + suffix);
                }
            }
        }

        if (currentIndexDirectory == null) {
            currentIndexDirectory = new File(configDirectory, serviceID + "index-" + System.currentTimeMillis());
            create = true;
        } else {
            LOGGER.info("Index already created.");
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
    public AbstractIndexer(String serviceID, File configDirectory) {
        this(serviceID, configDirectory, null);
    }

    /**
     * Replace the precedent index directory by another pre-generated.
     */
    private void deleteOldIndexDir(File configDirectory, String serviceID, String currentDirName) {
        for (File indexDirectory : configDirectory.listFiles(new IndexDirectoryFilter(serviceID))) {
            String dirName = indexDirectory.getName();
            if (!dirName.equals(currentDirName)) {
                try {
                    NIOUtilities.deleteDirectory(indexDirectory);
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Unable to delete the directory:" + dirName, ex);
                }
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
    public abstract void createIndex(List<? extends Object> toIndex) throws IndexingException;

    /**
     * Index a document from the specified object with the specified index writer.
     * Used when indexing in line many document.
     *
     * @param writer An Lucene index writer.
     * @param object The object to index.
     */
    public abstract void indexDocument(IndexWriter writer, E object);

    /**
     * This method add to index of lucene a new document.
     * (implements AbstractIndex.indexDocument() )
     *
     * @param object The object to index.
     */
    public abstract void indexDocument(E object);

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
    public void removeDocument(String identifier) {
        try {
            final IndexWriter writer = new IndexWriter(new SimpleFSDirectory(getFileDirectory()), analyzer, false, MaxFieldLength.UNLIMITED);

            final Term t          = new Term("id", identifier);
            final TermQuery query = new TermQuery(t);
            LOGGER.info("Term query:" + query);


            writer.deleteDocuments(query);
            LOGGER.info("Metadata: " + identifier + " removed from the index");

            writer.commit();
            writer.optimize();
            writer.close();

        } catch (CorruptIndexException ex) {
            LOGGER.severe("CorruptIndexException while indexing document: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.severe("IOException while indexing document: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
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
    

    protected byte[] toBytes(double minx, double maxx, double miny, double maxy, int srid){
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


    protected void addBoundingBox(Document doc, double minx, double maxx, double miny, double maxy, int srid) {
        addGeometry(doc, toBytes(minx, maxx, miny, maxy,srid));
    }

    public static void addGeometry(Document doc, Geometry geom) {
        addGeometry(doc, WKBUtils.toWKBwithSRID(geom));
    }

    public static void addGeometry(Document doc, byte[] geom) {
        doc.add(new Field(LuceneOGCFilter.GEOMETRY_FIELD_NAME,geom,Field.Store.YES));
    }

    public abstract void destroy();

    /**
     * This method stop all the current indexation running
     */
    public static void stopIndexation(List<String> ids) {
        stopIndexing = true;
        if (ids != null) {
            for (String id: ids) {
                indexationToStop.add(id);
            }
        }
    }

    /**
     * Return the service ID of this index or "" if there is not explicit service ID.
     * @return service id
     */
    protected String getServiceID() {
        File directory       = getFileDirectory();
        String directoryName = directory.getName();
        String serviceId = "";
        if (directoryName.contains("index")) {
            serviceId = directoryName.substring(0, directoryName.indexOf("index"));

        }
        return serviceId;
    }

    /**
     * A file filter to retrieve all the index directory in a specified directory.
     *
     * @author Guilhem Legal
     */
    public static class IndexDirectoryFilter implements FilenameFilter {

        private String prefix;

        public IndexDirectoryFilter(String id) {
            prefix = "";
            if (id != null) {
                prefix = id;
            }
        }

        public boolean accept(File dir, String name) {
            File f = new File(dir, name);
            return (name.startsWith(prefix + "index-") && f.isDirectory());
        }
    }
}

